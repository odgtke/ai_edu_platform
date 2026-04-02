package com.chinaunicom.edu.mapper;

import com.chinaunicom.edu.entity.LearningPath;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface LearningPathMapper {
    
    @Insert("INSERT INTO learning_paths (user_id, path_name, path_description, course_ids, path_status, estimated_hours) " +
            "VALUES (#{userId}, #{pathName}, #{pathDescription}, #{courseIds}, #{pathStatus}, #{estimatedHours})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(LearningPath learningPath);
    
    @Select("SELECT * FROM learning_paths WHERE user_id = #{userId} AND path_status = #{status}")
    List<LearningPath> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);
    
    @Select("SELECT * FROM learning_paths WHERE user_id = #{userId}")
    List<LearningPath> findByUserId(Long userId);
    
    @Update("UPDATE learning_paths SET completion_rate = #{completionRate}, updated_time = NOW() WHERE id = #{id}")
    int updateCompletionRate(LearningPath learningPath);
    
    @Update("UPDATE learning_paths SET path_status = #{status}, updated_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    @Update("UPDATE learning_paths SET " +
            "adaptive_level = #{adaptiveLevel}, " +
            "difficulty_factor = #{difficultyFactor}, " +
            "adjustment_reason = #{adjustmentReason}, " +
            "last_adjustment_time = #{lastAdjustmentTime}, " +
            "performance_metrics = #{performanceMetrics}, " +
            "alternative_paths = #{alternativePaths}, " +
            "current_stage_index = #{currentStageIndex}, " +
            "stage_completion_status = #{stageCompletionStatus}, " +
            "updated_time = NOW() " +
            "WHERE id = #{id}")
    int updateById(LearningPath learningPath);
    
    @Select("SELECT * FROM learning_paths WHERE path_status = #{status}")
    List<LearningPath> findByPathStatus(@Param("status") String status);
}