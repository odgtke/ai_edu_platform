package com.chinaunicom.edu.component;

import com.chinaunicom.edu.entity.Assessment;
import com.chinaunicom.edu.entity.AssessmentQuestion;
import com.chinaunicom.edu.mapper.AssessmentMapper;
import com.chinaunicom.edu.mapper.AssessmentQuestionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 考试预热组件
 * 提前加载即将开始的考试数据到缓存
 */
@Component
public class AssessmentWarmupComponent {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentWarmupComponent.class);

    @Autowired
    private AssessmentMapper assessmentMapper;

    @Autowired
    private AssessmentQuestionMapper assessmentQuestionMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 缓存键前缀
    private static final String CACHE_KEY_ASSESSMENT = "assessment:warmup:";
    private static final String CACHE_KEY_QUESTIONS = "assessment:questions:";
    private static final String CACHE_KEY_WARMUP_LIST = "assessment:warmup:list";

    // 缓存过期时间（小时）
    private static final long CACHE_EXPIRE_HOURS = 24;

    @PostConstruct
    public void init() {
        logger.info("Assessment warmup component initialized");
        // 启动时执行一次预热
        warmupUpcomingAssessments();
    }

    /**
     * 定时预热任务 - 每5分钟执行一次
     * 预热未来1小时内开始的考试
     */
    @Scheduled(fixedRate = 300000) // 5分钟
    @Async("scheduledExecutor")
    public void warmupUpcomingAssessments() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime oneHourLater = now.plus(1, ChronoUnit.HOURS);
            
            // 查询未来1小时内开始的考试
            List<Assessment> upcomingAssessments = assessmentMapper.selectStartingBetween(now, oneHourLater);
            
            if (upcomingAssessments.isEmpty()) {
                logger.debug("No upcoming assessments to warmup");
                return;
            }
            
            logger.info("Warming up {} upcoming assessments", upcomingAssessments.size());
            
            for (Assessment assessment : upcomingAssessments) {
                warmupAssessment(assessment.getAssessmentId());
            }
            
            // 更新预热列表
            redisTemplate.opsForValue().set(
                CACHE_KEY_WARMUP_LIST,
                upcomingAssessments,
                CACHE_EXPIRE_HOURS,
                TimeUnit.HOURS
            );
            
        } catch (Exception e) {
            logger.error("Error warming up assessments: {}", e.getMessage());
        }
    }

    /**
     * 预热单个考试数据
     */
    public void warmupAssessment(Long assessmentId) {
        try {
            // 1. 缓存考试基本信息
            Assessment assessment = assessmentMapper.selectById(assessmentId);
            if (assessment != null) {
                String assessmentKey = CACHE_KEY_ASSESSMENT + assessmentId;
                redisTemplate.opsForValue().set(
                    assessmentKey,
                    assessment,
                    CACHE_EXPIRE_HOURS,
                    TimeUnit.HOURS
                );
                logger.debug("Warmed up assessment: {}", assessmentId);
            }
            
            // 2. 缓存考试题目
            List<AssessmentQuestion> questions = assessmentQuestionMapper.selectByAssessmentId(assessmentId);
            if (!questions.isEmpty()) {
                String questionsKey = CACHE_KEY_QUESTIONS + assessmentId;
                redisTemplate.opsForValue().set(
                    questionsKey,
                    questions,
                    CACHE_EXPIRE_HOURS,
                    TimeUnit.HOURS
                );
                logger.debug("Warmed up {} questions for assessment: {}", questions.size(), assessmentId);
            }
            
        } catch (Exception e) {
            logger.error("Error warming up assessment {}: {}", assessmentId, e.getMessage());
        }
    }

    /**
     * 获取预热后的考试信息
     */
    public Assessment getWarmupAssessment(Long assessmentId) {
        try {
            String key = CACHE_KEY_ASSESSMENT + assessmentId;
            return (Assessment) redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            logger.error("Error getting warmup assessment: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取预热后的题目列表
     */
    @SuppressWarnings("unchecked")
    public List<AssessmentQuestion> getWarmupQuestions(Long assessmentId) {
        try {
            String key = CACHE_KEY_QUESTIONS + assessmentId;
            return (List<AssessmentQuestion>) redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            logger.error("Error getting warmup questions: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 清除预热缓存
     */
    public void clearWarmupCache(Long assessmentId) {
        try {
            redisTemplate.delete(CACHE_KEY_ASSESSMENT + assessmentId);
            redisTemplate.delete(CACHE_KEY_QUESTIONS + assessmentId);
            logger.info("Cleared warmup cache for assessment: {}", assessmentId);
        } catch (Exception e) {
            logger.error("Error clearing warmup cache: {}", e.getMessage());
        }
    }

    /**
     * 预加载热门考试
     * 用于已开始的考试，防止突发流量
     */
    @Scheduled(cron = "0 0 */6 * * ?") // 每6小时执行一次
    @Async("scheduledExecutor")
    public void warmupHotAssessments() {
        try {
            // 查询进行中的热门考试（参与人数多的）
            List<Assessment> hotAssessments = assessmentMapper.selectHotAssessments(10);
            
            logger.info("Warming up {} hot assessments", hotAssessments.size());
            
            for (Assessment assessment : hotAssessments) {
                warmupAssessment(assessment.getAssessmentId());
            }
            
        } catch (Exception e) {
            logger.error("Error warming up hot assessments: {}", e.getMessage());
        }
    }

    /**
     * 检查考试是否已预热
     */
    public boolean isWarmup(Long assessmentId) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(CACHE_KEY_ASSESSMENT + assessmentId));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 手动触发预热（用于管理后台）
     */
    public boolean manualWarmup(Long assessmentId) {
        try {
            warmupAssessment(assessmentId);
            return true;
        } catch (Exception e) {
            logger.error("Manual warmup failed for assessment {}: {}", assessmentId, e.getMessage());
            return false;
        }
    }
}
