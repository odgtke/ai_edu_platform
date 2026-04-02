package com.chinaunicom.edu.course.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinaunicom.edu.course.entity.Course;
import com.chinaunicom.edu.course.mapper.CourseMapper;
import com.chinaunicom.edu.course.service.CourseService;
import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
}
