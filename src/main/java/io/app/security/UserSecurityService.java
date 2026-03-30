package io.app.security;

import io.app.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;


@Component("userSecurityService")
public class UserSecurityService {
    public boolean canUpdateUser(Authentication authentication, User user){
        if (authentication==null || user==null || user.getEmail().isEmpty()){
            return false;
        }
        boolean isAdmin=authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .anyMatch(role->role.equals("ROLE_ADMIN"));
        if (isAdmin){
            return true;
        }

        UserDetails userDetails=(UserDetails) authentication.getPrincipal();

        if (!user.getEmail().equals(userDetails.getUsername())){
            return false;
        }
        return true;
    }
}
