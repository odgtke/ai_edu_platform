package com.chinaunicom.edu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinaunicom.edu.entity.Course;
import com.chinaunicom.edu.mapper.CourseMapper;
import com.chinaunicom.edu.service.CourseService;
import org.springframework.stereotype.Service;

/**
 * 课程Service实现类
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
}