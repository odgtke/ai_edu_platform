package com.chinaunicom.edu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学习记录实体类
 */
@Data
@TableName("edu_learning_record")
public class LearningRecord {
    
    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}