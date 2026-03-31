package io.app.security;

import io.app.repository.TaskRepository;
import org.springframework.stereotype.Component;

@Component("taskSecurityService")
public class TaskSecurityService {
    private final TaskRepository repository;
    public TaskSecurityService(TaskRepository repository){
        this.repository=repository;
    }

    public boolean canRead(Long taskId, Long ownerId){
        if (taskId==null || ownerId==null){
            return false;
        }
        if (!repository.existsByIdAndOwnerId(taskId,ownerId)){
            return false;
        }
        return true;
    }
}
