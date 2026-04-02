package com.chinaunicom.edu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("edu_user")
public class User {
    
    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 用户类型：1-学生 2-教师 3-家长 4-管理员
     */
    private Integer userType;
    
    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
    
    /**
     * 头像URL
     */
    private String avatar;
    
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