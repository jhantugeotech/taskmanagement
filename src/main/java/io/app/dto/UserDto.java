package io.app.dto;

import io.app.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private Set<Role> roles=new HashSet<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
