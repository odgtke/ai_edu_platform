package com.chinaunicom.edu.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
@TableName("learning_paths")
public class LearningPath {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    /**
     * 路径名称
     */
    private String pathName;
    
    /**
     * 路径描述
     */
    private String pathDescription;
    
    /**
     * 课程ID数组（JSON格式）
     */
    private String courseIds;
    
    /**
     * 路径状态：active, completed, archived
     */
    private String pathStatus;
    
    /**
     * 完成率 0-100
     */
    private Double completionRate;
    
    /**
     * 预估学习小时数
     */
    private Integer estimatedHours;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
