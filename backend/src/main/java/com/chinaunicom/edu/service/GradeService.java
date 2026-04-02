package com.chinaunicom.edu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinaunicom.edu.entity.Grade;

import java.util.List;

/**
 * 年级服务接口
 */
public interface GradeService extends IService<Grade> {

    /**
     * 获取所有启用的年级列表
     */
    List<Grade> listActiveGrades();

    /**
     * 根据年级级别获取年级
     */
    Grade getByLevel(Integer gradeLevel);
}
