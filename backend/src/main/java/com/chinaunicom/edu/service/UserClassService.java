package com.chinaunicom.edu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinaunicom.edu.entity.UserClass;

import java.util.List;

/**
 * 用户班级关联服务接口
 */
public interface UserClassService extends IService<UserClass> {

    /**
     * 为用户分配班级
     */
    boolean assignClass(Long userId, Long classId, String userType);

    /**
     * 移除用户的班级
     */
    boolean removeFromClass(Long userId, Long classId);

    /**
     * 获取用户的班级列表
     */
    List<UserClass> listByUserId(Long userId);

    /**
     * 获取班级的用户列表
     */
    List<UserClass> listByClassId(Long classId);

    /**
     * 获取用户所在的所有班级ID
     */
    List<Long> getClassIdsByUserId(Long userId);
}
