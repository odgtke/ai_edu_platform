package com.chinaunicom.edu.mapper;

import com.chinaunicom.edu.entity.UserBehavior;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserBehaviorMapper {
    
    @Insert("INSERT INTO user_behaviors (user_id, course_id, behavior_type, behavior_value, ip_address, user_agent) " +
            "VALUES (#{userId}, #{courseId}, #{behaviorType}, #{behaviorValue}, #{ipAddress}, #{userAgent})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserBehavior behavior);
    
    @Select("SELECT * FROM user_behaviors WHERE user_id = #{userId} ORDER BY created_time DESC LIMIT #{limit}")
    List<UserBehavior> findRecentByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);
    
    @Select("SELECT * FROM user_behaviors WHERE course_id = #{courseId} AND behavior_type = #{behaviorType}")
    List<UserBehavior> findByCourseAndBehavior(@Param("courseId") Long courseId, @Param("behaviorType") String behaviorType);
    
    @Select("SELECT * FROM user_behaviors WHERE course_id = #{courseId}")
    List<UserBehavior> findByCourseId(@Param("courseId") Long courseId);
    
    @Select("SELECT COUNT(*) FROM user_behaviors WHERE user_id = #{userId} AND behavior_type = #{behaviorType}")
    int countByUserAndBehavior(@Param("userId") Long userId, @Param("behaviorType") String behaviorType);
}