package io.app.mapper;

import io.app.dto.TaskDto;
import io.app.dto.UserDto;
import io.app.model.Task;
import io.app.model.User;

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
    public static UserDto userToDto(User user){
        UserDto userDto=UserDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .roles(user.getRoles())
                .id(user.getId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
        return userDto;
    }
}
