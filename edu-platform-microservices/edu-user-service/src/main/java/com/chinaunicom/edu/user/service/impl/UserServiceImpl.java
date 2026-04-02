package com.chinaunicom.edu.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinaunicom.edu.user.entity.User;
import com.chinaunicom.edu.user.mapper.UserMapper;
import com.chinaunicom.edu.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User login(String username, String password) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = baseMapper.selectOne(wrapper);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }
}
