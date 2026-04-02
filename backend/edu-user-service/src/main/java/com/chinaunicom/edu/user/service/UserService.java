package com.chinaunicom.edu.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinaunicom.edu.user.entity.User;

public interface UserService extends IService<User> {
    User login(String username, String password);
}
