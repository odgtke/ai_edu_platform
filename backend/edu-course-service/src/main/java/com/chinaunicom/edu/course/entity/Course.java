package com.chinaunicom.edu.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("edu_course")
public class Course {
    
    @TableId(type = IdType.AUTO)
    private Long courseId;
    
    private String courseName;
    
    private String description;
    
    private Long teacherId;
    
    @TableField(exist = false)
    private String category;
    
    private String coverImage;
    
    private Integer status;
    
    private Integer totalLessons;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
