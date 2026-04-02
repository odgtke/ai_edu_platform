package com.chinaunicom.edu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinaunicom.edu.entity.Assessment;
import com.chinaunicom.edu.entity.AssessmentQuestion;
import com.chinaunicom.edu.entity.UserAssessment;

import java.util.List;
import java.util.Map;

/**
 * 能力评估服务接口
 */
public interface AssessmentService extends IService<Assessment> {
    
    /**
     * 获取可参加的评估列表
     */
    List<Assessment> getAvailableAssessments(Long userId);
    
    /**
     * 根据学科获取评估列表
     */
    List<Assessment> getAssessmentsBySubject(Long subjectId);
    
    /**
     * 根据年级获取评估列表
     */
    List<Assessment> getAssessmentsByGrade(Integer gradeLevel);
    
    /**
     * 获取评估详情（包含题目）
     */
    Map<String, Object> getAssessmentDetail(Long assessmentId);
    
    /**
     * 开始评估
     */
    UserAssessment startAssessment(Long userId, Long assessmentId);
    
    /**
     * 提交答案
     */
    UserAssessment submitAnswer(Long userId, Long assessmentId, Map<Integer, String> answers);
    
    /**
     * 获取用户评估历史
     */
    List<UserAssessment> getUserAssessmentHistory(Long userId);
    
    /**
     * 获取评估统计信息
     */
    Map<String, Object> getAssessmentStatistics(Long userId);
    
    /**
     * 生成能力分析报告
     */
    Map<String, Object> generateCapabilityReport(Long userAssessmentId);
    
    /**
     * 检查用户是否有权限参加评估
     */
    boolean canUserTakeAssessment(Long userId, Long assessmentId);
    
    /**
     * 获取用户当前进行中的评估
     */
    List<UserAssessment> getInProgressAssessments(Long userId);
}