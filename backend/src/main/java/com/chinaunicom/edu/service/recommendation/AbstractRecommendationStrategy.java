package com.chinaunicom.edu.service.recommendation;

import com.chinaunicom.edu.entity.Course;
import com.chinaunicom.edu.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 推荐策略抽象基类
 * 模板方法模式 + 策略模式：提供通用实现框架
 */
public abstract class AbstractRecommendationStrategy implements RecommendationStrategy {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    protected static final double MAX_SCORE = 99.99;
    
    @Autowired
    protected CourseService courseService;
    
    @Autowired
    private RecommendationStrategyRegistry strategyRegistry;
    
    /**
     * 自动注册到策略注册表
     */
    @PostConstruct
    public void register() {
        strategyRegistry.registerStrategy(this);
        logger.info("Registered recommendation strategy: {}", getStrategyName());
    }
    
    @Override
    public List<Map<String, Object>> generateRecommendations(Long userId, Integer limit) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 1. 获取候选课程
            List<Course> candidates = getCandidateCourses(userId);
            
            // 2. 计算分数并排序
            List<Map<String, Object>> scoredItems = candidates.stream()
                    .map(course -> {
                        double score = calculateScore(userId, course.getCourseId());
                        return buildRecommendationItem(course, score);
                    })
                    .filter(item -> (Double) item.get("score") > 0)
                    .sorted((a, b) -> Double.compare((Double) b.get("score"), (Double) a.get("score")))
                    .limit(limit)
                    .collect(Collectors.toList());
            
            // 3. 后处理（去重、多样性调整等）
            scoredItems = postProcessRecommendations(scoredItems, userId);
            
            logger.debug("{} generated {} recommendations for user {} in {}ms", 
                    getStrategyName(), scoredItems.size(), userId, 
                    System.currentTimeMillis() - startTime);
            
            return scoredItems;
            
        } catch (Exception e) {
            logger.error("Error in {} for user {}", getStrategyName(), userId, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 获取候选课程列表 - 子类可重写
     */
    protected List<Course> getCandidateCourses(Long userId) {
        return courseService.list();
    }
    
    /**
     * 构建推荐项
     */
    protected Map<String, Object> buildRecommendationItem(Course course, double score) {
        Map<String, Object> item = new HashMap<>();
        item.put("itemId", course.getCourseId());
        item.put("itemName", course.getCourseName());
        item.put("itemType", "course");
        item.put("score", Math.min(score, MAX_SCORE));
        item.put("reason", generateReason(course, score));
        item.put("strategy", getStrategyName());
        return item;
    }
    
    /**
     * 生成推荐理由 - 子类实现
     */
    protected abstract String generateReason(Course course, double score);
    
    /**
     * 后处理推荐结果 - 子类可重写
     */
    protected List<Map<String, Object>> postProcessRecommendations(
            List<Map<String, Object>> recommendations, Long userId) {
        return recommendations;
    }
    
    @Override
    public boolean isSupported(Long userId) {
        return true;
    }
}
