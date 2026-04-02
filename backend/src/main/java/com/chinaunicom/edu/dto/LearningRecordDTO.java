package com.chinaunicom.edu.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学习记录DTO，包含课程名称
 */
@Data
public class LearningRecordDTO {
    
    /**
     * 记录ID
     */
    private Long recordId;
    
    /**
     * 学生ID
     */
    private Long studentId;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 课程总课时数
     */
    private Integer totalLessons;
    
    /**
     * 已学课时数（根据进度计算）
     */
    private Integer completedLessons;
    
    /**
     * 课时ID
     */
    private Long lessonId;
    
    /**
     * 学习时长（分钟）
     */
    private Integer studyDuration;
    
    /**
     * 学习进度百分比
     */
    private Integer progress;
    
    /**
     * 是否完成
     */
    private Boolean isCompleted;
    
    /**
     * 最后学习时间
     */
    private LocalDateTime lastStudyTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
