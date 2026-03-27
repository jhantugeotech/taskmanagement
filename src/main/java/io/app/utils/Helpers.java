package io.app.utils;

import io.app.dto.TaskExecutionDto;
import io.app.model.Task;
import io.app.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@Slf4j
public class Helpers {
    @Autowired
    private TaskRepository repository;

    public boolean hasCircularDependency(Task currentTask, Task parentTask) {
        Set<Long> visited = new HashSet<>();
        return dfsOperation(parentTask, currentTask.getId(), visited);
    }

    private boolean dfsOperation(Task task, Long targetId, Set<Long> visited) {
        if (task == null) return false;

        if (task.getId().equals(targetId)) {
            return true;
        }

        if (visited.contains(task.getId())) {
            return false    ;
        }

        visited.add(task.getId());

        for (Task parent : task.getParentTask()) {
            if (dfsOperation(parent, targetId, visited)) {
                return true;
            }
        }


        return false;
    }


    public TaskExecutionDto executionOrderBuilder(Task task){
        return executeTask(task,new HashSet<>());
    }

    private TaskExecutionDto executeTask(Task task,HashSet<Long> visited){
        if (visited.contains(task.getId())){
            return null;
        }
        visited.add(task.getId());

        TaskExecutionDto dto=TaskExecutionDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .children(new ArrayList<>())
                .build();


        List<Task> children=repository.findTaskByParentId(task.getId());

        log.info("{} has {} children",task.getId(),children.size());

        for (Task childrenTask:children){
            TaskExecutionDto childDto=executeTask(childrenTask,visited);
            if (childDto!=null){
                dto.getChildren().add(childDto);
            }
        }

        return dto;
    }

}
