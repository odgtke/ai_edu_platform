package com.chinaunicom.edu.user.util;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private Key signingKey;

    public JwtUtil(@Value("${jwt.secret:}") String jwtSecret) {
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

    @Value("${jwt.expiration:86400000}")
    private long expiration;

    public String generateToken(String username, String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, signingKey)
                .compact();
    }

    public String generateRefreshToken(String username, String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 7); // 7天

        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, signingKey)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }
}
