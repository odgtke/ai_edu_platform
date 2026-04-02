package com.chinaunicom.edu.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 知识点实体类
 * 用于跟踪用户对各个知识点的掌握情况
 */
@Data
public class KnowledgePoint {
    private Long id;
    private String pointName;        // 知识点名称
    private String pointCode;        // 知识点编码
    private Long courseId;           // 所属课程ID
    private String categoryName;     // 分类名称
    private String description;      // 知识点描述
    private Integer difficultyLevel; // 难度等级 1-5
    private String prerequisitePoints; // 前置知识点IDs (JSON数组)
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
