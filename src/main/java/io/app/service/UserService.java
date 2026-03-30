package io.app.service;

import io.app.dto.ApiResponse;
import io.app.dto.UserDto;
import io.app.model.Role;
import io.app.model.User;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Set;

public interface UserService {
    UserDto profile(Authentication authentication);
    List<UserDto> users();
    ApiResponse createAdmin(User user);
    UserDto getUserById(Long id);
    ApiResponse updateUser(User user,Authentication authentication);
    ApiResponse deleteUserById(Long id);
    ApiResponse assignRoleById(Long id,Set<Role> roles);
    ApiResponse removeRoleById(Long id,Role role);
}
