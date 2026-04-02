package com.chinaunicom.edu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinaunicom.edu.entity.Assessment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评估Mapper接口
 */
@Mapper
public interface AssessmentMapper extends BaseMapper<Assessment> {
    
    /**
     * 根据学科ID查询评估列表
     */
    @Select("SELECT * FROM edu_assessment WHERE subject_id = #{subjectId} AND status = 1 ORDER BY create_time DESC")
    List<Assessment> selectBySubjectId(@Param("subjectId") Long subjectId);
    
    /**
     * 根据年级查询评估列表
     */
    @Select("SELECT * FROM edu_assessment WHERE grade_level = #{gradeLevel} AND status = 1 ORDER BY create_time DESC")
    List<Assessment> selectByGradeLevel(@Param("gradeLevel") Integer gradeLevel);
    
    /**
     * 查询当前可参加的评估
     */
    @Select("SELECT * FROM edu_assessment WHERE status = 1 AND start_time <= NOW() AND end_time >= NOW() ORDER BY create_time DESC")
    List<Assessment> selectCurrentAssessments();
    
    /**
     * 查询用户历史评估记录
     */
    @Select("SELECT a.*, ua.total_score, ua.score_percentage, ua.is_passed " +
            "FROM edu_assessment a " +
            "JOIN edu_user_assessment ua ON a.assessment_id = ua.assessment_id " +
            "WHERE ua.user_id = #{userId} " +
            "ORDER BY ua.create_time DESC")
    List<Assessment> selectUserHistory(@Param("userId") Long userId);
    
    /**
     * 查询指定时间范围内开始的考试（用于预热）
     */
    @Select("SELECT * FROM edu_assessment WHERE status = 1 AND start_time BETWEEN #{startTime} AND #{endTime}")
    List<Assessment> selectStartingBetween(@Param("startTime") LocalDateTime startTime, 
                                           @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询热门考试（参与人数最多的）
     */
    @Select("SELECT a.*, COUNT(ua.user_assessment_id) as participant_count " +
            "FROM edu_assessment a " +
            "LEFT JOIN edu_user_assessment ua ON a.assessment_id = ua.assessment_id " +
            "WHERE a.status = 1 " +
            "GROUP BY a.assessment_id " +
            "ORDER BY participant_count DESC " +
            "LIMIT #{limit}")
    List<Assessment> selectHotAssessments(@Param("limit") int limit);
}