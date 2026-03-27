package io.app.service;

import io.app.dto.ApiResponse;
import io.app.dto.AuthResponse;

public interface AuthService {
    public ApiResponse register(String email,String password);
    public AuthResponse login(String email, String password);
}
