package com.chinaunicom.edu.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户知识点掌握情况实体类
 * 记录用户对每个知识点的掌握程度和学习历史
 */
@Data
public class UserKnowledgeMastery {
    private Long id;
    private Long userId;              // 用户ID
    private Long knowledgePointId;    // 知识点ID
    private BigDecimal masteryLevel;  // 掌握程度 0.0-100.0
    private Integer attemptCount;     // 尝试次数
    private Integer correctCount;     // 正确次数
    private BigDecimal averageScore;  // 平均得分
    private LocalDateTime lastStudyTime; // 最后学习时间
    private Integer studyDuration;    // 累计学习时长(分钟)
    private String learningMethods;   // 学习方式记录 (JSON)
    private String weakAreas;         // 薄弱环节分析 (JSON)
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
