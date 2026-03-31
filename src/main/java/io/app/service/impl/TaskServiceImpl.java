package io.app.service.impl;

import io.app.dto.ApiResponse;
import io.app.dto.TaskDto;
import io.app.dto.TaskExecutionDto;
import io.app.exception.BadRequestException;
import io.app.exception.ConflictException;
import io.app.exception.ResourceNotFoundException;
import io.app.mapper.ModelMapper;
import io.app.model.Task;
import io.app.model.TaskStatus;
import io.app.repository.TaskRepository;
import io.app.service.TaskService;
import io.app.utils.Helpers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {
    private final TaskRepository repository;
    private final Helpers helpers;

    public TaskServiceImpl(TaskRepository repository,Helpers helpers){
        this.repository=repository;
        this.helpers=helpers;
    }


    @Override
    @Transactional
    public ApiResponse create(TaskDto taskDto, Long userId) {
        validateTaskDto(taskDto);

        Task task = buildTask(taskDto, userId);

        task = repository.save(task);

        if (taskDto.getParentIds() != null && !taskDto.getParentIds().isEmpty()) {
            linkParentTasks(task, taskDto.getParentIds(), userId);
            task = repository.save(task); // Save after linking parents
        }

        return ApiResponse.builder()
                .message("Task Created Successfully")
                .status(true)
                .build();
    }

    @Override
    public Page<TaskDto> getAll(Long ownerId,
                               TaskStatus status,
                               String search,
                               int pageNo,
                               int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<Task> tasks;

        if (status != null && search != null) {
            tasks = repository.findByOwnerIdAndStatusAndTitleContainingIgnoreCase(
                    ownerId, status, search, pageable
            );
        } else if (status != null) {
            tasks = repository.findByOwnerIdAndStatus(ownerId, status, pageable);
        } else if (search != null) {
            tasks = repository.findByOwnerIdAndTitleContainingIgnoreCase(
                    ownerId, search, pageable
            );
        } else {
            tasks = repository.findByOwnerId(ownerId, pageable);
        }

        System.out.println("total task "+tasks.getContent().size());

        return tasks.map(ModelMapper::taskToDto);
    }

    @Override
    public TaskDto getById(Long id) {
        Task task=repository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Task Not Found"));
        return ModelMapper.taskToDto(task);
    }

    @Override
    public TaskDto getById(Long id, Long userId) {
        Task task=repository.findByIdAndOwnerId(id,userId)
                .orElseThrow(()->new ResourceNotFoundException("Task Not Fund"));
        return ModelMapper.taskToDto(task);
    }

    @Override
    @Transactional
    public ApiResponse update(Long id, TaskDto taskDto) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task Does Not Exist"));

        updateTaskFields(task, taskDto);

        repository.save(task);

        return ApiResponse.builder()
                .message("Task Updated Successfully")
                .status(true)
                .build();
    }


    @Override
    public Set<TaskDto> getDependencies(Long id) {
        boolean exists=repository.existsById(id);
        if (!exists){
            throw new ResourceNotFoundException("Task Not Found");
        }
        List<Task> tasks=repository.findDependencyTaskById(id);
        Set<TaskDto> result=tasks.stream().map(
                item->ModelMapper.taskToDto(item)
        ).collect(Collectors.toSet());
        return result;
    }

    @Override
    public ApiResponse updateStatus(Long id, TaskStatus taskStatus) {
        Task task=repository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Task Not Found"));
        for (Task t:task.getParentTask()){
            if (t.getStatus()!=TaskStatus.DONE){
                throw new BadRequestException(t.getTitle()+" is Not Completed Yet");
            }
        }
        task.setStatus(taskStatus);
        repository.save(task);
        return ApiResponse.builder()
                .message("Task Updated Successfully")
                .status(true)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse deleteTaskById(Long id) {
        Task task=repository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Task Not Found"));

        List<Task> subTask=repository.findTaskByParentId(task.getId());
        for (Task t:subTask){
            t.getParentTask().remove(task);
        }

        repository.delete(task);

        return ApiResponse.builder()
                .message("Task Deleted Successfully")
                .status(true)
                .build();
    }

    @Override
    public ApiResponse addDependency(Long id, Long dependencyId) {
        Task task=repository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Invalid Task"));
        Task dependency=repository.findById(dependencyId)
                .orElseThrow(()->new ResourceNotFoundException("Invalid Dependency"));
        if (task.getParentTask().contains(dependency)){
            throw new ConflictException("Already has dependency");
        }
        task.getParentTask().add(dependency);
        repository.save(task);
        return ApiResponse.builder()
                .message("Added the dependency")
                .status(true)
                .build();
    }

    @Override
    public ApiResponse removeDependency(Long id, Long dependencyId) {
        Task task=repository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Invalid Task"));
        Task dependency=repository.findById(dependencyId)
                .orElseThrow(()->new ResourceNotFoundException("Invalid Dependency"));
        if (!task.getParentTask().contains(dependency)){
            throw new ConflictException("No such dependency found");
        }
        task.getParentTask().remove(dependency);
        repository.save(task);
        return ApiResponse.builder()
                .message("Removed the Dependency")
                .status(true)
                .build();
    }

//    @Override
//    public Set<TaskDto> search(String keyword) {
//        Set<TaskDto> searchResult=repository.findByKeyword(keyword).stream()
//                .map(task->ModelMapper.taskToDto(task))
//                .collect(Collectors.toSet());
//        return searchResult;
//    }

    @Override
    public ApiResponse checkTaskIsBlocked(Long id) {
        List<Task> parentTask=repository.findDependencyTaskById(id);
        boolean isTaskNonBlocked=parentTask.stream().allMatch(item->item.getStatus()==TaskStatus.DONE);
        ApiResponse apiResponse=new ApiResponse();
        if (isTaskNonBlocked){
            apiResponse.setMessage("Task is not blocked");
        }else {
            apiResponse.setMessage("Task is blocked");
        }
        apiResponse.setStatus(!isTaskNonBlocked);
        return apiResponse;
    }

    @Override
    public Set<TaskDto> getBlockedTask() {
        List<Task> tasks=repository.findByStatusNotIn(Set.of(TaskStatus.DONE));

        Set<TaskDto> result=new HashSet<>();

        for (Task task: tasks){
            if (task.getParentTask()!=null && !task.getParentTask().isEmpty()){
                boolean flag=true;
                for (Task parent:task.getParentTask()){
                    if (parent.getStatus()!=TaskStatus.DONE){
                        flag=false;
                        break;
                    }
                }
                if (flag){
                    result.add(ModelMapper.taskToDto(task));
                }
            }
        }

        return result;
    }

    @Override
    public TaskExecutionDto getExecutionOrderById(Long id) {
        Task task=repository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Invalid Id"));

        return helpers.executionOrderBuilder(task);
    }



    private void validateTaskDto(TaskDto taskDto) {
        if (taskDto.getTitle() == null || taskDto.getTitle().isBlank()) {
            throw new BadRequestException("Title required");
        }
    }

    private Task buildTask(TaskDto taskDto, Long userId) {
        return Task.builder()
                .ownerId(userId)
                .title(taskDto.getTitle())
                .description(taskDto.getDescription())
                .build();
    }

    private void linkParentTasks(Task task, Set<Long> parentIds, Long userId) {
        Set<Task> parentTasks = new HashSet<>();
        for (Long parentId : parentIds) {
            Task parent = repository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Invalid Parent Task"));

            if (!parent.getOwnerId().equals(userId)) {
                throw new BadRequestException("Parent task must belong to the current user.");
            }

            if (helpers.hasCircularDependency(task, parent)) {
                throw new BadRequestException("Circular dependency detected");
            }

            parentTasks.add(parent);
        }
        task.setParentTask(parentTasks);
    }

    private void updateTaskFields(Task task, TaskDto taskDto) {
        if (taskDto.getTitle() != null && !taskDto.getTitle().isBlank()) {
            task.setTitle(taskDto.getTitle());
        }
        if (taskDto.getDescription() != null) {
            task.setDescription(taskDto.getDescription());
        }
    }

}
