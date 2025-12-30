package com.studentsys.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
public class JwtUtil {
    
    /**
     * 获取密钥
     */
    public static SecretKey getSecretKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * 生成JWT Token
     */
    public static String generateToken(Long userId, String username, String role, 
                                        String secret, Long expiration) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        
        Date now = new Date();
        // expiration 单位是秒，转换为毫秒
        Date expiryDate = new Date(now.getTime() + expiration * 1000);
        
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSecretKey(secret))
                .compact();
    }
    
    /**
     * 解析Token
     */
    public static Claims parseToken(String token, String secret) {
        try {
            return Jwts.parser()
                    .verifyWith(getSecretKey(secret))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 验证Token是否有效
     */
    public static boolean validateToken(String token, String secret) {
        try {
            Claims claims = parseToken(token, secret);
            return claims != null && !isTokenExpired(claims);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 判断Token是否过期
     */
    public static boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
    
    /**
     * 从Token中获取用户ID
     */
    public static Long getUserId(Claims claims) {
        Object userId = claims.get("userId");
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        return (Long) userId;
    }
    
    /**
     * 从Token中获取用户名
     */
    public static String getUsername(Claims claims) {
        return (String) claims.get("username");
    }
    
    /**
     * 从Token中获取角色
     */
    public static String getRole(Claims claims) {
        return (String) claims.get("role");
    }
}
