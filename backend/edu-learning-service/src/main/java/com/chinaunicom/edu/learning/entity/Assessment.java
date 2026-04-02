package com.chinaunicom.edu.learning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("edu_assessment")
public class Assessment {
    
    @TableId(type = IdType.AUTO)
    private Long assessmentId;
    
    private String assessmentName;
    
    private String description;
    
    private Long courseId;
    
    private Integer totalScore;
    
    private Integer duration;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
