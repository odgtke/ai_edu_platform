package com.chinaunicom.edu.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户VO
 */
@Data
public class UserVO {
    
    private Long userId;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private Integer userType;
    private Integer status;
    private String avatar;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}