package com.chinaunicom.edu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinaunicom.edu.entity.User;

/**
 * 用户Service接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);
}