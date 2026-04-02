package com.chinaunicom.edu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinaunicom.edu.entity.User;
import com.chinaunicom.edu.mapper.UserMapper;
import com.chinaunicom.edu.service.UserService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 用户Service实现类
 * 支持Redis缓存
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Override
    @Cacheable(value = "users", key = "#username")
    public User findByUsername(String username) {
        return this.lambdaQuery()
                .eq(User::getUsername, username)
                .one();
    }

    /**
     * 根据ID获取用户（带缓存）
     */
    @Cacheable(value = "users", key = "#id")
    public User getUserById(Long id) {
        return super.getById(id);
    }

    /**
     * 更新用户（更新缓存）
     */
    @CachePut(value = "users", key = "#user.userId")
    public boolean updateUser(User user) {
        return super.updateById(user);
    }

    /**
     * 删除用户（清除缓存）
     */
    @CacheEvict(value = "users", key = "#id")
    public boolean deleteUser(Long id) {
        return super.removeById(id);
    }
}