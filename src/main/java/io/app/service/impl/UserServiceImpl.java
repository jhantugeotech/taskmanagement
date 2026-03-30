package io.app.service.impl;

import io.app.dto.ApiResponse;
import io.app.dto.UserDto;
import io.app.exception.BadRequestException;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.IllegalFormatCodePointException;
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

    @Override
    public ApiResponse createAdmin(User user) {
        if (user.getEmail().isEmpty()){
            throw new BadRequestException("Email Required");
        }
        if (user.getPassword().isEmpty()){
            throw new BadRequestException("Password Required");
        }
        user.setRoles(Set.of(Role.ADMIN));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repository.save(user);
        return ApiResponse.builder()
                .message("Admin Created Successfully")
                .status(true)
                .build();
    }

    @Override
    public UserDto getUserById(Long id) {
        User user=repository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("User Not Found"));
        return ModelMapper.userToDto(user);
    }

    @Override
    @Transactional
    public ApiResponse updateUser(User user,Authentication authentication) {
        User updatableUser=repository.findByEmail(user.getEmail())
                .orElseThrow(()->new ResourceNotFoundException("User Not Found"));
        updatableUser.setName(user.getName());
        return ApiResponse.builder()
                .status(true)
                .message("User Updated Successfully")
                .build();
    }

    @Override
    public ApiResponse deleteUserById(Long id) {
        if (!repository.existsById(id)){
            throw new ResourceNotFoundException("User Not Found");
        }
        repository.deleteById(id);
        return ApiResponse.builder()
                .message("User Deleted Successfully")
                .status(true)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse assignRoleById(Long id, Set<Role> roles) {
        User user=repository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("User Not Found"));
        user.getRoles().addAll(roles);
        return ApiResponse.builder()
                .message("Role Added Successfully")
                .status(true)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse removeRoleById(Long id, Role role) {
        User user=repository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("User Not Found"));
        user.getRoles().removeIf(role1 -> role1.equals(role));
        return ApiResponse.builder()
                .status(true)
                .message("User Role Removed Successfully")
                .build();
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
