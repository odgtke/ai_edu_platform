package com.chinaunicom.edu.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("edu_user")
public class User {
    
    @TableId(type = IdType.AUTO)
    private Long userId;
    
    private String username;
    
    private String password;
    
    private String realName;
    
    private String phone;
    
    private String email;
    
    private Integer userType;
    
    private Integer status;
    
    private String avatar;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
