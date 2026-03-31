package io.app.controller;

import io.app.dto.ApiResponse;
import io.app.dto.TaskDto;
import io.app.dto.TaskExecutionDto;
import io.app.model.Task;
import io.app.model.TaskStatus;
import io.app.service.TaskService;
import io.app.service.impl.CustomUserDetails;
import io.app.service.impl.TaskServiceImpl;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/task")
public class TaskController {
    private final TaskServiceImpl service;

    public TaskController(TaskServiceImpl service){
        this.service=service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse create(@RequestBody TaskDto task,
                              @AuthenticationPrincipal CustomUserDetails userDetails){
        return service.create(task,userDetails.getUserId());
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(value = "page",defaultValue = "0",required = false) int pageNo,
            @RequestParam(value = "size",defaultValue = "10",required = false) int pageSize){
        return ResponseEntity.ok(service.getAll(pageNo,pageSize));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(
            @PathVariable Long id,
            @RequestBody TaskDto taskDto){
        return ResponseEntity.ok(service.update(id,taskDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getById(@PathVariable Long id){
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/{id}/dependencies")
    public ResponseEntity<Set<TaskDto>> getDependencies(@PathVariable Long id){
        return ResponseEntity.ok(service.getDependencies(id));
    }

    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<ApiResponse> updateStatus(@PathVariable Long id,
                                                    @PathVariable TaskStatus status){
        return ResponseEntity.ok(service.updateStatus(id,status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteById(@PathVariable Long id){
        return ResponseEntity.ok(service.deleteTaskById(id));
    }

    @PostMapping("/{id}/dependencies/{dependencyId}")
    public ResponseEntity<ApiResponse> addDependency(@PathVariable("id") Long id,
                                                     @PathVariable("dependencyId") Long dependencyId){
        return ResponseEntity.ok(service.addDependency(id,dependencyId));
    }

    @DeleteMapping("/{id}/dependencies/{dependencyId}")
    public ResponseEntity<ApiResponse> removeDependency(@PathVariable("id") Long id,
                                                     @PathVariable("dependencyId") Long dependencyId){
        return ResponseEntity.ok(service.removeDependency(id,dependencyId));
    }

    @GetMapping("/search")
    public ResponseEntity<Set<TaskDto>> search(@RequestParam("keyword") String keyword){
        return ResponseEntity.ok(service.search(keyword));
    }

    @GetMapping("/{id}/blocked")
    public ResponseEntity<ApiResponse> checkBlockedNonBlocked(@PathVariable("id") Long id){
        return ResponseEntity.ok(service.checkTaskIsBlocked(id));
    }

    @GetMapping("/blocked")
    public ResponseEntity<Set<TaskDto>> allBlockedTask(){
        return ResponseEntity.ok(service.getBlockedTask());
    }


    @GetMapping("/{id}/execution-order")
    public ResponseEntity<TaskExecutionDto> getInExecutionOrder(@PathVariable("id") Long id){
        return ResponseEntity.ok(service.getExecutionOrderById(id));
    }


}
