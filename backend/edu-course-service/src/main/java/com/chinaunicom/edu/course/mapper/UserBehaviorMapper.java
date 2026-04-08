package com.chinaunicom.edu.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinaunicom.edu.course.entity.UserBehavior;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserBehaviorMapper extends BaseMapper<UserBehavior> {
    
    /**
     * 查询用户行为
     */
    @Select("SELECT * FROM user_behaviors WHERE user_id = #{userId} ORDER BY created_time DESC")
    List<UserBehavior> findByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户对特定课程的行为
     */
    @Select("SELECT * FROM user_behaviors WHERE user_id = #{userId} AND course_id = #{courseId}")
    List<UserBehavior> findByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);
    
    /**
     * 查询学习某课程的所有用户
     */
    @Select("SELECT DISTINCT user_id FROM user_behaviors WHERE course_id = #{courseId} AND behavior_type = 'study'")
    List<Long> findLearnersByCourse(@Param("courseId") Long courseId);
    
    /**
     * 查询最近热门课程
     */
    @Select("SELECT course_id FROM user_behaviors WHERE created_time > DATE_SUB(NOW(), INTERVAL #{days} DAY) AND behavior_type = 'view' GROUP BY course_id ORDER BY COUNT(*) DESC LIMIT #{limit}")
    List<Long> findTrendingCourses(@Param("days") int days, @Param("limit") int limit);
    
    /**
     * 批量插入
     */
    void batchInsert(@Param("behaviors") List<UserBehavior> behaviors);
}
