package com.chinaunicom.edu.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程相似度实体类
 * 存储课程之间的相似度信息
 */
@Data
public class CourseSimilarity {
    private Long id;
    private Long course1Id;
    private Long course2Id;
    private BigDecimal similarityScore; // 相似度分数 0-1
    private String similarityType; // 相似度类型: category,content,instructor
    private LocalDateTime calculatedTime;
}