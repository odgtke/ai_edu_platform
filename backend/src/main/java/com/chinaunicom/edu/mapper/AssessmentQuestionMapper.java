package com.chinaunicom.edu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinaunicom.edu.entity.AssessmentQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 评估题目Mapper接口
 */
@Mapper
public interface AssessmentQuestionMapper extends BaseMapper<AssessmentQuestion> {
    
    /**
     * 根据评估ID查询题目列表
     */
    @Select("SELECT * FROM edu_assessment_question WHERE assessment_id = #{assessmentId} ORDER BY question_order ASC")
    List<AssessmentQuestion> selectByAssessmentId(@Param("assessmentId") Long assessmentId);
    
    /**
     * 根据评估ID和题目序号查询题目
     */
    @Select("SELECT * FROM edu_assessment_question WHERE assessment_id = #{assessmentId} AND question_order = #{questionOrder}")
    AssessmentQuestion selectByAssessmentIdAndOrder(@Param("assessmentId") Long assessmentId, @Param("questionOrder") Integer questionOrder);
    
    /**
     * 随机获取指定数量的题目
     */
    @Select("SELECT * FROM edu_assessment_question WHERE assessment_id = #{assessmentId} ORDER BY RAND() LIMIT #{count}")
    List<AssessmentQuestion> selectRandomQuestions(@Param("assessmentId") Long assessmentId, @Param("count") Integer count);
}