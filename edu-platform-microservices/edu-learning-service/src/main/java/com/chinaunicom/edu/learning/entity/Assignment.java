package com.chinaunicom.edu.learning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("edu_assignment")
public class Assignment {
    
    @TableId(type = IdType.AUTO)
    private Long assignmentId;
    
    private Long courseId;
    
    private String title;
    
    private String description;
    
    private LocalDateTime deadline;
    
    private Integer totalScore;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
