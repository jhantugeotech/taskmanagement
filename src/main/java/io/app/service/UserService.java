package io.app.service;

import io.app.dto.UserDto;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {
    UserDto profile(Authentication authentication);
    List<UserDto> users();
}
