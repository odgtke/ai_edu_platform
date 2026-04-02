package com.chinaunicom.edu.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("edu_grade")
public class Grade {
    
    @TableId(type = IdType.AUTO)
    private Long gradeId;
    
    private String gradeName;
    
    private Integer level;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
