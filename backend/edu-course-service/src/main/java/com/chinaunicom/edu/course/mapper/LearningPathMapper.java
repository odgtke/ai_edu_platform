package com.chinaunicom.edu.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinaunicom.edu.course.entity.LearningPath;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LearningPathMapper extends BaseMapper<LearningPath> {
    
    /**
     * 查询用户的学习路径
     */
    @Select("SELECT * FROM learning_paths WHERE user_id = #{userId} ORDER BY created_time DESC")
    List<LearningPath> findByUserId(@Param("userId") Long userId);
    
    /**
     * 查询活跃的学习路径
     */
    @Select("SELECT * FROM learning_paths WHERE user_id = #{userId} AND path_status = 'active'")
    List<LearningPath> findActivePaths(@Param("userId") Long userId);
}
