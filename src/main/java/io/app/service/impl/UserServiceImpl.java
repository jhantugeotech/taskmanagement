package io.app.service.impl;

import io.app.dto.UserDto;
import io.app.exception.ResourceNotFoundException;
import io.app.model.User;
import io.app.repository.UserRepository;
import io.app.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository){
        this.repository=repository;
    }


    @Override
    public UserDto profile(Authentication authentication) {
        UserDetails userDetails= (UserDetails) authentication.getPrincipal();
        User user=repository.findByEmail(userDetails.getUsername())
                .orElseThrow(()->new ResourceNotFoundException("Invalid Credentials"));

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build();
    }
}
