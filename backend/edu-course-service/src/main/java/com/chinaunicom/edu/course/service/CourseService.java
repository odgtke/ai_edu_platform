package com.chinaunicom.edu.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinaunicom.edu.course.entity.Course;

/**
 * 课程Service接口
 */
public interface CourseService extends IService<Course> {
    
    /**
     * 根据ID获取课程
     */
    Course getCourseById(Long id);
}
