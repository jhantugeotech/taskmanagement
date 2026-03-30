package io.app.utils;

import io.app.service.impl.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    public JwtFilter(JwtUtils jwtUtils,CustomUserDetailsService userDetailsService){
        this.jwtUtils=jwtUtils;
        this.userDetailsService=userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try{
            String header=request.getHeader("Authorization");

            if (header==null || !header.startsWith("Bearer")){
                filterChain.doFilter(request,response);
                return;
            }

            final String token=header.substring(7);
            String username= jwtUtils.extractUsername(token);

            if (!username.isEmpty() && SecurityContextHolder.getContext().getAuthentication()==null){
                UserDetails userDetails=userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken auth=new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            filterChain.doFilter(request,response);
        }catch (ExpiredJwtException ex){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter()
                    .write("{\"message\": \"Your session has expired. Please log in again.\",\"status\": false}");
        }
    }
}
