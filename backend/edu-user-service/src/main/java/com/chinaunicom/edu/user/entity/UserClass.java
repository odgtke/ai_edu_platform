package com.chinaunicom.edu.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("edu_user_class")
public class UserClass {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long classId;
    
    private Integer role;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
