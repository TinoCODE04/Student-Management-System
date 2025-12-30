package com.studentsys.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * JWT鉴权全局过滤器
 */
@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.whitelist}")
    private String whitelistStr;
    
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        log.debug("Gateway收到请求: {} {}", request.getMethod(), path);
        
        // 白名单放行
        if (isWhitelist(path)) {
            log.debug("白名单路径放行: {}", path);
            return chain.filter(exchange);
        }
        
        // 获取 Token
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("请求未携带Token或格式错误: {}", path);
            return unauthorized(exchange, "未登录或Token无效");
        }
        
        String token = authHeader.substring(7);
        
        // 验证 Token
        try {
            Claims claims = parseToken(token);
            if (claims == null) {
                log.warn("Token解析失败: {}", path);
                return unauthorized(exchange, "Token无效");
            }
            
            // 检查Token是否过期
            if (claims.getExpiration().before(new java.util.Date())) {
                log.warn("Token已过期: {}", path);
                return unauthorized(exchange, "Token已过期，请重新登录");
            }
            
            // 将用户信息传递到下游服务
            Object userId = claims.get("userId");
            String userIdStr = userId instanceof Integer ? 
                    String.valueOf(userId) : String.valueOf(userId);
            
            ServerHttpRequest newRequest = request.mutate()
                    .header("X-User-Id", userIdStr)
                    .header("X-Username", (String) claims.get("username"))
                    .header("X-Role", (String) claims.get("role"))
                    .build();
            
            log.debug("Token验证成功, 用户: {}, 角色: {}", 
                    claims.get("username"), claims.get("role"));
            
            return chain.filter(exchange.mutate().request(newRequest).build());
        } catch (Exception e) {
            log.error("Token解析异常: {}", e.getMessage());
            return unauthorized(exchange, "Token解析失败");
        }
    }
    
    @Override
    public int getOrder() {
        return -100; // 优先级高
    }
    
    /**
     * 判断是否是白名单路径
     */
    private boolean isWhitelist(String path) {
        if (whitelistStr == null || whitelistStr.trim().isEmpty()) {
            return false;
        }
        List<String> whitelist = Arrays.asList(whitelistStr.split(","));
        return whitelist.stream()
                .map(String::trim)
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
    
    /**
     * 解析Token
     */
    private Claims parseToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("解析Token失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        String body = String.format("{\"code\":401,\"message\":\"%s\",\"data\":null}", message);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
