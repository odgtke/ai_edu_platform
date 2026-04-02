package com.chinaunicom.edu.mapper;

import com.chinaunicom.edu.entity.UserRating;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserRatingMapper {
    
    @Insert("INSERT INTO user_ratings (user_id, course_id, rating, rating_type) " +
            "VALUES (#{userId}, #{courseId}, #{rating}, #{ratingType})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserRating rating);
    
    @Select("SELECT * FROM user_ratings WHERE user_id = #{userId}")
    List<UserRating> findByUserId(Long userId);
    
    @Select("SELECT * FROM user_ratings WHERE course_id = #{courseId}")
    List<UserRating> findByCourseId(Long courseId);
    
    @Select("SELECT AVG(rating) FROM user_ratings WHERE course_id = #{courseId}")
    Double getAverageRatingByCourseId(Long courseId);
    
    @Select("SELECT * FROM user_ratings WHERE user_id = #{userId} AND course_id = #{courseId}")
    UserRating findByUserAndCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);
    
    @Update("UPDATE user_ratings SET rating = #{rating}, updated_time = NOW() WHERE id = #{id}")
    int updateRating(UserRating rating);
    
    @Select("SELECT * FROM user_ratings ORDER BY created_time DESC LIMIT #{limit}")
    List<UserRating> findRecentRatings(@Param("limit") int limit);
}