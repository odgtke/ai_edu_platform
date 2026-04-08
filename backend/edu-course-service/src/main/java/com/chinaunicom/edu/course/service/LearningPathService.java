package com.chinaunicom.edu.course.service;

import com.chinaunicom.edu.course.entity.Course;
import com.chinaunicom.edu.course.entity.CourseSimilarity;
import com.chinaunicom.edu.course.entity.LearningPath;
import com.chinaunicom.edu.course.entity.UserBehavior;
import com.chinaunicom.edu.course.mapper.CourseMapper;
import com.chinaunicom.edu.course.mapper.CourseSimilarityMapper;
import com.chinaunicom.edu.course.mapper.LearningPathMapper;
import com.chinaunicom.edu.course.mapper.UserBehaviorMapper;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 学习路径推荐服务
 */
@Slf4j
@Service
public class LearningPathService {
    
    @Autowired
    private LearningPathMapper learningPathMapper;
    
    @Autowired
    private UserBehaviorMapper behaviorMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private CourseSimilarityMapper similarityMapper;
    
    /**
     * 生成个性化学习路径
     */
    public LearningPathRecommendation generateLearningPath(Long userId, String goal) {
        log.info("生成学习路径，userId: {}, goal: {}", userId, goal);
        
        // 1. 获取用户已学课程
        List<Long> learnedCourses = getLearnedCourses(userId);
        
        // 2. 基于目标生成路径
        List<PathNode> path = buildPathByGoal(goal, learnedCourses);
        
        // 3. 计算路径信息
        int totalHours = path.stream().mapToInt(PathNode::getEstimatedHours).sum();
        int totalCourses = path.size();
        
        return LearningPathRecommendation.builder()
                .userId(userId)
                .pathName(goal + "学习路径")
                .pathDescription("为您定制的" + goal + "学习路线")
                .nodes(path)
                .totalCourses(totalCourses)
                .totalHours(totalHours)
                .difficulty(calculateDifficulty(path))
                .generatedTime(LocalDateTime.now())
                .build();
    }
    
    /**
     * 基于目标构建学习路径
     */
    private List<PathNode> buildPathByGoal(String goal, List<Long> learnedCourses) {
        List<PathNode> path = new ArrayList<>();
        
        // 根据目标确定课程序列
        // 这里简化处理，实际应该基于知识图谱
        List<Course> allCourses = courseMapper.selectList(null);
        
        // 过滤已学课程并排序
        List<Course> candidateCourses = allCourses.stream()
                .filter(c -> !learnedCourses.contains(c.getCourseId()))
                .collect(Collectors.toList());
        
        // 按难度排序（初级 -> 中级 -> 高级）
        candidateCourses.sort(Comparator.comparingInt(this::getDifficultyLevel));
        
        // 构建路径节点
        int sequence = 1;
        for (Course course : candidateCourses) {
            path.add(PathNode.builder()
                    .sequence(sequence++)
                    .courseId(course.getCourseId())
                    .courseName(course.getCourseName())
                    .estimatedHours(course.getTotalLessons() != null ? course.getTotalLessons() * 2 : 10)
                    .isCompleted(false)
                    .build());
        }
        
        return path;
    }
    
    /**
     * 获取用户已学课程
     */
    private List<Long> getLearnedCourses(Long userId) {
        List<UserBehavior> behaviors = behaviorMapper.findByUserId(userId);
        return behaviors.stream()
                .filter(b -> Arrays.asList("complete", "study").contains(b.getBehaviorType()))
                .map(UserBehavior::getCourseId)
                .distinct()
                .collect(Collectors.toList());
    }
    
    /**
     * 获取难度等级
     */
    private int getDifficultyLevel(Course course) {
        // 简化处理，实际应该根据课程字段
        return 1; // 1=初级, 2=中级, 3=高级
    }
    
    /**
     * 计算路径难度
     */
    private String calculateDifficulty(List<PathNode> path) {
        int avgHours = path.stream().mapToInt(PathNode::getEstimatedHours).sum() / path.size();
        if (avgHours < 10) return "初级";
        if (avgHours < 20) return "中级";
        return "高级";
    }
    
    /**
     * 获取推荐的学习路径
     */
    public List<LearningPath> getRecommendedPaths(Long userId) {
        return learningPathMapper.findByUserId(userId);
    }
    
    /**
     * 保存学习路径
     */
    public void saveLearningPath(LearningPath path) {
        learningPathMapper.insert(path);
    }
    
    /**
     * 更新路径进度
     */
    public void updatePathProgress(Long pathId, Long courseId, boolean completed) {
        LearningPath path = learningPathMapper.selectById(pathId);
        if (path != null) {
            // 更新完成率
            double completionRate = calculateCompletionRate(path);
            path.setCompletionRate(completionRate);
            path.setUpdatedTime(LocalDateTime.now());
            learningPathMapper.updateById(path);
        }
    }
    
    /**
     * 计算完成率
     */
    private double calculateCompletionRate(LearningPath path) {
        // 简化处理
        return 0.0;
    }
    
    /**
     * 学习路径推荐结果
     */
    @Data
    @Builder
    public static class LearningPathRecommendation {
        private Long userId;
        private String pathName;
        private String pathDescription;
        private List<PathNode> nodes;
        private int totalCourses;
        private int totalHours;
        private String difficulty;
        private LocalDateTime generatedTime;
    }
    
    /**
     * 路径节点
     */
    @Data
    @Builder
    public static class PathNode {
        private int sequence;
        private Long courseId;
        private String courseName;
        private int estimatedHours;
        private boolean isCompleted;
        private String description;
    }
}
