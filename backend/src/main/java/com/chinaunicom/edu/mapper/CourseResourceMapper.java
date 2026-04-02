package com.chinaunicom.edu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinaunicom.edu.entity.CourseResource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 课程资源关联 Mapper 接口
 */
@Mapper
public interface CourseResourceMapper extends BaseMapper<CourseResource> {

    /**
     * 根据课程ID查询关联列表
     */
    @Select("SELECT * FROM edu_course_resource WHERE course_id = #{courseId} ORDER BY resource_order ASC")
    List<CourseResource> selectByCourseId(@Param("courseId") Long courseId);

    /**
     * 检查课程是否已关联资源
     */
    @Select("SELECT COUNT(*) FROM edu_course_resource WHERE course_id = #{courseId} AND resource_id = #{resourceId}")
    int checkExist(@Param("courseId") Long courseId, @Param("resourceId") Long resourceId);
}
