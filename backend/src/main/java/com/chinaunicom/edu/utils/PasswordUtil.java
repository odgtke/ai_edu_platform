package com.chinaunicom.edu.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * 密码安全工具类
 * 提供强密码生成、验证和加密功能
 */
@Component
@Slf4j
public class PasswordUtil {

    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom;
    
    // 密码强度要求
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 128;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{" + 
        MIN_LENGTH + "," + MAX_LENGTH + "}$"
    );
    
    public PasswordUtil() {
        this.passwordEncoder = new BCryptPasswordEncoder(12); // 使用更高的强度
        this.secureRandom = new SecureRandom();
    }
    
    /**
     * 生成强随机密码
     * @param length 密码长度（8-128）
     * @return 生成的强密码
     */
    public String generateStrongPassword(int length) {
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            throw new IllegalArgumentException("密码长度必须在" + MIN_LENGTH + "-" + MAX_LENGTH + "之间");
        }
        
        // 字符集定义
        String upperChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerChars = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "@#$%^&+=!";
        String allChars = upperChars + lowerChars + digits + specialChars;
        
        StringBuilder password = new StringBuilder();
        
        // 确保每种字符类型至少有一个
        password.append(upperChars.charAt(secureRandom.nextInt(upperChars.length())));
        password.append(lowerChars.charAt(secureRandom.nextInt(lowerChars.length())));
        password.append(digits.charAt(secureRandom.nextInt(digits.length())));
        password.append(specialChars.charAt(secureRandom.nextInt(specialChars.length())));
        
        // 填充剩余长度
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(secureRandom.nextInt(allChars.length())));
        }
        
        // 随机打乱字符顺序
        return shuffleString(password.toString());
    }
    
    /**
     * 验证密码强度
     * @param password 待验证的密码
     * @return 是否符合强度要求
     */
    public boolean validatePasswordStrength(String password) {
        if (password == null || password.length() < MIN_LENGTH || password.length() > MAX_LENGTH) {
            return false;
        }
        
        return PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * 加密密码
     * @param rawPassword 明文密码
     * @return 加密后的密码
     */
    public String encodePassword(String rawPassword) {
        if (!validatePasswordStrength(rawPassword)) {
            throw new IllegalArgumentException("密码不符合安全要求");
        }
        
        String encodedPassword = passwordEncoder.encode(rawPassword);
        log.debug("Password encoded successfully");
        return encodedPassword;
    }
    
    /**
     * 验证密码
     * @param rawPassword 明文密码
     * @param encodedPassword 加密密码
     * @return 是否匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        boolean result = passwordEncoder.matches(rawPassword, encodedPassword);
        log.debug("Password match result: {}", result);
        return result;
    }
    
    /**
     * 生成密码重置令牌
     * @return 安全的重置令牌
     */
    public String generateResetToken() {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
    
    /**
     * 验证密码重置令牌格式
     * @param token 令牌
     * @return 是否有效格式
     */
    public boolean isValidResetToken(String token) {
        if (token == null || token.length() != 43) { // Base64编码后长度
            return false;
        }
        
        try {
            Base64.getUrlDecoder().decode(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 打乱字符串顺序
     */
    private String shuffleString(String input) {
        char[] chars = input.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = secureRandom.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }
    
    /**
     * 获取密码强度评分
     * @param password 密码
     * @return 强度评分（0-100）
     */
    public int getPasswordStrengthScore(String password) {
        if (password == null || password.isEmpty()) {
            return 0;
        }
        
        int score = 0;
        
        // 长度评分
        if (password.length() >= 8) score += 20;
        if (password.length() >= 12) score += 10;
        if (password.length() >= 16) score += 10;
        
        // 字符类型评分
        if (password.matches(".*[a-z].*")) score += 15;
        if (password.matches(".*[A-Z].*")) score += 15;
        if (password.matches(".*[0-9].*")) score += 15;
        if (password.matches(".*[@#$%^&+=!].*")) score += 15;
        
        // 复杂度加分
        if (password.length() >= 12 && 
            password.matches(".*[a-z].*") && 
            password.matches(".*[A-Z].*") && 
            password.matches(".*[0-9].*") && 
            password.matches(".*[@#$%^&+=!].*")) {
            score += 10;
        }
        
        return Math.min(score, 100);
    }
    
    /**
     * 获取密码强度描述
     * @param password 密码
     * @return 强度描述
     */
    public String getPasswordStrengthDescription(String password) {
        int score = getPasswordStrengthScore(password);
        
        if (score >= 80) {
            return "很强";
        } else if (score >= 60) {
            return "强";
        } else if (score >= 40) {
            return "中等";
        } else if (score >= 20) {
            return "弱";
        } else {
            return "很弱";
        }
    }
}