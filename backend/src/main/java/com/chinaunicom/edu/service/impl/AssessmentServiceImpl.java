package com.chinaunicom.edu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinaunicom.edu.entity.Assessment;
import com.chinaunicom.edu.entity.AssessmentQuestion;
import com.chinaunicom.edu.entity.UserAssessment;
import com.chinaunicom.edu.mapper.AssessmentMapper;
import com.chinaunicom.edu.mapper.AssessmentQuestionMapper;
import com.chinaunicom.edu.mapper.UserAssessmentMapper;
import com.chinaunicom.edu.service.AssessmentService;
import com.chinaunicom.edu.tools.EducationTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 能力评估服务实现类
 */
@Slf4j
@Service
public class AssessmentServiceImpl extends ServiceImpl<AssessmentMapper, Assessment> implements AssessmentService {
    
    @Autowired
    private AssessmentQuestionMapper assessmentQuestionMapper;
    
    @Autowired
    private UserAssessmentMapper userAssessmentMapper;
    
    @Autowired
    private EducationTools educationTools;
    
    @Override
    public List<Assessment> getAvailableAssessments(Long userId) {
        // 获取当前可参加的评估
        List<Assessment> assessments = baseMapper.selectCurrentAssessments();
        
        // 过滤掉用户已经参加过的评估（如果不允许重复参加）
        return assessments.stream()
                .filter(assessment -> canUserTakeAssessment(userId, assessment.getAssessmentId()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Assessment> getAssessmentsBySubject(Long subjectId) {
        return baseMapper.selectBySubjectId(subjectId);
    }
    
    @Override
    public List<Assessment> getAssessmentsByGrade(Integer gradeLevel) {
        return baseMapper.selectByGradeLevel(gradeLevel);
    }
    
    @Override
    public Map<String, Object> getAssessmentDetail(Long assessmentId) {
        Assessment assessment = baseMapper.selectById(assessmentId);
        if (assessment == null) {
            throw new RuntimeException("评估不存在");
        }
        
        List<AssessmentQuestion> questions = assessmentQuestionMapper.selectByAssessmentId(assessmentId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("assessment", assessment);
        result.put("questions", questions);
        result.put("questionCount", questions.size());
        
        return result;
    }
    
    @Override
    @Transactional
    public UserAssessment startAssessment(Long userId, Long assessmentId) {
        // 检查评估是否存在且可参加
        Assessment assessment = baseMapper.selectById(assessmentId);
        if (assessment == null || assessment.getStatus() != 1) {
            throw new RuntimeException("评估不可参加");
        }
        
        if (!canUserTakeAssessment(userId, assessmentId)) {
            throw new RuntimeException("您没有权限参加此评估");
        }
        
        // 检查是否已有进行中的评估
        List<UserAssessment> inProgress = userAssessmentMapper.selectInProgressByUserId(userId);
        if (!inProgress.isEmpty()) {
            throw new RuntimeException("您还有未完成的评估，请先完成");
        }
        
        // 创建用户评估记录
        UserAssessment userAssessment = new UserAssessment();
        userAssessment.setUserId(userId);
        userAssessment.setAssessmentId(assessmentId);
        userAssessment.setStartTime(LocalDateTime.now());
        userAssessment.setStatus(1); // 进行中
        
        userAssessmentMapper.insert(userAssessment);
        
        log.info("用户{}开始参加评估{}", userId, assessmentId);
        return userAssessment;
    }
    
    @Override
    @Transactional
    public UserAssessment submitAnswer(Long userId, Long assessmentId, Map<Integer, String> answers) {
        // 获取用户评估记录
        UserAssessment userAssessment = userAssessmentMapper.selectLatestByUserAndAssessment(userId, assessmentId);
        if (userAssessment == null || userAssessment.getStatus() != 1) {
            throw new RuntimeException("评估状态异常");
        }
        
        // 获取评估题目
        List<AssessmentQuestion> questions = assessmentQuestionMapper.selectByAssessmentId(assessmentId);
        
        // 计算得分
        BigDecimal totalScore = BigDecimal.ZERO;
        List<Map<String, Object>> answerDetails = new ArrayList<>();
        
        for (AssessmentQuestion question : questions) {
            String userAnswer = answers.get(question.getQuestionOrder());
            boolean isCorrect = checkAnswer(question, userAnswer);
            
            Map<String, Object> detail = new HashMap<>();
            detail.put("questionId", question.getQuestionId());
            detail.put("questionOrder", question.getQuestionOrder());
            detail.put("userAnswer", userAnswer);
            detail.put("correctAnswer", question.getCorrectAnswer());
            detail.put("isCorrect", isCorrect);
            detail.put("score", question.getScore());
            
            answerDetails.add(detail);
            
            if (isCorrect) {
                totalScore = totalScore.add(question.getScore());
            }
        }
        
        // 更新评估结果
        LocalDateTime endTime = LocalDateTime.now();
        userAssessment.setEndTime(endTime);
        userAssessment.setTimeSpent(calculateTimeSpent(userAssessment.getStartTime(), endTime));
        userAssessment.setTotalScore(totalScore);
        userAssessment.setScorePercentage(calculatePercentage(totalScore, questions));
        userAssessment.setIsPassed(isPassed(totalScore, questions) ? 1 : 0);
        userAssessment.setAnswerDetails(convertToJson(answerDetails));
        userAssessment.setStatus(2); // 已完成
        
        userAssessmentMapper.updateById(userAssessment);
        
        // 生成能力分析报告
        Map<String, Object> capabilityReport = generateCapabilityReport(userAssessment.getResultId());
        userAssessment.setCapabilityReport(convertToJson(capabilityReport));
        
        log.info("用户{}完成评估{}，得分：{}", userId, assessmentId, totalScore);
        return userAssessment;
    }
    
    @Override
    public List<UserAssessment> getUserAssessmentHistory(Long userId) {
        return userAssessmentMapper.selectByUserId(userId);
    }
    
    @Override
    public Map<String, Object> getAssessmentStatistics(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 参加次数
        Integer totalCount = userAssessmentMapper.countByUserId(userId);
        stats.put("totalCount", totalCount);
        
        // 平均分
        Double averageScore = userAssessmentMapper.getAverageScoreByUserId(userId);
        stats.put("averageScore", averageScore != null ? averageScore : 0);
        
        // 及格率
        List<UserAssessment> history = userAssessmentMapper.selectByUserId(userId);
        if (!history.isEmpty()) {
            long passedCount = history.stream()
                    .filter(ua -> ua.getIsPassed() != null && ua.getIsPassed() == 1)
                    .count();
            double passRate = (double) passedCount / history.size() * 100;
            stats.put("passRate", Math.round(passRate * 100.0) / 100.0);
        } else {
            stats.put("passRate", 0);
        }
        
        return stats;
    }
    
    @Override
    public Map<String, Object> generateCapabilityReport(Long userAssessmentId) {
        UserAssessment userAssessment = userAssessmentMapper.selectById(userAssessmentId);
        if (userAssessment == null) {
            throw new RuntimeException("评估记录不存在");
        }
        
        Assessment assessment = baseMapper.selectById(userAssessment.getAssessmentId());
        List<AssessmentQuestion> questions = assessmentQuestionMapper.selectByAssessmentId(assessment.getAssessmentId());
        
        // 使用现有工具分析能力
        String analysis = educationTools.assessLearningLevel("综合能力");
        
        Map<String, Object> report = new HashMap<>();
        report.put("userId", userAssessment.getUserId());
        report.put("assessmentId", userAssessment.getAssessmentId());
        report.put("totalScore", userAssessment.getTotalScore());
        report.put("scorePercentage", userAssessment.getScorePercentage());
        report.put("isPassed", userAssessment.getIsPassed());
        report.put("analysis", analysis);
        report.put("recommendations", generateRecommendations(analysis));
        report.put("generatedTime", LocalDateTime.now());
        
        return report;
    }
    
    @Override
    public boolean canUserTakeAssessment(Long userId, Long assessmentId) {
        Assessment assessment = baseMapper.selectById(assessmentId);
        if (assessment == null) {
            return false;
        }
        
        // 检查是否允许重复参加
        if (assessment.getAllowRepeat() == 0) {
            UserAssessment existing = userAssessmentMapper.selectLatestByUserAndAssessment(userId, assessmentId);
            if (existing != null) {
                return false;
            }
        }
        
        // 检查时间范围
        LocalDateTime now = LocalDateTime.now();
        if (assessment.getStartTime() != null && now.isBefore(assessment.getStartTime())) {
            return false;
        }
        if (assessment.getEndTime() != null && now.isAfter(assessment.getEndTime())) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public List<UserAssessment> getInProgressAssessments(Long userId) {
        return userAssessmentMapper.selectInProgressByUserId(userId);
    }
    
    // 私有辅助方法
    private boolean checkAnswer(AssessmentQuestion question, String userAnswer) {
        if (userAnswer == null || userAnswer.trim().isEmpty()) {
            return false;
        }
        
        switch (question.getQuestionType()) {
            case 1: // 单选题
            case 3: // 判断题
                return question.getCorrectAnswer().equalsIgnoreCase(userAnswer.trim());
            case 2: // 多选题
                return compareMultipleChoiceAnswers(question.getCorrectAnswer(), userAnswer);
            default:
                return true; // 填空题、简答题需要人工批改
        }
    }
    
    private boolean compareMultipleChoiceAnswers(String correct, String user) {
        Set<String> correctSet = new HashSet<>(Arrays.asList(correct.split(",")));
        Set<String> userSet = new HashSet<>(Arrays.asList(user.split(",")));
        return correctSet.equals(userSet);
    }
    
    private Integer calculateTimeSpent(LocalDateTime start, LocalDateTime end) {
        return (int) java.time.Duration.between(start, end).getSeconds();
    }
    
    private BigDecimal calculatePercentage(BigDecimal score, List<AssessmentQuestion> questions) {
        BigDecimal total = questions.stream()
                .map(AssessmentQuestion::getScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return score.multiply(BigDecimal.valueOf(100)).divide(total, 2, BigDecimal.ROUND_HALF_UP);
    }
    
    private boolean isPassed(BigDecimal score, List<AssessmentQuestion> questions) {
        BigDecimal total = questions.stream()
                .map(AssessmentQuestion::getScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return score.compareTo(total.multiply(BigDecimal.valueOf(0.6))) >= 0;
    }
    
    private String convertToJson(Object obj) {
        // 简单的JSON转换，实际项目中应使用Jackson等库
        return obj.toString();
    }
    
    private List<String> generateRecommendations(String analysis) {
        List<String> recommendations = new ArrayList<>();
        recommendations.add("根据您的表现，建议加强基础知识的学习");
        recommendations.add("可以尝试更多的练习题目");
        recommendations.add("关注错题，查漏补缺");
        return recommendations;
    }
}