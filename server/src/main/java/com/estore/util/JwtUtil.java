package com.estore.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {
    
    public String generateToken(String secret, Long expiration, Map<String, Object> claims) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }
    
    public Claims parseToken(String secret, String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.error("Token已过期: {}", e.getMessage());
            throw new RuntimeException("Token已过期");
        } catch (JwtException e) {
            log.error("Token解析失败: {}", e.getMessage());
            throw new RuntimeException("无效的Token");
        }
    }
    
    public Long getUserId(String secret, String token) {
        Claims claims = parseToken(secret, token);
        return claims.get("userId", Long.class);
    }
    
    public Long getAdminId(String secret, String token) {
        Claims claims = parseToken(secret, token);
        return claims.get("adminId", Long.class);
    }
}
