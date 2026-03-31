package io.app.service;

import io.app.dto.TaskDto;
import io.app.dto.ApiResponse;
import io.app.dto.TaskExecutionDto;
import io.app.model.Task;
import io.app.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

public interface TaskService {
    public ApiResponse create(TaskDto task,Long userId);
    public Page<TaskDto> getAll(Long ownerId,
                                TaskStatus status,
                                String search,
                                int pageNo,
                                int pageSize);
    public TaskDto getById(Long id);
    public TaskDto getById(Long id,Long userId);
    public ApiResponse update(Long id,TaskDto taskDto);
    public Set<TaskDto> getDependencies(Long id);
    public ApiResponse updateStatus(Long id, TaskStatus taskStatus);
    public ApiResponse deleteTaskById(Long id);
    public ApiResponse addDependency(Long id,Long dependencyId);
    public ApiResponse removeDependency(Long id,Long dependencyId);
//    public Set<TaskDto> search(String keyword);
    public ApiResponse checkTaskIsBlocked(Long id);
    public Set<TaskDto> getBlockedTask();
    public TaskExecutionDto getExecutionOrderById(Long id);
}
