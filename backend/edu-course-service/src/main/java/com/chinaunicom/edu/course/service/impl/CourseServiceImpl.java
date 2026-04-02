package com.chinaunicom.edu.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinaunicom.edu.common.enums.ExceptionEnum;
import com.chinaunicom.edu.common.exception.BusinessException;
import com.chinaunicom.edu.course.entity.Course;
import com.chinaunicom.edu.course.mapper.CourseMapper;
import com.chinaunicom.edu.course.service.CourseService;
import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
    
    @Override
    public Course getCourseById(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ExceptionEnum.REQUEST_PARAMS_ERROR.getCode(), "课程ID不能为空或小于等于0");
        }
        
        Course course = this.getById(id);
        if (course == null) {
            throw new BusinessException(ExceptionEnum.COURSE_NOT_EXIST.getCode(), "课程不存在");
        }
        
        return course;
    }
    
    @Override
    public boolean save(Course entity) {
        if (entity == null) {
            throw new BusinessException(ExceptionEnum.REQUEST_PARAMS_ERROR.getCode(), "课程信息不能为空");
        }
        
        if (entity.getCourseName() == null || entity.getCourseName().trim().isEmpty()) {
            throw new BusinessException(ExceptionEnum.REQUEST_PARAMS_ERROR.getCode(), "课程名称不能为空");
        }
        
        // 检查课程名称是否已存在
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.eq("course_name", entity.getCourseName());
        if (entity.getCourseId() != null) {
            // 更新时排除当前课程ID
            wrapper.ne("course_id", entity.getCourseId());
        }
        
        long count = this.count(wrapper);
        if (count > 0) {
            throw new BusinessException(ExceptionEnum.COURSE_ALREADY_EXISTS.getCode(), "课程名称已存在");
        }
        
        return super.save(entity);
    }
}
