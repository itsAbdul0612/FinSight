package com.technerd.finsight.security.service;

import com.technerd.finsight.security.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JWTService {

    @Value("${jwt.secret-key}")
    private String secret;

    private final long refreshTokenValidity =  1000*120*24*30*6L;

    private SecretKey secretKey(){
       return Keys.hmacShaKeyFor(secret.getBytes());
    }


    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000*60*10))
                .signWith(secretKey())
                .compact();
    }

    public String generateRefreshToken(User user) {
       return Jwts.builder()
               .subject(user.getId().toString())
               .issuedAt(new Date())
               .expiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
               .signWith(secretKey())
               .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return  Long.parseLong(claims.getSubject());
    }
}
