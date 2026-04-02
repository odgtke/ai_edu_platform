package com.chinaunicom.edu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinaunicom.edu.entity.Clazz;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 班级 Mapper 接口
 */
@Mapper
public interface ClazzMapper extends BaseMapper<Clazz> {

    /**
     * 根据年级ID查询班级列表
     */
    @Select("SELECT c.*, g.grade_name FROM edu_class c " +
            "LEFT JOIN edu_grade g ON c.grade_id = g.grade_id " +
            "WHERE c.grade_id = #{gradeId} AND c.status = 1")
    List<Clazz> selectByGradeId(@Param("gradeId") Long gradeId);

    /**
     * 根据教师ID查询班级列表
     */
    @Select("SELECT c.*, g.grade_name FROM edu_class c " +
            "LEFT JOIN edu_grade g ON c.grade_id = g.grade_id " +
            "WHERE c.teacher_id = #{teacherId} AND c.status = 1")
    List<Clazz> selectByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 查询班级详情（包含年级信息）
     */
    @Select("SELECT c.*, g.grade_name FROM edu_class c " +
            "LEFT JOIN edu_grade g ON c.grade_id = g.grade_id " +
            "WHERE c.class_id = #{classId}")
    Clazz selectDetailById(@Param("classId") Long classId);
}
