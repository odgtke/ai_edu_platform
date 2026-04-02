package com.chinaunicom.edu.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 用户DTO
 */
@Data
public class UserDTO {
    
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    private String realName;
    private String phone;
    private String email;
    private Integer userType;
    private Integer status;
    private String avatar;
}