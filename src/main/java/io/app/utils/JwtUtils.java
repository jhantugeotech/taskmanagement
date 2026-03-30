package io.app.utils;

import io.app.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtUtils {
    @Value("${jwt.secret}")
    private String JWT_SECRET;

    private SecretKey signinkey;

    private final long JWT_EXPIRATION=86400000L;

    @PostConstruct
    private void preConstruct(){
        this.signinkey= Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    }

    public String extractUsername(String token){
        return extractClaims(token,Claims::getSubject);
    }

    public boolean isTokenValid(String token,User user){
        String username=extractUsername(token);
        return username.equals(user.getEmail()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token){
        return extractClaims(token,Claims::getExpiration);
    }

    public <T> T extractClaims(String token, Function<Claims,T> resolver){
        return resolver.apply(extractClaims(token));
    }

    private Claims extractClaims(String token){
        return Jwts.parser()
                .verifyWith(this.signinkey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateToken(User user){
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("roles",user.getRoles())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+JWT_EXPIRATION))
                .signWith(this.signinkey)
                .compact();
    }

}
