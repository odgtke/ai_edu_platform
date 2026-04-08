package com.chinaunicom.edu.course.strategy;

import com.chinaunicom.edu.course.entity.Course;
import com.chinaunicom.edu.course.entity.RecommendationResult;
import com.chinaunicom.edu.course.mapper.CourseMapper;
import com.chinaunicom.edu.course.mapper.UserBehaviorMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TrendingStrategy implements RecommendationStrategy {
    
    @Autowired
    private UserBehaviorMapper behaviorMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Override
    public String getStrategyName() {
        return "trending";
    }
    
    @Override
    public List<RecommendationResult> recommend(Long userId, int limit) {
        log.info("热门推荐，userId: {}, limit: {}", userId, limit);
        
        // 查询最近7天最热门的课程
        List<Long> trendingCourses = behaviorMapper.findTrendingCourses(7, limit * 2);
        
        // 如果没有行为数据，返回最新的课程作为默认推荐
        if (trendingCourses == null || trendingCourses.isEmpty()) {
            log.info("没有行为数据，返回最新课程作为默认推荐");
            List<Course> latestCourses = courseMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Course>()
                    .orderByDesc("create_time")
                    .last("LIMIT " + limit)
            );
            
            return latestCourses.stream()
                    .map(course -> RecommendationResult.builder()
                            .courseId(course.getCourseId())
                            .score(1.0)
                            .finalScore(1.0)
                            .source(getStrategyName())
                            .reason("最新上架课程")
                            .generatedTime(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());
        }
        
        return trendingCourses.stream()
                .map(courseId -> RecommendationResult.builder()
                        .courseId(courseId)
                        .score(1.0) // 热门课程基础分
                        .finalScore(1.0)
                        .source(getStrategyName())
                        .reason("近期热门课程")
                        .generatedTime(LocalDateTime.now())
                        .build())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
