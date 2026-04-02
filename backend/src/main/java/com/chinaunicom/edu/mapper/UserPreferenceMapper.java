package com.chinaunicom.edu.mapper;

import com.chinaunicom.edu.entity.UserPreference;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserPreferenceMapper {
    
    @Insert("INSERT INTO user_preferences (user_id, preference_type, preference_value, preference_score) " +
            "VALUES (#{userId}, #{preferenceType}, #{preferenceValue}, #{preferenceScore})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserPreference preference);
    
    @Select("SELECT * FROM user_preferences WHERE user_id = #{userId}")
    List<UserPreference> findByUserId(Long userId);
    
    @Select("SELECT * FROM user_preferences WHERE user_id = #{userId} AND preference_type = #{preferenceType}")
    List<UserPreference> findByUserIdAndType(@Param("userId") Long userId, @Param("preferenceType") String preferenceType);
    
    @Delete("DELETE FROM user_preferences WHERE id = #{id}")
    int deleteById(Long id);
    
    @Update("UPDATE user_preferences SET preference_score = #{preferenceScore}, updated_time = NOW() " +
            "WHERE id = #{id}")
    int updateScore(UserPreference preference);
}