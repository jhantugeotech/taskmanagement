package io.app.controller;

import io.app.dto.UserDto;
import io.app.service.impl.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.webauthn.api.CredentialPropertiesOutput;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserServiceImpl service;

    public UserController(UserServiceImpl service){
        this.service=service;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> profile(Authentication authentication){
        return ResponseEntity.ok(service.profile(authentication));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> users(){
        return ResponseEntity.ok(service.users());
    }

}
