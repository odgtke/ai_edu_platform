package com.chinaunicom.edu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinaunicom.edu.entity.Clazz;

import java.util.List;

/**
 * 班级服务接口
 */
public interface ClazzService extends IService<Clazz> {

    /**
     * 根据年级ID查询班级列表
     */
    List<Clazz> listByGradeId(Long gradeId);

    /**
     * 根据教师ID查询班级列表
     */
    List<Clazz> listByTeacherId(Long teacherId);

    /**
     * 获取班级详情
     */
    Clazz getDetailById(Long classId);

    /**
     * 更新班级学生人数
     */
    void updateStudentCount(Long classId);
}
