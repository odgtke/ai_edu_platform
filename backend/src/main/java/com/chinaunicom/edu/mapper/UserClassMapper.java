package com.chinaunicom.edu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinaunicom.edu.entity.UserClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户班级关联 Mapper 接口
 */
@Mapper
public interface UserClassMapper extends BaseMapper<UserClass> {

    /**
     * 根据用户ID查询班级关联列表
     */
    @Select("SELECT uc.*, u.username as user_name, c.class_name, g.grade_name " +
            "FROM edu_user_class uc " +
            "LEFT JOIN edu_user u ON uc.user_id = u.user_id " +
            "LEFT JOIN edu_class c ON uc.class_id = c.class_id " +
            "LEFT JOIN edu_grade g ON c.grade_id = g.grade_id " +
            "WHERE uc.user_id = #{userId}")
    List<UserClass> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据班级ID查询用户关联列表
     */
    @Select("SELECT uc.*, u.username as user_name, c.class_name, g.grade_name " +
            "FROM edu_user_class uc " +
            "LEFT JOIN edu_user u ON uc.user_id = u.user_id " +
            "LEFT JOIN edu_class c ON uc.class_id = c.class_id " +
            "LEFT JOIN edu_grade g ON c.grade_id = g.grade_id " +
            "WHERE uc.class_id = #{classId}")
    List<UserClass> selectByClassId(@Param("classId") Long classId);

    /**
     * 查询用户所在班级ID列表
     */
    @Select("SELECT class_id FROM edu_user_class WHERE user_id = #{userId}")
    List<Long> selectClassIdsByUserId(@Param("userId") Long userId);
}
