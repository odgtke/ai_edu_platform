package com.chinaunicom.edu.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinaunicom.edu.course.entity.UserPreference;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserPreferenceMapper extends BaseMapper<UserPreference> {
    
    /**
     * 根据用户ID查询偏好
     */
    @Select("SELECT * FROM user_preferences WHERE user_id = #{userId}")
    List<UserPreference> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID和偏好类型查询
     */
    @Select("SELECT * FROM user_preferences WHERE user_id = #{userId} AND preference_type = #{type}")
    List<UserPreference> findByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);
}
