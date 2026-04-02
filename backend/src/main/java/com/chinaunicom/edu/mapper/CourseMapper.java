package com.chinaunicom.edu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinaunicom.edu.entity.Course;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程Mapper接口
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {
}