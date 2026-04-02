package com.chinaunicom.edu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinaunicom.edu.entity.UserAssessment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户评估结果Mapper接口
 */
@Mapper
public interface UserAssessmentMapper extends BaseMapper<UserAssessment> {
    
    /**
     * 根据用户ID查询评估结果
     */
    @Select("SELECT * FROM edu_user_assessment WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<UserAssessment> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 根据评估ID查询用户结果
     */
    @Select("SELECT * FROM edu_user_assessment WHERE assessment_id = #{assessmentId} ORDER BY total_score DESC")
    List<UserAssessment> selectByAssessmentId(@Param("assessmentId") Long assessmentId);
    
    /**
     * 查询用户某个评估的最新结果
     */
    @Select("SELECT * FROM edu_user_assessment WHERE user_id = #{userId} AND assessment_id = #{assessmentId} ORDER BY create_time DESC LIMIT 1")
    UserAssessment selectLatestByUserAndAssessment(@Param("userId") Long userId, @Param("assessmentId") Long assessmentId);
    
    /**
     * 查询用户正在进行的评估
     */
    @Select("SELECT * FROM edu_user_assessment WHERE user_id = #{userId} AND status = 1")
    List<UserAssessment> selectInProgressByUserId(@Param("userId") Long userId);
    
    /**
     * 统计用户评估次数
     */
    @Select("SELECT COUNT(*) FROM edu_user_assessment WHERE user_id = #{userId}")
    Integer countByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户平均分
     */
    @Select("SELECT AVG(total_score) FROM edu_user_assessment WHERE user_id = #{userId} AND status = 3")
    Double getAverageScoreByUserId(@Param("userId") Long userId);
}