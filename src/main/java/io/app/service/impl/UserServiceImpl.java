package io.app.service.impl;

import io.app.dto.UserDto;
import io.app.exception.ResourceNotFoundException;
import io.app.mapper.ModelMapper;
import io.app.model.Role;
import io.app.model.User;
import io.app.repository.UserRepository;
import io.app.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder){
        this.repository=repository;
        this.passwordEncoder=passwordEncoder;
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

    @Override
    public List<UserDto> users() {
        List<User> users=repository.findAll();
        List<UserDto> restult=users.stream().map(ModelMapper::userToDto)
                .collect(Collectors.toList());
        return restult;
    }

    @PostConstruct
    public void createDefaultAdmin(){
        if (repository.findByEmail("admin@gmail.com").isEmpty()){
            User admin=User.builder()
                    .name("Admin")
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("admin@123"))
                    .roles(Set.of(Role.ADMIN))
                    .build();
            repository.save(admin);
        }
        System.out.println("Default admin email: admin@gmail.com");
        System.out.println("Default admin password: admin@123");
    }

}
