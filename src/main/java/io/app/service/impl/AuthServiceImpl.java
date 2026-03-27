package io.app.service.impl;

import io.app.dto.ApiResponse;
import io.app.dto.AuthResponse;
import io.app.exception.BadRequestException;
import io.app.exception.ConflictException;
import io.app.exception.ResourceNotFoundException;
import io.app.model.Role;
import io.app.model.User;
import io.app.repository.UserRepository;
import io.app.service.AuthService;
import io.app.utils.JwtUtils;
import io.app.utils.Validators;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository repository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository repository,
                           JwtUtils jwtUtils,
                           PasswordEncoder passwordEncoder){
        this.jwtUtils=jwtUtils;
        this.repository=repository;
        this.passwordEncoder=passwordEncoder;
    }

    @Override
    public ApiResponse register(String email, String password) {
        if (!Validators.isValidEmail(email)){
            throw new BadRequestException("Invalid Email");
        }
        if (password.length()<6 || password.length()>15){
            throw new BadRequestException("Password length must be more than 6 and less than 15");
        }
        if (repository.existsByEmail(email)){
            throw new ConflictException("User already exist");
        }
        User user=new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.getRoles().add(Role.USER);
        repository.save(user);

        return ApiResponse.builder()
                .status(true)
                .message("Registered Successfully")
                .build();
    }

    @Override
    public AuthResponse login(String email, String password) {
        User user=repository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("User not found"));
        if (!passwordEncoder.matches(password,user.getPassword())){
            throw new BadRequestException("Invalid Password");
        }
        String token=jwtUtils.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .status(true)
                .build();
    }
}
