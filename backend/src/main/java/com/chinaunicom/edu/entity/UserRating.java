package com.chinaunicom.edu.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户评分实体类
 * 记录用户对课程的评分信息
 */
@Data
public class UserRating {
    private Long id;
    private Long userId;
    private Long courseId;
    private BigDecimal rating; // 评分 1-5
    private String ratingType; // 评分类型: manual,auto
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}