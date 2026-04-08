package com.chinaunicom.edu.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
@TableName("user_preferences")
public class UserPreference {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    /**
     * 偏好类型：course_category(课程类别), difficulty_level(难度), learning_style(学习风格), tag(标签)
     */
    private String preferenceType;
    
    /**
     * 偏好值，如：编程开发、中级、实践型
     */
    private String preferenceValue;
    
    /**
     * 偏好权重 0.00-1.00
     */
    private Double preferenceScore;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
