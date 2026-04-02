package com.chinaunicom.edu.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("edu_class")
public class Clazz {
    
    @TableId(type = IdType.AUTO)
    private Long classId;
    
    private String className;
    
    private Long gradeId;
    
    private Long teacherId;
    
    private Integer studentCount;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
