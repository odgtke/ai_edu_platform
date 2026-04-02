package com.chinaunicom.edu.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学习路径实体类
 * 存储用户的学习路径规划，支持动态调整
 */
@Data
public class LearningPath {
    private Long id;
    private Long userId;
    private String pathName;
    private String pathDescription;
    private String courseIds; // JSON格式存储课程ID数组
    private String pathStatus; // 路径状态: active,completed,archived
    private BigDecimal completionRate; // 完成率 0-100
    private Integer estimatedHours; // 预估学习小时数
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    
    // 动态调整相关字段
    private String adaptiveLevel; // 适应性等级: easy,normal,hard
    private BigDecimal difficultyFactor; // 难度系数 0.5-2.0
    private String adjustmentReason; // 调整原因
    private LocalDateTime lastAdjustmentTime; // 上次调整时间
    private String performanceMetrics; // JSON格式存储性能指标
    private String alternativePaths; // JSON格式存储备选路径
    private Integer currentStageIndex; // 当前阶段索引
    private String stageCompletionStatus; // 各阶段完成状态JSON
}