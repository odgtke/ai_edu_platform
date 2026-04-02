package com.chinaunicom.edu.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinaunicom.edu.common.enums.ExceptionEnum;
import com.chinaunicom.edu.common.exception.BusinessException;
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
        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException(ExceptionEnum.REQUEST_PARAMS_ERROR.getCode(), "用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new BusinessException(ExceptionEnum.REQUEST_PARAMS_ERROR.getCode(), "密码不能为空");
        }
        
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = baseMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new BusinessException(ExceptionEnum.USER_NOT_EXIST.getCode(), "用户不存在");
        }
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(ExceptionEnum.USER_PASSWORD_ERROR.getCode(), "密码错误");
        }
        
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ExceptionEnum.USER_ACCOUNT_LOCKED.getCode(), "账户已被锁定");
        }
        
        return user;
    }
}
