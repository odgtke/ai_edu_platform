package com.chinaunicom.edu.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * JWT认证全局过滤器
 */
@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private Key signingKey;

    public JwtAuthenticationFilter(@Value("${jwt.secret:}") String jwtSecret) {
        // 优先从环境变量或配置中心获取密钥
        String secret = jwtSecret;
        if (secret == null || secret.isEmpty()) {
            secret = System.getenv("JWT_SECRET");
        }
        if (secret == null || secret.isEmpty()) {
            secret = System.getProperty("jwt.secret");
        }
        
        // 如果仍没有设置密钥，则生成警告
        if (secret == null || secret.isEmpty()) {
            System.err.println("警告: JWT密钥未设置，使用默认密钥存在安全隐患！");
            secret = "mySecretKey"; // 默认值仅用于开发环境
        }
        
        // 使用Base64编码的密钥以确保兼容性
        byte[] keyBytes = Base64.getEncoder().encode(secret.getBytes());
        this.signingKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS512.getJcaName());
    }

    // 白名单路径
    private static final List<String> WHITE_LIST = Arrays.asList(
        "/api/auth/login",
        "/api/auth/register",
        "/api/auth/refresh",
        "/actuator",
        "/nacos"
    );
    
    // 开发环境临时白名单（用于前端联调）
    private static final List<String> DEV_WHITE_LIST = Arrays.asList(
        "/api/users/count",
        "/api/users/page",
        "/api/users",
        "/api/courses/count",
        "/api/courses/page",
        "/api/courses",
        "/api/learning/records",
        "/api/assignments",
        "/api/assessments",
        "/api/analytics",
        "/api/recommendations",
        "/api/resources",
        "/api/agent",
        "/api/classes",
        "/api/grades"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // 白名单放行
        if (isWhiteList(path)) {
            return chain.filter(exchange);
        }

        // 获取Token
        String token = getTokenFromRequest(request);
        if (!StringUtils.hasText(token)) {
            return unauthorized(exchange.getResponse());
        }

        // 验证Token
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 将用户信息添加到请求头
            String userId = claims.getSubject();
            String role = claims.get("role", String.class);

            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Role", role != null ? role : "")
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            log.error("JWT验证失败: {}", e.getMessage());
            return unauthorized(exchange.getResponse());
        }
    }

    /**
     * 从请求中获取Token
     */
    private String getTokenFromRequest(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 检查是否在白名单中
     */
    private boolean isWhiteList(String path) {
        // 开发环境：放行所有 /api/ 开头的接口
        if (path.startsWith("/api/")) {
            return true;
        }
        // 检查永久白名单
        if (WHITE_LIST.stream().anyMatch(path::startsWith)) {
            return true;
        }
        // 检查开发环境白名单（支持前缀匹配）
        return DEV_WHITE_LIST.stream().anyMatch(path::startsWith);
    }

    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorized(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -100; // 最高优先级
    }
}
