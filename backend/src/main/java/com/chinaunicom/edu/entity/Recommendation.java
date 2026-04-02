package com.chinaunicom.edu.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 推荐结果实体类
 * 存储给用户推荐的课程及其相关信息
 */
@Data
public class Recommendation {
    private Long id;
    private Long userId;
    private Long courseId;
    private String recommendationType; // 推荐类型: content_based,collaborative_filtering,trending,knowledge_based
    private BigDecimal recommendationScore; // 推荐分数 0-100
    private String recommendationReason; // 推荐理由
    private Boolean isClicked; // 是否点击
    private Boolean isEnrolled; // 是否报名
    private LocalDateTime createdTime;
    private LocalDateTime expiredTime;
}