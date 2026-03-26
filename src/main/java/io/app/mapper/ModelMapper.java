package io.app.mapper;

import io.app.dto.TaskDto;
import io.app.model.Task;

public class ModelMapper {
    public static TaskDto taskToDto(Task task){
        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .updatedAt(task.getUpdatedAt())
                .createdAt(task.getCreatedAt())
                .build();
    }
}
