package com.chinaunicom.edu.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {
    
    @Value("${jwt.secret:}")
    private String secret;
    
    @Value("${jwt.expiration:86400000}")
    private Long expiration;
    
    @Value("${jwt.refresh-expiration:604800000}")
    private Long refreshExpiration;
    
    private SecretKey secretKey;
    
    /**
     * 初始化JWT密钥
     */
    private void initializeSecretKey() {
        if (secretKey == null) {
            if (secret == null || secret.trim().isEmpty()) {
                // 生成强随机密钥
                log.warn("JWT secret not configured, generating strong random key");
                secretKey = generateStrongSecretKey();
            } else {
                // 使用配置的密钥
                byte[] keyBytes = Base64.getDecoder().decode(secret);
                if (keyBytes.length < 32) {
                    log.warn("JWT secret is weak, consider using stronger key");
                    secretKey = Keys.hmacShaKeyFor(keyBytes);
                } else {
                    secretKey = Keys.hmacShaKeyFor(keyBytes);
                }
            }
        }
    }
    
    /**
     * 生成强随机密钥
     */
    private SecretKey generateStrongSecretKey() {
        byte[] keyBytes = new byte[64]; // 512 bits
        new SecureRandom().nextBytes(keyBytes);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成访问令牌
     */
    public String generateAccessToken(String username, String userId) {
        initializeSecretKey();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("type", "access")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }
    
    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(String username, String userId) {
        initializeSecretKey();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration);
        
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("type", "refresh")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }
    
    /**
     * 兼容旧版本的方法
     */
    public String generateToken(String username) {
        return generateAccessToken(username, null);
    }

    /**
     * 从令牌中获取用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            initializeSecretKey();
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            throw new RuntimeException("Token已过期");
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            throw new RuntimeException("无效的Token");
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
            throw new RuntimeException("Token签名无效");
        } catch (Exception e) {
            log.error("Failed to parse JWT token", e);
            throw new RuntimeException("Token解析失败");
        }
    }
    
    /**
     * 从令牌中获取用户ID
     */
    public String getUserIdFromToken(String token) {
        try {
            initializeSecretKey();
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            return claims.get("userId", String.class);
        } catch (Exception e) {
            log.warn("Failed to get userId from token: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 验证令牌是否为刷新令牌
     */
    public boolean isRefreshToken(String token) {
        try {
            initializeSecretKey();
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            String tokenType = claims.get("type", String.class);
            return "refresh".equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证令牌有效性
     */
    public boolean validateToken(String token) {
        try {
            initializeSecretKey();
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Failed to validate JWT token", e);
            return false;
        }
    }
    
    /**
     * 刷新密钥（用于密钥轮换）
     */
    public synchronized void rotateSecretKey() {
        log.info("Rotating JWT secret key");
        this.secretKey = generateStrongSecretKey();
        // 在实际应用中，这里应该通知所有实例更新密钥
    }
    
    /**
     * 获取当前密钥的Base64编码（用于配置）
     */
    public String getCurrentSecretAsBase64() {
        initializeSecretKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
}