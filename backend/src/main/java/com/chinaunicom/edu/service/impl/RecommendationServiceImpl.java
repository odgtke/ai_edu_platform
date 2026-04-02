package com.chinaunicom.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chinaunicom.edu.entity.*;
import com.chinaunicom.edu.mapper.*;
import com.chinaunicom.edu.service.CourseService;
import com.chinaunicom.edu.service.ABTestService;
import com.chinaunicom.edu.service.RecommendationService;
import com.chinaunicom.edu.service.RecommendationWeightService;
import com.chinaunicom.edu.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 个性推荐服务实现类
 * 实现混合推荐算法：内容推荐 + 协同过滤 + 热门推荐 + 知识推荐
 */
@Service
public class RecommendationServiceImpl implements RecommendationService {
    
    private static final Logger logger = LoggerFactory.getLogger(RecommendationServiceImpl.class);
    
    /** 最大推荐分数 */
    private static final double MAX_SCORE = 99.99;
    /** 推荐结果过期天数 */
    private static final int EXPIRE_DAYS = 7;
    
    private final UserPreferenceMapper userPreferenceMapper;
    private final UserBehaviorMapper userBehaviorMapper;
    private final RecommendationMapper recommendationMapper;
    private final CourseSimilarityMapper courseSimilarityMapper;
    private final UserRatingMapper userRatingMapper;
    private final LearningPathMapper learningPathMapper;
    private final CourseService courseService;
    private final RecommendationWeightService weightService;
    private final UserService userService;
    private final ABTestService abTestService;
    private final RedisTemplate<String, Object> redisTemplate;
    
    // 缓存键前缀
    private static final String CACHE_KEY_PREFIX = "recommendation:";
    private static final long CACHE_TTL_HOURS = 24;
    
    public RecommendationServiceImpl(UserPreferenceMapper userPreferenceMapper,
                                     UserBehaviorMapper userBehaviorMapper,
                                     RecommendationMapper recommendationMapper,
                                     CourseSimilarityMapper courseSimilarityMapper,
                                     UserRatingMapper userRatingMapper,
                                     LearningPathMapper learningPathMapper,
                                     CourseService courseService,
                                     RecommendationWeightService weightService,
                                     UserService userService,
                                     ABTestService abTestService,
                                     RedisTemplate<String, Object> redisTemplate) {
        this.userPreferenceMapper = userPreferenceMapper;
        this.userBehaviorMapper = userBehaviorMapper;
        this.recommendationMapper = recommendationMapper;
        this.courseSimilarityMapper = courseSimilarityMapper;
        this.userRatingMapper = userRatingMapper;
        this.learningPathMapper = learningPathMapper;
        this.courseService = courseService;
        this.weightService = weightService;
        this.userService = userService;
        this.abTestService = abTestService;
        this.redisTemplate = redisTemplate;
    }
    
    @Override
    @Cacheable(value = "recommendations", key = "#userId + ':' + #limit")
    @Transactional(rollbackFor = Exception.class)
    public List<Map<String, Object>> generateRecommendations(Long userId, Integer limit) {
        try {
            logger.info("Generating personalized recommendations for user: {}", userId);
            
            // 1. 先检查Redis缓存
            String cacheKey = CACHE_KEY_PREFIX + userId + ":" + limit;
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cachedRecommendations = (List<Map<String, Object>>) redisTemplate.opsForValue().get(cacheKey);
            if (cachedRecommendations != null && !cachedRecommendations.isEmpty()) {
                logger.info("Returning cached recommendations for user: {}", userId);
                return cachedRecommendations;
            }
            
            // 2. 获取用户A/B测试组
            String abGroup = abTestService.getUserGroup(userId);
            logger.info("User {} is in A/B test group: {}", userId, abGroup);
            
            // 3. 根据A/B测试组使用不同的推荐策略
            List<Map<String, Object>> recommendations;
            switch (abGroup) {
                case "B":
                    // B组：使用改进的混合推荐（更多协同过滤）
                    recommendations = generateImprovedRecommendations(userId, limit);
                    break;
                case "C":
                    // C组：使用知识图谱增强推荐
                    recommendations = generateKnowledgeEnhancedRecommendations(userId, limit);
                    break;
                case "A":
                default:
                    // A组：使用标准混合推荐
                    recommendations = generateHybridRecommendations(userId, limit);
                    break;
            }
            
            // 4. 记录A/B测试展示
            for (Map<String, Object> rec : recommendations) {
                Long courseId = (Long) rec.get("itemId");
                abTestService.recordImpression(userId, abGroup, courseId);
            }
            
            // 5. 异步保存推荐结果到数据库
            asyncSaveRecommendations(userId, recommendations);
            
            // 6. 写入Redis缓存
            redisTemplate.opsForValue().set(cacheKey, recommendations, CACHE_TTL_HOURS, TimeUnit.HOURS);
            
            return recommendations;
        } catch (Exception e) {
            logger.error("Error generating recommendations for user {}: {}", userId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 异步生成推荐（用于后台预计算）
     * 使用 recommendationExecutor 线程池
     */
    @Async("recommendationExecutor")
    public CompletableFuture<List<Map<String, Object>>> generateRecommendationsAsync(Long userId, Integer limit) {
        logger.info("Async generating recommendations for user: {} on thread: {}", 
            userId, Thread.currentThread().getName());
        List<Map<String, Object>> recommendations = generateRecommendations(userId, limit);
        return CompletableFuture.completedFuture(recommendations);
    }
    
    /**
     * 异步保存推荐结果
     * 使用 taskExecutor 线程池
     */
    @Async("taskExecutor")
    public void asyncSaveRecommendations(Long userId, List<Map<String, Object>> recommendations) {
        try {
            saveRecommendations(userId, recommendations);
            logger.debug("Async saved recommendations for user: {} on thread: {}", 
                userId, Thread.currentThread().getName());
        } catch (Exception e) {
            logger.error("Error async saving recommendations for user {}: {}", userId, e.getMessage());
        }
    }
    
    /**
     * 批量异步生成推荐（高并发场景）
     * 使用 CompletableFuture 并行处理多个用户
     */
    public List<CompletableFuture<List<Map<String, Object>>>> generateRecommendationsBatch(
            List<Long> userIds, Integer limit) {
        return userIds.stream()
            .map(userId -> generateRecommendationsAsync(userId, limit))
            .collect(Collectors.toList());
    }
    
    /**
     * 并行获取多个用户的推荐（带超时控制）
     */
    public Map<Long, List<Map<String, Object>>> getRecommendationsParallel(
            List<Long> userIds, Integer limit, long timeoutSeconds) {
        Map<Long, List<Map<String, Object>>> result = new HashMap<>();
        
        List<CompletableFuture<Void>> futures = userIds.stream()
            .map(userId -> generateRecommendationsAsync(userId, limit)
                .thenAccept(recs -> result.put(userId, recs))
                .exceptionally(ex -> {
                    logger.error("Failed to generate recommendations for user {}: {}", 
                        userId, ex.getMessage());
                    result.put(userId, new ArrayList<>());
                    return null;
                }))
            .collect(Collectors.toList());
        
        // 等待所有任务完成，带超时
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("Timeout or error waiting for recommendations: {}", e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 内部类：简单的键值对
     */
    private static class Pair<K, V> {
        private final K key;
        private final V value;
        
        Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }
        
        K getKey() { return key; }
        V getValue() { return value; }
    }
    
    /**
     * 清除用户推荐缓存
     */
    @CacheEvict(value = "recommendations", key = "#userId + ':*'")
    public void clearUserRecommendationCache(Long userId) {
        logger.info("Cleared recommendation cache for user: {}", userId);
    }

    /**
     * 清除所有推荐缓存
     */
    @CacheEvict(value = "recommendations", allEntries = true)
    public void clearAllRecommendationCache() {
        logger.info("Cleared all recommendation cache");
    }
    
    @Override
    public List<Map<String, Object>> generateRecommendationsByType(Long userId, String type, Integer limit) {
        try {
            List<Map<String, Object>> recommendations;
            
            switch (type.toLowerCase()) {
                case "content":
                    recommendations = getContentBasedRecommendations(userId, limit);
                    break;
                case "collaborative":
                    recommendations = getCollaborativeRecommendations(userId, limit);
                    break;
                case "trending":
                    recommendations = getTrendingCourses(limit);
                    break;
                case "knowledge":
                    recommendations = getKnowledgeBasedRecommendations(userId, limit);
                    break;
                default:
                    recommendations = generateRecommendations(userId, limit);
            }
            
            return recommendations;
        } catch (Exception e) {
            logger.error("Error generating {} recommendations for user {}: {}", type, userId, e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Map<String, Object>> getTrendingCourses(Integer limit) {
        try {
            // 基于用户行为统计的热门课程推荐
            List<UserBehavior> behaviors = userBehaviorMapper.findRecentByUserId(1L, 1000);
            
            Map<Long, Double> courseScores = new HashMap<>();
            Map<Long, Integer> courseViews = new HashMap<>();
            
            // 统计各课程的行为分数
            for (UserBehavior behavior : behaviors) {
                if (behavior != null && behavior.getCourseId() != null && behavior.getBehaviorValue() != null) {
                    Long courseId = behavior.getCourseId();
                    Double score = courseScores.getOrDefault(courseId, 0.0);
                    Integer views = courseViews.getOrDefault(courseId, 0);
                    
                    // 不同行为类型有不同的权重
                    double weight = getBehaviorWeight(behavior.getBehaviorType());
                    courseScores.put(courseId, score + behavior.getBehaviorValue().doubleValue() * weight);
                    courseViews.put(courseId, views + 1);
                }
            }
            
            // 获取真实课程数据
            Set<Long> courseIds = courseScores.keySet();
            Map<Long, Course> courseMap = getCourseDetails(courseIds);
            
            // 构建推荐结果，关联真实课程数据
            List<Map<String, Object>> trendingCourses = courseScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    Long courseId = entry.getKey();
                    Course realCourse = courseMap.get(courseId);
                    
                    Map<String, Object> course = new HashMap<>();
                    course.put("itemId", courseId);
                    course.put("itemType", "course");
                    course.put("score", Math.min(entry.getValue(), MAX_SCORE) / 100.0); // 转换为0-1范围
                    course.put("views", courseViews.get(courseId));
                    course.put("recommendationType", "trending");
                    course.put("reason", "热门课程推荐");
                    
                    // 关联真实课程数据
                    if (realCourse != null) {
                        course.put("itemName", realCourse.getCourseName());
                        course.put("courseDescription", realCourse.getDescription());
                        course.put("courseImage", realCourse.getCoverImage());
                        course.put("courseCategory", getGradeLevelName(realCourse.getGradeLevel()));
                        course.put("teacherId", realCourse.getTeacherId());
                        course.put("totalLessons", realCourse.getTotalLessons());
                    } else {
                        course.put("itemName", "热门课程 " + courseId);
                    }
                    
                    return course;
                })
                .collect(Collectors.toList());
            
            return trendingCourses;
        } catch (Exception e) {
            logger.error("Error getting trending courses: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public void recordUserBehavior(Long userId, Long courseId, String behaviorType, Double behaviorValue) {
        try {
            UserBehavior behavior = new UserBehavior();
            behavior.setUserId(userId);
            behavior.setCourseId(courseId);
            behavior.setBehaviorType(behaviorType);
            behavior.setBehaviorValue(BigDecimal.valueOf(behaviorValue));
            behavior.setIpAddress("127.0.0.1"); // 简化处理
            behavior.setUserAgent("Web Browser");
            behavior.setCreatedTime(LocalDateTime.now());
            
            userBehaviorMapper.insert(behavior);
            logger.info("Recorded user behavior: userId={}, courseId={}, type={}", userId, courseId, behaviorType);
        } catch (Exception e) {
            logger.error("Error recording user behavior: {}", e.getMessage());
        }
    }
    
    @Override
    public void updateUserPreference(Long userId, String preferenceType, String preferenceValue, Double score) {
        try {
            // 先查找是否已存在该偏好
            List<UserPreference> existingPreferences = userPreferenceMapper.findByUserIdAndType(userId, preferenceType);
            boolean found = false;
            
            for (UserPreference pref : existingPreferences) {
                if (pref.getPreferenceValue().equals(preferenceValue)) {
                    // 更新现有偏好分数
                    pref.setPreferenceScore(BigDecimal.valueOf(score));
                    userPreferenceMapper.updateScore(pref);
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                // 创建新的偏好记录
                UserPreference preference = new UserPreference();
                preference.setUserId(userId);
                preference.setPreferenceType(preferenceType);
                preference.setPreferenceValue(preferenceValue);
                preference.setPreferenceScore(BigDecimal.valueOf(score));
                preference.setCreatedTime(LocalDateTime.now());
                preference.setUpdatedTime(LocalDateTime.now());
                
                userPreferenceMapper.insert(preference);
            }
            
            logger.info("Updated user preference: userId={}, type={}, value={}", userId, preferenceType, preferenceValue);
        } catch (Exception e) {
            logger.error("Error updating user preference: {}", e.getMessage());
        }
    }
    
    @Override
    public List<Map<String, Object>> getLearningPathRecommendations(Long userId) {
        try {
            // 基于用户偏好和已完成课程推荐学习路径
            List<UserPreference> preferences = userPreferenceMapper.findByUserId(userId);
            List<UserBehavior> behaviors = userBehaviorMapper.findRecentByUserId(userId, 50);
            
            // 分析用户已完成的课程
            Set<Long> completedCourses = behaviors.stream()
                .filter(b -> "complete".equals(b.getBehaviorType()))
                .map(UserBehavior::getCourseId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            
            // 构建学习路径推荐
            List<Map<String, Object>> pathRecommendations = new ArrayList<>();
            
            // 基于偏好的路径推荐
            for (UserPreference preference : preferences) {
                if ("course_category".equals(preference.getPreferenceType())) {
                    Map<String, Object> path = new HashMap<>();
                    path.put("pathName", preference.getPreferenceValue() + "进阶学习路径");
                    path.put("description", "基于您的兴趣推荐的系统性学习路径");
                    path.put("category", preference.getPreferenceValue());
                    path.put("estimatedHours", 40 + (int)(Math.random() * 20)); // 随机时长
                    path.put("recommendationScore", preference.getPreferenceScore().doubleValue() * 100);
                    path.put("completedCount", completedCourses.size());
                    pathRecommendations.add(path);
                }
            }
            
            return pathRecommendations;
        } catch (Exception e) {
            logger.error("Error getting learning path recommendations: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * 改进的混合推荐算法 - B组使用（增加协同过滤权重）
     */
    private List<Map<String, Object>> generateImprovedRecommendations(Long userId, Integer limit) {
        Map<Long, Map<String, Object>> courseScores = new HashMap<>();
        
        // 获取动态权重配置（增加协同过滤权重）
        Map<String, Double> weights = new HashMap<>();
        weights.put("content", 0.25);
        weights.put("collaborative", 0.45); // 增加协同过滤权重
        weights.put("trending", 0.15);
        weights.put("knowledge", 0.15);
        
        // 1. 内容推荐
        List<Map<String, Object>> contentBased = getContentBasedRecommendations(userId, limit * 2);
        addToCourseScores(courseScores, contentBased, weights.get("content"));
        
        // 2. 协同过滤推荐（增强版）
        List<Map<String, Object>> collaborative = getEnhancedCollaborativeRecommendations(userId, limit * 2);
        addToCourseScores(courseScores, collaborative, weights.get("collaborative"));
        
        // 3. 热门课程推荐
        List<Map<String, Object>> trending = getTrendingCourses(limit * 2);
        addToCourseScores(courseScores, trending, weights.get("trending"));
        
        // 4. 知识推荐
        List<Map<String, Object>> knowledgeBased = getKnowledgeBasedRecommendations(userId, limit * 2);
        addToCourseScores(courseScores, knowledgeBased, weights.get("knowledge"));
        
        return finalizeRecommendations(courseScores, limit);
    }
    
    /**
     * 知识图谱增强推荐 - C组使用
     */
    private List<Map<String, Object>> generateKnowledgeEnhancedRecommendations(Long userId, Integer limit) {
        Map<Long, Map<String, Object>> courseScores = new HashMap<>();
        
        // 获取动态权重配置（增加知识推荐权重）
        Map<String, Double> weights = new HashMap<>();
        weights.put("content", 0.20);
        weights.put("collaborative", 0.25);
        weights.put("trending", 0.15);
        weights.put("knowledge", 0.40); // 大幅增加知识推荐权重
        
        // 1. 内容推荐
        List<Map<String, Object>> contentBased = getContentBasedRecommendations(userId, limit * 2);
        addToCourseScores(courseScores, contentBased, weights.get("content"));
        
        // 2. 协同过滤推荐
        List<Map<String, Object>> collaborative = getCollaborativeRecommendations(userId, limit * 2);
        addToCourseScores(courseScores, collaborative, weights.get("collaborative"));
        
        // 3. 热门课程推荐
        List<Map<String, Object>> trending = getTrendingCourses(limit * 2);
        addToCourseScores(courseScores, trending, weights.get("trending"));
        
        // 4. 知识图谱推荐（增强版）
        List<Map<String, Object>> knowledgeBased = getEnhancedKnowledgeRecommendations(userId, limit * 2);
        addToCourseScores(courseScores, knowledgeBased, weights.get("knowledge"));
        
        return finalizeRecommendations(courseScores, limit);
    }
    
    /**
     * 混合推荐算法 - 结合多种推荐策略（支持动态权重）
     */
    private List<Map<String, Object>> generateHybridRecommendations(Long userId, Integer limit) {
        Map<Long, Map<String, Object>> courseScores = new HashMap<>();
        
        // 获取动态权重配置
        Map<String, Double> weights = getDynamicWeights(userId);
        
        // 1. 内容推荐
        List<Map<String, Object>> contentBased = getContentBasedRecommendations(userId, limit * 2);
        addToCourseScores(courseScores, contentBased, weights.get("content"));
        
        // 2. 协同过滤推荐
        List<Map<String, Object>> collaborative = getCollaborativeRecommendations(userId, limit * 2);
        addToCourseScores(courseScores, collaborative, weights.get("collaborative"));
        
        // 3. 热门课程推荐
        List<Map<String, Object>> trending = getTrendingCourses(limit * 2);
        addToCourseScores(courseScores, trending, weights.get("trending"));
        
        // 4. 知识推荐
        List<Map<String, Object>> knowledgeBased = getKnowledgeBasedRecommendations(userId, limit * 2);
        addToCourseScores(courseScores, knowledgeBased, weights.get("knowledge"));
        
        return finalizeRecommendations(courseScores, limit);
    }
    
    /**
     * 统一推荐结果处理
     */
    private List<Map<String, Object>> finalizeRecommendations(Map<Long, Map<String, Object>> courseScores, Integer limit) {
        // 获取所有候选课程的详细信息
        Set<Long> courseIds = courseScores.keySet();
        Map<Long, Course> courseMap = getCourseDetails(courseIds);
        
        // 按分数排序并返回前N个
        return courseScores.values().stream()
            .sorted((a, b) -> {
                Double scoreA = (Double) a.get("totalScore");
                Double scoreB = (Double) b.get("totalScore");
                return Double.compare(scoreB != null ? scoreB : 0.0, scoreA != null ? scoreA : 0.0);
            })
            .limit(limit)
            .map(courseInfo -> {
                Long courseId = (Long) courseInfo.get("courseId");
                Course realCourse = courseMap.get(courseId);
                
                // 计算平均匹配度并添加必要字段
                Double totalScore = (Double) courseInfo.get("totalScore");
                Integer count = (Integer) courseInfo.get("count");
                double avgScore = (count != null && count > 0) ? totalScore / count : 0.0;
                
                // 设置匹配度分数 (0-100)
                double matchScore = Math.min(avgScore, 100.0);
                
                // 创建新的结果Map，使用前端期望的字段名
                Map<String, Object> result = new HashMap<>();
                result.put("itemId", courseId);
                result.put("itemType", "course");
                result.put("score", matchScore / 100.0); // 转换为0-1范围
                result.put("matchScore", Math.round(matchScore));
                result.put("recommendationType", courseInfo.get("recommendationType"));
                result.put("reason", courseInfo.get("reason"));
                
                // 关联真实课程数据
                if (realCourse != null) {
                    result.put("itemName", realCourse.getCourseName());
                    result.put("courseDescription", realCourse.getDescription());
                    result.put("courseImage", realCourse.getCoverImage());
                    result.put("courseCategory", getGradeLevelName(realCourse.getGradeLevel()));
                    result.put("teacherId", realCourse.getTeacherId());
                    result.put("totalLessons", realCourse.getTotalLessons());
                    result.put("credit", realCourse.getCredit());
                } else {
                    // 设置默认课程名称
                    String courseName = (String) courseInfo.get("courseName");
                    result.put("itemName", courseName != null ? courseName : "推荐课程 " + courseId);
                }
                
                return result;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * 增强版协同过滤推荐 - 使用用户相似度计算
     */
    private List<Map<String, Object>> getEnhancedCollaborativeRecommendations(Long userId, Integer limit) {
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        try {
            // 1. 获取当前用户的课程评分
            List<UserRating> currentUserRatings = userRatingMapper.findByUserId(userId);
            Map<Long, Double> currentUserCourseRatings = currentUserRatings.stream()
                .filter(r -> r.getCourseId() != null && r.getRating() != null)
                .collect(Collectors.toMap(
                    UserRating::getCourseId,
                    r -> r.getRating().doubleValue(),
                    (existing, replacement) -> existing
                ));
            
            // 2. 查找相似用户（基于共同评分课程）
            Map<Long, Double> userSimilarities = new HashMap<>();
            List<UserRating> allRatings = userRatingMapper.findRecentRatings(1000);
            
            Map<Long, List<UserRating>> ratingsByUser = allRatings.stream()
                .filter(r -> r.getUserId() != null && !r.getUserId().equals(userId))
                .collect(Collectors.groupingBy(UserRating::getUserId));
            
            for (Map.Entry<Long, List<UserRating>> entry : ratingsByUser.entrySet()) {
                Long otherUserId = entry.getKey();
                List<UserRating> otherRatings = entry.getValue();
                
                // 计算余弦相似度
                double similarity = calculateCosineSimilarity(currentUserCourseRatings, otherRatings);
                if (similarity > 0.3) { // 只保留相似度大于0.3的用户
                    userSimilarities.put(otherUserId, similarity);
                }
            }
            
            // 3. 基于相似用户推荐课程
            Map<Long, Double> courseScores = new HashMap<>();
            Map<Long, Double> courseWeights = new HashMap<>();
            
            List<Map.Entry<Long, Double>> topSimilarUsers = userSimilarities.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(20)
                .collect(Collectors.toList());
            
            for (Map.Entry<Long, Double> similarUser : topSimilarUsers) {
                Long similarUserId = similarUser.getKey();
                Double similarity = similarUser.getValue();
                
                List<UserRating> similarUserRatings = ratingsByUser.get(similarUserId);
                if (similarUserRatings != null) {
                    for (UserRating rating : similarUserRatings) {
                        if (rating.getCourseId() != null && rating.getRating() != null) {
                            // 跳过已评分课程
                            if (!currentUserCourseRatings.containsKey(rating.getCourseId())) {
                                Long courseId = rating.getCourseId();
                                double weightedRating = rating.getRating().doubleValue() * similarity;
                                
                                courseScores.merge(courseId, weightedRating, Double::sum);
                                courseWeights.merge(courseId, similarity, Double::sum);
                            }
                        }
                    }
                }
            }
            
            // 4. 计算加权平均分数并构建推荐结果
            for (Map.Entry<Long, Double> entry : courseScores.entrySet()) {
                Long courseId = entry.getKey();
                Double totalScore = entry.getValue();
                Double totalWeight = courseWeights.get(courseId);
                
                if (totalWeight != null && totalWeight > 0) {
                    double avgScore = totalScore / totalWeight;
                    Course course = courseService.getById(courseId);
                    
                    if (course != null) {
                        Map<String, Object> rec = new HashMap<>();
                        rec.put("courseId", courseId);
                        rec.put("score", Math.min(avgScore * 20, MAX_SCORE)); // 缩放到0-100
                        rec.put("recommendationType", "collaborative_filtering");
                        rec.put("reason", "与您兴趣相似的用户也喜欢");
                        rec.put("courseName", course.getCourseName());
                        rec.put("courseDescription", course.getDescription());
                        rec.put("courseImage", course.getCoverImage());
                        rec.put("courseCategory", getGradeLevelName(course.getGradeLevel()));
                        rec.put("teacherId", course.getTeacherId());
                        rec.put("totalLessons", course.getTotalLessons());
                        recommendations.add(rec);
                    }
                }
            }
            
            // 按分数排序
            recommendations.sort((a, b) -> {
                Double scoreA = (Double) a.get("score");
                Double scoreB = (Double) b.get("score");
                return Double.compare(scoreB, scoreA);
            });
            
            if (recommendations.size() > limit) {
                recommendations = recommendations.subList(0, limit);
            }
            
        } catch (Exception e) {
            logger.error("Error in enhanced collaborative filtering: {}", e.getMessage(), e);
        }
        
        return recommendations;
    }
    
    /**
     * 计算余弦相似度
     */
    private double calculateCosineSimilarity(Map<Long, Double> user1Ratings, List<UserRating> user2Ratings) {
        Map<Long, Double> user2RatingMap = user2Ratings.stream()
            .filter(r -> r.getCourseId() != null && r.getRating() != null)
            .collect(Collectors.toMap(
                UserRating::getCourseId,
                r -> r.getRating().doubleValue(),
                (existing, replacement) -> existing
            ));
        
        // 找到共同评分的课程
        Set<Long> commonCourses = new HashSet<>(user1Ratings.keySet());
        commonCourses.retainAll(user2RatingMap.keySet());
        
        if (commonCourses.isEmpty()) {
            return 0.0;
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (Long courseId : commonCourses) {
            double rating1 = user1Ratings.get(courseId);
            double rating2 = user2RatingMap.get(courseId);
            dotProduct += rating1 * rating2;
        }
        
        for (double rating : user1Ratings.values()) {
            norm1 += rating * rating;
        }
        
        for (double rating : user2RatingMap.values()) {
            norm2 += rating * rating;
        }
        
        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
    
    /**
     * 增强版知识推荐 - 使用更复杂的知识图谱分析
     */
    private List<Map<String, Object>> getEnhancedKnowledgeRecommendations(Long userId, Integer limit) {
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        try {
            // 1. 获取用户已完成课程
            List<UserBehavior> behaviors = userBehaviorMapper.findRecentByUserId(userId, 50);
            Set<Long> completedCourses = behaviors.stream()
                .filter(b -> "complete".equals(b.getBehaviorType()))
                .map(UserBehavior::getCourseId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            
            // 2. 获取已完成课程的详细信息
            Map<Long, Course> completedCourseMap = getCourseDetails(completedCourses);
            
            // 3. 构建知识关联图
            Map<Long, Double> courseScores = new HashMap<>();
            Map<Long, String> courseReasons = new HashMap<>();
            
            // 4. 基于课程相似性矩阵计算推荐
            for (Course completedCourse : completedCourseMap.values()) {
                List<CourseSimilarity> similarities = courseSimilarityMapper
                    .findByCourseId(completedCourse.getCourseId(), 10);
                
                for (CourseSimilarity similarity : similarities) {
                    if (similarity.getSimilarityScore() != null) {
                        Long relatedCourseId = similarity.getCourse1Id().equals(completedCourse.getCourseId()) 
                            ? similarity.getCourse2Id() : similarity.getCourse1Id();
                        
                        // 跳过已完成课程
                        if (!completedCourses.contains(relatedCourseId)) {
                            double score = similarity.getSimilarityScore().doubleValue() * 50;
                            courseScores.merge(relatedCourseId, score, Double::sum);
                            courseReasons.put(relatedCourseId, 
                                "与《" + completedCourse.getCourseName() + "》知识关联度高");
                        }
                    }
                }
            }
            
            // 5. 基于年级进阶路径推荐
            for (Course completedCourse : completedCourseMap.values()) {
                if (completedCourse.getGradeLevel() != null && completedCourse.getSubjectId() != null) {
                    // 推荐同科目高一年级的课程
                    QueryWrapper<Course> wrapper = new QueryWrapper<>();
                    wrapper.eq("status", 1)
                           .eq("subject_id", completedCourse.getSubjectId())
                           .eq("grade_level", completedCourse.getGradeLevel() + 1);
                    
                    List<Course> advancedCourses = courseService.list(wrapper);
                    for (Course advanced : advancedCourses) {
                        if (!completedCourses.contains(advanced.getCourseId())) {
                            courseScores.merge(advanced.getCourseId(), 35.0, Double::sum);
                            courseReasons.put(advanced.getCourseId(), 
                                "《" + completedCourse.getCourseName() + "》的进阶课程");
                        }
                    }
                }
            }
            
            // 6. 基于学习路径推荐
            List<LearningPath> paths = learningPathMapper.findByUserId(userId);
            for (LearningPath path : paths) {
                if (path.getCourseIds() != null && path.getCompletionRate() != null) {
                    String[] pathCourseIds = path.getCourseIds().split(",");
                    double completionRate = path.getCompletionRate().doubleValue();
                    
                    for (String courseIdStr : pathCourseIds) {
                        try {
                            Long courseId = Long.parseLong(courseIdStr.trim());
                            if (!completedCourses.contains(courseId)) {
                                // 根据路径完成率调整分数
                                double score = (100 - completionRate) * 0.3;
                                courseScores.merge(courseId, score, Double::sum);
                                courseReasons.put(courseId, 
                                    "学习路径《" + path.getPathName() + "》推荐");
                            }
                        } catch (NumberFormatException e) {
                            // 忽略解析错误
                        }
                    }
                }
            }
            
            // 7. 排序并构建推荐结果
            List<Map.Entry<Long, Double>> sortedCourses = courseScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());
            
            for (Map.Entry<Long, Double> entry : sortedCourses) {
                Long courseId = entry.getKey();
                Course course = courseService.getById(courseId);
                
                if (course != null) {
                    Map<String, Object> rec = new HashMap<>();
                    rec.put("courseId", courseId);
                    rec.put("score", Math.min(entry.getValue(), MAX_SCORE));
                    rec.put("recommendationType", "knowledge_based");
                    rec.put("reason", courseReasons.getOrDefault(courseId, "基于知识图谱推荐"));
                    rec.put("courseName", course.getCourseName());
                    rec.put("courseDescription", course.getDescription());
                    rec.put("courseImage", course.getCoverImage());
                    rec.put("courseCategory", getGradeLevelName(course.getGradeLevel()));
                    rec.put("teacherId", course.getTeacherId());
                    rec.put("totalLessons", course.getTotalLessons());
                    recommendations.add(rec);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error in enhanced knowledge recommendations: {}", e.getMessage(), e);
        }
        
        return recommendations;
    }
    
    /**
     * 内容推荐算法 - 基于用户偏好和课程标签
     */
    private List<Map<String, Object>> getContentBasedRecommendations(Long userId, Integer limit) {
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        try {
            // 获取用户偏好
            List<UserPreference> preferences = userPreferenceMapper.findByUserId(userId);
            
            // 获取所有有效课程
            QueryWrapper<Course> wrapper = new QueryWrapper<>();
            wrapper.eq("status", 1);
            List<Course> allCourses = courseService.list(wrapper);
            
            if (allCourses.isEmpty()) {
                return recommendations;
            }
            
            // 基于用户年级偏好推荐相关课程
            Set<Integer> preferredGrades = preferences.stream()
                .filter(p -> "grade_level".equals(p.getPreferenceType()))
                .map(p -> {
                    try {
                        return Integer.parseInt(p.getPreferenceValue());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            
            // 计算每门课程的匹配分数
            for (Course course : allCourses) {
                double score = 0;
                String reason = "基于课程热度推荐";
                
                // 年级匹配加分
                if (preferredGrades.contains(course.getGradeLevel())) {
                    score += 30;
                    reason = "符合您的年级偏好";
                }
                
                // 根据偏好类型加分
                for (UserPreference pref : preferences) {
                    if (pref.getPreferenceScore() != null) {
                        // 学科偏好匹配
                        if ("subject".equals(pref.getPreferenceType()) && 
                            course.getSubjectId() != null &&
                            course.getSubjectId().toString().equals(pref.getPreferenceValue())) {
                            score += pref.getPreferenceScore().doubleValue() * 20;
                            reason = "符合您的学科偏好";
                        }
                    }
                }
                
                // 基础分数（确保有分数）
                if (score == 0) {
                    score = 50 + Math.random() * 20;
                }
                
                Map<String, Object> rec = new HashMap<>();
                rec.put("courseId", course.getCourseId());
                rec.put("score", Math.min(score, MAX_SCORE));
                rec.put("recommendationType", "content_based");
                rec.put("reason", reason);
                rec.put("courseName", course.getCourseName());
                rec.put("courseDescription", course.getDescription());
                rec.put("courseImage", course.getCoverImage());
                rec.put("courseCategory", getGradeLevelName(course.getGradeLevel()));
                rec.put("teacherId", course.getTeacherId());
                rec.put("totalLessons", course.getTotalLessons());
                
                recommendations.add(rec);
            }
            
            // 按分数排序并限制数量
            recommendations.sort((a, b) -> {
                Double scoreA = (Double) a.get("score");
                Double scoreB = (Double) b.get("score");
                return Double.compare(scoreB, scoreA);
            });
            
            if (recommendations.size() > limit) {
                recommendations = recommendations.subList(0, limit);
            }
            
        } catch (Exception e) {
            logger.error("Error in content-based recommendation: {}", e.getMessage(), e);
        }
        
        return recommendations;
    }
    
    /**
     * 协同过滤推荐算法 - 基于相似用户行为
     */
    private List<Map<String, Object>> getCollaborativeRecommendations(Long userId, Integer limit) {
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        try {
            // 查找当前用户的行为模式
            List<UserBehavior> currentUserBehaviors = userBehaviorMapper.findRecentByUserId(userId, 20);
            
            // 获取当前用户已学习的课程
            Set<Long> currentUserCourses = currentUserBehaviors.stream()
                .map(UserBehavior::getCourseId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            
            // 获取所有有效课程
            QueryWrapper<Course> wrapper = new QueryWrapper<>();
            wrapper.eq("status", 1);
            if (!currentUserCourses.isEmpty()) {
                wrapper.notIn("course_id", currentUserCourses);
            }
            List<Course> candidateCourses = courseService.list(wrapper);
            
            if (candidateCourses.isEmpty()) {
                return recommendations;
            }
            
            // 基于用户评分数据计算协同过滤分数
            Map<Long, Double> courseScores = new HashMap<>();
            
            for (Course course : candidateCourses) {
                double score = 60; // 基础分数
                
                // 获取该课程的用户评分
                List<UserRating> ratings = userRatingMapper.findByCourseId(course.getCourseId());
                if (ratings != null && !ratings.isEmpty()) {
                    double avgRating = ratings.stream()
                        .filter(r -> r.getRating() != null)
                        .mapToDouble(r -> r.getRating().doubleValue())
                        .average()
                        .orElse(0.0);
                    
                    // 根据平均评分调整分数
                    score += avgRating * 8; // 评分范围1-5，贡献0-40分
                    
                    // 根据评分人数调整（热门度）
                    int ratingCount = ratings.size();
                    if (ratingCount >= 10) {
                        score += 10;
                    } else if (ratingCount >= 5) {
                        score += 5;
                    }
                }
                
                // 根据课程完成人数加分
                List<UserBehavior> courseBehaviors = userBehaviorMapper.findByCourseId(course.getCourseId());
                long completeCount = courseBehaviors.stream()
                    .filter(b -> "complete".equals(b.getBehaviorType()))
                    .count();
                if (completeCount >= 5) {
                    score += 10;
                }
                
                courseScores.put(course.getCourseId(), Math.min(score, MAX_SCORE));
            }
            
            // 排序并选择前N个
            List<Map.Entry<Long, Double>> sortedCourses = courseScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());
            
            // 构建推荐结果，关联真实课程数据
            for (Map.Entry<Long, Double> entry : sortedCourses) {
                Long courseId = entry.getKey();
                Course course = courseService.getById(courseId);
                
                if (course != null) {
                    Map<String, Object> rec = new HashMap<>();
                    rec.put("courseId", courseId);
                    rec.put("score", entry.getValue());
                    rec.put("recommendationType", "collaborative_filtering");
                    rec.put("reason", "相似用户也喜欢这门课程");
                    rec.put("courseName", course.getCourseName());
                    rec.put("courseDescription", course.getDescription());
                    rec.put("courseImage", course.getCoverImage());
                    rec.put("courseCategory", getGradeLevelName(course.getGradeLevel()));
                    rec.put("teacherId", course.getTeacherId());
                    rec.put("totalLessons", course.getTotalLessons());
                    recommendations.add(rec);
                }
            }
        } catch (Exception e) {
            logger.error("Error in collaborative filtering: {}", e.getMessage(), e);
        }
        
        return recommendations;
    }
    
    /**
     * 知识推荐算法 - 基于课程关联性和先修要求
     */
    private List<Map<String, Object>> getKnowledgeBasedRecommendations(Long userId, Integer limit) {
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        try {
            // 基于用户已完成课程的知识图谱推荐
            List<UserBehavior> behaviors = userBehaviorMapper.findRecentByUserId(userId, 30);
            
            Set<Long> completedCourses = behaviors.stream()
                .filter(b -> "complete".equals(b.getBehaviorType()))
                .map(UserBehavior::getCourseId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            
            // 获取已完成课程的详细信息
            Map<Long, Course> completedCourseMap = getCourseDetails(completedCourses);
            
            // 获取所有有效课程作为候选
            QueryWrapper<Course> wrapper = new QueryWrapper<>();
            wrapper.eq("status", 1);
            if (!completedCourses.isEmpty()) {
                wrapper.notIn("course_id", completedCourses);
            }
            List<Course> candidateCourses = courseService.list(wrapper);
            
            // 计算每门候选课程的知识关联分数
            Map<Long, Double> courseScores = new HashMap<>();
            Map<Long, String> courseReasons = new HashMap<>();
            
            for (Course candidate : candidateCourses) {
                double score = 50; // 基础分数
                String reason = "基于知识图谱推荐";
                
                // 1. 基于课程相似性表
                List<CourseSimilarity> similarities = courseSimilarityMapper.findByCourseId(candidate.getCourseId(), 5);
                if (similarities != null) {
                    for (CourseSimilarity similarity : similarities) {
                        if (similarity != null && similarity.getSimilarityScore() != null) {
                            Long relatedCourseId = similarity.getCourse1Id().equals(candidate.getCourseId()) ? 
                                similarity.getCourse2Id() : similarity.getCourse1Id();
                            
                            // 如果相似课程是用户已完成的，增加分数
                            if (completedCourses.contains(relatedCourseId)) {
                                score += similarity.getSimilarityScore().doubleValue() * 30;
                                Course completedCourse = completedCourseMap.get(relatedCourseId);
                                if (completedCourse != null) {
                                    reason = "与《" + completedCourse.getCourseName() + "》相关知识";
                                }
                            }
                        }
                    }
                }
                
                // 2. 基于年级进阶推荐（推荐高一年级的课程）
                for (Course completed : completedCourseMap.values()) {
                    if (completed.getGradeLevel() != null && candidate.getGradeLevel() != null) {
                        int gradeDiff = candidate.getGradeLevel() - completed.getGradeLevel();
                        if (gradeDiff == 1 && completed.getSubjectId() != null && 
                            completed.getSubjectId().equals(candidate.getSubjectId())) {
                            // 同科目高一年级 + 20分
                            score += 20;
                            reason = "《" + completed.getCourseName() + "》的进阶课程";
                        }
                    }
                }
                
                // 3. 基于学习路径推荐
                List<LearningPath> paths = learningPathMapper.findByUserId(userId);
                if (paths != null) {
                    for (LearningPath path : paths) {
                        // 检查课程ID是否在学习路径的courseIds中
                        String courseIdsStr = path.getCourseIds();
                        if (courseIdsStr != null && courseIdsStr.contains(candidate.getCourseId().toString())) {
                            // 根据路径完成率调整分数
                            if (path.getCompletionRate() != null) {
                                score += path.getCompletionRate().doubleValue() * 0.2; // 完成率贡献0-20分
                                reason = "符合您的学习路径《" + path.getPathName() + "》规划";
                            }
                        }
                    }
                }
                
                if (score > 50) { // 只保留有实际关联的课程
                    courseScores.put(candidate.getCourseId(), Math.min(score, MAX_SCORE));
                    courseReasons.put(candidate.getCourseId(), reason);
                }
            }
            
            // 排序并选择前N个
            List<Map.Entry<Long, Double>> sortedCourses = courseScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());
            
            // 构建推荐结果，关联真实课程数据
            for (Map.Entry<Long, Double> entry : sortedCourses) {
                Long courseId = entry.getKey();
                Course course = courseService.getById(courseId);
                
                if (course != null) {
                    Map<String, Object> rec = new HashMap<>();
                    rec.put("courseId", courseId);
                    rec.put("score", entry.getValue());
                    rec.put("recommendationType", "knowledge_based");
                    rec.put("reason", courseReasons.getOrDefault(courseId, "基于您已完成课程的相关推荐"));
                    rec.put("courseName", course.getCourseName());
                    rec.put("courseDescription", course.getDescription());
                    rec.put("courseImage", course.getCoverImage());
                    rec.put("courseCategory", getGradeLevelName(course.getGradeLevel()));
                    rec.put("teacherId", course.getTeacherId());
                    rec.put("totalLessons", course.getTotalLessons());
                    recommendations.add(rec);
                }
            }
        } catch (Exception e) {
            logger.error("Error in knowledge-based recommendation: {}", e.getMessage(), e);
        }
        
        return recommendations;
    }
    
    /**
     * 获取动态权重配置
     * 根据用户类型和活跃度返回不同的权重配置
     */
    private Map<String, Double> getDynamicWeights(Long userId) {
        // 判断是否为冷启动用户
        if (isColdStartUser(userId)) {
            logger.info("User {} is cold start, using cold start weights", userId);
            return weightService.getColdStartWeights();
        }
        
        // 获取用户类型
        User user = userService.getById(userId);
        if (user != null && user.getUserType() != null) {
            // 根据用户类型获取基础权重
            Map<String, Double> weights = weightService.getWeightsByUserType(user.getUserType());
            
            // 根据活跃度微调权重
            Integer activityScore = calculateActivityScore(userId);
            Map<String, Double> activityWeights = weightService.getWeightsByActivity(activityScore);
            
            // 合并权重（用户类型权重60% + 活跃度权重40%）
            Map<String, Double> finalWeights = new HashMap<>();
            for (String key : weights.keySet()) {
                double weight = weights.get(key) * 0.6 + activityWeights.get(key) * 0.4;
                finalWeights.put(key, weight);
            }
            
            logger.info("User {} dynamic weights: {}", userId, finalWeights);
            return finalWeights;
        }
        
        // 默认返回学生权重
        return weightService.getWeightsByUserType(1);
    }
    
    /**
     * 判断是否为冷启动用户（行为数据少于5条）
     */
    private boolean isColdStartUser(Long userId) {
        // 获取用户最近100条行为记录，判断是否少于5条
        List<UserBehavior> behaviors = userBehaviorMapper.findRecentByUserId(userId, 100);
        return behaviors.size() < 5;
    }
    
    /**
     * 计算用户活跃度分数(0-100)
     */
    private Integer calculateActivityScore(Long userId) {
        // 基于最近30天的行为数据计算活跃度
        List<UserBehavior> recentBehaviors = userBehaviorMapper.findRecentByUserId(userId, 100);
        
        // 过滤30天内的记录
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long recentCount = recentBehaviors.stream()
            .filter(b -> b.getCreatedTime() != null && b.getCreatedTime().isAfter(thirtyDaysAgo))
            .count();
        
        // 活跃度计算：行为数 * 10，最高100
        return (int) Math.min(100, recentCount * 10);
    }
    
    /**
     * 将推荐结果添加到课程分数映射中
     */
    private void addToCourseScores(Map<Long, Map<String, Object>> courseScores,
                                 List<Map<String, Object>> recommendations, 
                                 double weight) {
        for (Map<String, Object> rec : recommendations) {
            Long courseId = (Long) rec.get("courseId");
            Double score = (Double) rec.get("score");
                
            if (courseId != null && score != null) {
                Map<String, Object> courseInfo = courseScores.computeIfAbsent(courseId, k -> {
                    Map<String, Object> newInfo = new HashMap<>();
                    newInfo.put("courseId", courseId);
                    newInfo.put("totalScore", 0.0);
                    newInfo.put("count", 0);
                    return newInfo;
                });
                    
                Double currentTotal = (Double) courseInfo.get("totalScore");
                Integer count = (Integer) courseInfo.get("count");
                    
                courseInfo.put("totalScore", currentTotal + score * weight);
                courseInfo.put("count", count + 1);
                courseInfo.put("recommendationType", rec.get("recommendationType"));
                courseInfo.put("reason", rec.get("reason"));
                    
                // 保存课程名称（如果存在）
                if (rec.get("courseName") != null) {
                    courseInfo.put("courseName", rec.get("courseName"));
                }
                if (rec.get("courseCategory") != null) {
                    courseInfo.put("courseCategory", rec.get("courseCategory"));
                }
            }
        }
    }
    
    /**
     * 获取行为类型的权重
     */
    private double getBehaviorWeight(String behaviorType) {
        if (behaviorType == null) {
            return 1.0;
        }
        switch (behaviorType.toLowerCase()) {
            case "complete": return 3.0;
            case "study": return 2.0;
            case "favorite": return 2.5;
            case "view": return 1.0;
            case "browse": return 0.5;
            default: return 1.0;
        }
    }
    
    /**
     * 保存推荐结果到数据库
     */
    private void saveRecommendations(Long userId, List<Map<String, Object>> recommendations) {
        try {
            for (Map<String, Object> rec : recommendations) {
                Recommendation recommendation = new Recommendation();
                recommendation.setUserId(userId);
                recommendation.setCourseId((Long) rec.get("courseId"));
                recommendation.setRecommendationType((String) rec.get("recommendationType"));
                Double score = (Double) rec.get("score");
                recommendation.setRecommendationScore(BigDecimal.valueOf(Math.min(score != null ? score : 50.0, MAX_SCORE)));
                recommendation.setRecommendationReason((String) rec.get("reason"));
                recommendation.setIsClicked(false);
                recommendation.setIsEnrolled(false);
                recommendation.setCreatedTime(LocalDateTime.now());
                recommendation.setExpiredTime(LocalDateTime.now().plusDays(EXPIRE_DAYS));
                
                recommendationMapper.insert(recommendation);
            }
        } catch (Exception e) {
            logger.error("Error saving recommendations: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 批量获取课程详细信息
     */
    private Map<Long, Course> getCourseDetails(Set<Long> courseIds) {
        Map<Long, Course> courseMap = new HashMap<>();
        if (courseIds == null || courseIds.isEmpty()) {
            return courseMap;
        }
        
        try {
            for (Long courseId : courseIds) {
                Course course = courseService.getById(courseId);
                if (course != null) {
                    courseMap.put(courseId, course);
                }
            }
        } catch (Exception e) {
            logger.error("Error getting course details: {}", e.getMessage(), e);
        }
        
        return courseMap;
    }
    
    /**
     * 获取年级名称
     */
    private String getGradeLevelName(Integer gradeLevel) {
        if (gradeLevel == null) {
            return "未分类";
        }
        switch (gradeLevel) {
            case 1: return "小学";
            case 2: return "初中";
            case 3: return "高中";
            case 4: return "大学";
            default: return "其他";
        }
    }
}