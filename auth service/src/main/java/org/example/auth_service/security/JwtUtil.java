package org.example.auth_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.example.auth_service.model.Role;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    private static final String SECRET = "BIbisIDkHDt5nhhwp3tYgP+hTUleOkJfXPXkoYZfVteu+alOSlGtCUDg6HICUXCf";


    private static final SecretKey SECRET_KEY =
            Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    private final long jwtExpirationMs = 3600000;

    public String generateToken(int userId, String username, Role role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId",userId)
                .claim("roles", role.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY)
                .build().parseClaimsJws(token).getBody().getSubject();
    }

    public Integer getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("userId", Integer.class);
    }
}