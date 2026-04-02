package com.chinaunicom.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinaunicom.edu.entity.UserClass;
import com.chinaunicom.edu.mapper.UserClassMapper;
import com.chinaunicom.edu.service.ClazzService;
import com.chinaunicom.edu.service.UserClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户班级关联服务实现类
 */
@Service
public class UserClassServiceImpl extends ServiceImpl<UserClassMapper, UserClass> implements UserClassService {

    private final UserClassMapper userClassMapper;
    private final ClazzService clazzService;

    @Autowired
    public UserClassServiceImpl(UserClassMapper userClassMapper, ClazzService clazzService) {
        this.userClassMapper = userClassMapper;
        this.clazzService = clazzService;
    }

    @Override
    @Transactional
    public boolean assignClass(Long userId, Long classId, String userType) {
        // 检查是否已存在关联
        QueryWrapper<UserClass> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .eq("class_id", classId);
        if (count(wrapper) > 0) {
            return false;
        }
        
        UserClass userClass = new UserClass();
        userClass.setUserId(userId);
        userClass.setClassId(classId);
        userClass.setUserType(userType);
        userClass.setJoinTime(LocalDateTime.now());
        
        boolean success = save(userClass);
        
        // 更新班级学生人数
        if (success && "student".equals(userType)) {
            clazzService.updateStudentCount(classId);
        }
        
        return success;
    }

    @Override
    @Transactional
    public boolean removeFromClass(Long userId, Long classId) {
        QueryWrapper<UserClass> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .eq("class_id", classId);
        
        UserClass userClass = getOne(wrapper);
        if (userClass == null) {
            return false;
        }
        
        boolean success = remove(wrapper);
        
        // 更新班级学生人数
        if (success && "student".equals(userClass.getUserType())) {
            clazzService.updateStudentCount(classId);
        }
        
        return success;
    }

    @Override
    public List<UserClass> listByUserId(Long userId) {
        return userClassMapper.selectByUserId(userId);
    }

    @Override
    public List<UserClass> listByClassId(Long classId) {
        return userClassMapper.selectByClassId(classId);
    }

    @Override
    public List<Long> getClassIdsByUserId(Long userId) {
        return userClassMapper.selectClassIdsByUserId(userId);
    }
}
