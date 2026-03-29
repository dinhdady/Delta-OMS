package com.project.management_system.security.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    private final String SECRET =
            "managementsystemsecretkeymanagementsystemsecretkey";

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    private final long ACCESS_EXPIRATION = 15 * 60 * 1000;      // 15 minutes
    private final long REFRESH_EXPIRATION = 7 * 24 * 60 * 60 * 1000; // 7 days

    public String generateAccessToken(String username) {
        return buildToken(username, ACCESS_EXPIRATION);
    }

    public String generateRefreshToken(String username) {
        return buildToken(username, REFRESH_EXPIRATION);
    }

    private String buildToken(String username, long expiration) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .claim("jti", UUID.randomUUID().toString())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}