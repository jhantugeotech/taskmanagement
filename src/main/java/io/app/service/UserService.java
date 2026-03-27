package io.app.service;

import io.app.dto.UserDto;
import org.springframework.security.core.Authentication;

public interface UserService {
    UserDto profile(Authentication authentication);
}
