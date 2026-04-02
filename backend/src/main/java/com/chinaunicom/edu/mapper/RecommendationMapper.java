package com.chinaunicom.edu.mapper;

import com.chinaunicom.edu.entity.Recommendation;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface RecommendationMapper {
    
    @Insert("INSERT INTO recommendations (user_id, course_id, recommendation_type, recommendation_score, recommendation_reason, expired_time) " +
            "VALUES (#{userId}, #{courseId}, #{recommendationType}, #{recommendationScore}, #{recommendationReason}, #{expiredTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Recommendation recommendation);
    
    @Select("SELECT * FROM recommendations WHERE user_id = #{userId} AND created_time > DATE_SUB(NOW(), INTERVAL 7 DAY) " +
            "ORDER BY recommendation_score DESC LIMIT #{limit}")
    List<Recommendation> findRecentByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);
    
    @Select("SELECT * FROM recommendations WHERE user_id = #{userId} AND recommendation_type = #{type} " +
            "ORDER BY recommendation_score DESC LIMIT #{limit}")
    List<Recommendation> findByUserIdAndType(@Param("userId") Long userId, @Param("type") String type, @Param("limit") Integer limit);
    
    @Update("UPDATE recommendations SET is_clicked = TRUE WHERE id = #{id}")
    int markAsClicked(Long id);
    
    @Update("UPDATE recommendations SET is_enrolled = TRUE WHERE id = #{id}")
    int markAsEnrolled(Long id);
    
    @Delete("DELETE FROM recommendations WHERE expired_time < NOW()")
    int deleteExpired();
}