package io.app.controller;

import io.app.dto.ApiResponse;
import io.app.dto.AuthResponse;
import io.app.dto.RegisterRequest;
import io.app.service.impl.AuthServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthServiceImpl service;

    public AuthController(AuthServiceImpl service){
        this.service=service;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request){
        return new ResponseEntity<>(service.register(request.email(),request.password()), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(service.login(request.email(),request.password()));
    }
}
