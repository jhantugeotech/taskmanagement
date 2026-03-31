package io.app.controller;

import io.app.dto.ApiResponse;
import io.app.dto.UserDto;
import io.app.model.Role;
import io.app.model.User;
import io.app.service.UserService;
import io.app.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.webauthn.api.CredentialPropertiesOutput;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

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

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> createAdmin(@RequestBody User user){
        return new ResponseEntity<>(service.createAdmin(user), HttpStatus.CREATED);
    }

    @GetMapping("/admin/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Long id){
        return ResponseEntity.ok(service.getUserById(id));
    }

    @PutMapping
    @PreAuthorize("@userSezcurityService.canUpdateUser(authentication,#user)")
    public ResponseEntity<ApiResponse> updateUser(@RequestBody User user,Authentication auth){
        UserDetails userDetails=(UserDetails) auth.getPrincipal();
        return ResponseEntity.ok(service.updateUser(user,auth));
    }


    @PatchMapping("/admin/user/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> assignRole(@PathVariable("id") Long id,
                                                  @RequestBody Set<Role> roles){
        return ResponseEntity.ok(service.assignRoleById(id,roles));
    }

    @DeleteMapping("/admin/user/{id}/roles/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> removeRole(@PathVariable Long id,
                                                  @PathVariable Role role){
        return ResponseEntity.ok(service.removeRoleById(id,role));
    }

    @DeleteMapping("/admin/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteUserById(@PathVariable("id") Long id){
        return ResponseEntity.ok(service.deleteUserById(id));
    }

}
