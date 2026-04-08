package com.chinaunicom.edu.course.strategy;

import com.chinaunicom.edu.course.entity.RecommendationResult;
import com.chinaunicom.edu.course.entity.UserBehavior;
import com.chinaunicom.edu.course.mapper.UserBehaviorMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CollaborativeFilteringStrategy implements RecommendationStrategy {
    
    @Autowired
    private UserBehaviorMapper userBehaviorMapper;
    
    @Override
    public String getStrategyName() {
        return "collaborative";
    }
    
    @Override
    public List<RecommendationResult> recommend(Long userId, int limit) {
        log.info("协同过滤推荐，userId: {}, limit: {}", userId, limit);
        
        // 1. 获取目标用户的行为向量
        Map<Long, Double> userVector = getUserBehaviorVector(userId);
        
        if (userVector.isEmpty()) {
            log.warn("用户 {} 无行为数据，无法使用协同过滤", userId);
            return Collections.emptyList();
        }
        
        // 2. 找到相似用户（简化版：找有共同课程的用户）
        List<SimilarUser> similarUsers = findSimilarUsers(userId, userVector, 20);
        
        // 3. 聚合相似用户的偏好
        Map<Long, Double> candidateScores = new HashMap<>();
        
        for (SimilarUser similarUser : similarUsers) {
            List<UserBehavior> behaviors = userBehaviorMapper.findByUserId(similarUser.getUserId());
            
            for (UserBehavior behavior : behaviors) {
                Long courseId = behavior.getCourseId();
                // 过滤已学过的课程
                if (!userVector.containsKey(courseId)) {
                    double score = behavior.getBehaviorValue() * similarUser.getSimilarity();
                    candidateScores.merge(courseId, score, Double::sum);
                }
            }
        }
        
        // 4. 转换为推荐结果
        return candidateScores.entrySet().stream()
                .map(entry -> RecommendationResult.builder()
                        .courseId(entry.getKey())
                        .score(entry.getValue())
                        .finalScore(entry.getValue())
                        .source(getStrategyName())
                        .reason("与您相似的用户都在学")
                        .generatedTime(LocalDateTime.now())
                        .build())
                .sorted(Comparator.comparing(RecommendationResult::getScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户行为向量
     */
    private Map<Long, Double> getUserBehaviorVector(Long userId) {
        List<UserBehavior> behaviors = userBehaviorMapper.findByUserId(userId);
        Map<Long, Double> vector = new HashMap<>();
        
        for (UserBehavior behavior : behaviors) {
            vector.merge(behavior.getCourseId(), behavior.getBehaviorValue(), Double::sum);
        }
        
        return vector;
    }
    
    /**
     * 查找相似用户（简化实现）
     */
    private List<SimilarUser> findSimilarUsers(Long userId, Map<Long, Double> userVector, int limit) {
        // 实际项目中应该使用更高效的算法，如LSH、MinHash等
        // 这里简化处理：找有共同课程的用户
        
        Set<Long> targetCourses = userVector.keySet();
        Map<Long, Double> userSimilarities = new HashMap<>();
        
        // 对每个目标课程，找到学习过的其他用户
        for (Long courseId : targetCourses) {
            List<Long> learners = userBehaviorMapper.findLearnersByCourse(courseId);
            for (Long learnerId : learners) {
                if (!learnerId.equals(userId)) {
                    userSimilarities.merge(learnerId, 1.0, Double::sum);
                }
            }
        }
        
        // 转换为相似用户列表并排序
        return userSimilarities.entrySet().stream()
                .map(entry -> new SimilarUser(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(SimilarUser::getSimilarity).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * 相似用户内部类
     */
    private static class SimilarUser {
        private final Long userId;
        private final Double similarity;
        
        public SimilarUser(Long userId, Double similarity) {
            this.userId = userId;
            this.similarity = similarity;
        }
        
        public Long getUserId() { return userId; }
        public Double getSimilarity() { return similarity; }
    }
}
