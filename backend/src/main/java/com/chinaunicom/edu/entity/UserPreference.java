package com.chinaunicom.edu.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户偏好实体类
 * 用于存储用户的兴趣偏好和学习倾向
 */
@Data
public class UserPreference {
    private Long id;
    private Long userId;
    private String preferenceType;  // 偏好类型: course_category, difficulty_level, learning_style
    private String preferenceValue; // 偏好值
    private BigDecimal preferenceScore; // 偏好权重 0.00-1.00
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}