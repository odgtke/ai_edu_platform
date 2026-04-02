package com.chinaunicom.edu.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户行为实体类
 * 记录用户的各种学习行为数据
 */
@Data
public class UserBehavior {
    private Long id;
    private Long userId;
    private Long courseId;
    private String behaviorType; // 行为类型: view,browse,study,complete,favorite,share
    private BigDecimal behaviorValue; // 行为权重值
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdTime;
}