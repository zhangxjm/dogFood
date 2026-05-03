package com.seckill.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Slf4j
public class JwtUtils {

    private static final String SECRET = "seckill-jwt-secret-key-must-be-at-least-256-bits-long-for-hs256";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    private static final long EXPIRATION = 7 * 24 * 60 * 60 * 1000L;

    public static String generateToken(Long userId, String username) {
        return generateToken(userId, username, EXPIRATION);
    }

    public static String generateToken(Long userId, String username, long expiration) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.error("Token已过期: {}", e.getMessage());
            throw new RuntimeException("Token已过期");
        } catch (UnsupportedJwtException e) {
            log.error("不支持的Token格式: {}", e.getMessage());
            throw new RuntimeException("不支持的Token格式");
        } catch (MalformedJwtException e) {
            log.error("Token格式错误: {}", e.getMessage());
            throw new RuntimeException("Token格式错误");
        } catch (IllegalArgumentException e) {
            log.error("Token参数异常: {}", e.getMessage());
            throw new RuntimeException("Token参数异常");
        } catch (Exception e) {
            log.error("Token解析失败: {}", e.getMessage());
            throw new RuntimeException("Token解析失败");
        }
    }

    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        return Long.parseLong(claims.getSubject());
    }

    public static String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    public static boolean isExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
