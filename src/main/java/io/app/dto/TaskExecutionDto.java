package io.app.dto;

import io.app.model.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class TaskExecutionDto {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private List<TaskExecutionDto> children=new ArrayList<>();
}
