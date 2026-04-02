package com.chinaunicom.edu.service.impl;

import com.chinaunicom.edu.service.RecommendationWeightService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 推荐权重配置服务实现类
 */
@Service
public class RecommendationWeightServiceImpl implements RecommendationWeightService {

    /**
     * 学生权重：更注重内容推荐和知识推荐
     */
    private static final Map<String, Double> STUDENT_WEIGHTS = createWeightMap(0.35, 0.30, 0.20, 0.15);

    /**
     * 教师权重：更注重热门和协同过滤
     */
    private static final Map<String, Double> TEACHER_WEIGHTS = createWeightMap(0.25, 0.35, 0.25, 0.15);

    /**
     * 管理员权重：均衡分布
     */
    private static final Map<String, Double> ADMIN_WEIGHTS = createWeightMap(0.25, 0.25, 0.25, 0.25);

    /**
     * 冷启动权重：更依赖热门和内容推荐
     */
    private static final Map<String, Double> COLD_START_WEIGHTS = createWeightMap(0.40, 0.10, 0.40, 0.10);
    
    /**
     * 创建权重Map
     */
    private static Map<String, Double> createWeightMap(double content, double collaborative, 
                                                        double trending, double knowledge) {
        Map<String, Double> weights = new HashMap<String, Double>();
        weights.put("content", content);
        weights.put("collaborative", collaborative);
        weights.put("trending", trending);
        weights.put("knowledge", knowledge);
        return weights;
    }

    @Override
    public Map<String, Double> getWeightsByUserType(Integer userType) {
        switch (userType) {
            case 1: // 学生
                return new HashMap<>(STUDENT_WEIGHTS);
            case 2: // 教师
                return new HashMap<>(TEACHER_WEIGHTS);
            case 3: // 管理员
                return new HashMap<>(ADMIN_WEIGHTS);
            default:
                return new HashMap<>(STUDENT_WEIGHTS);
        }
    }

    @Override
    public Map<String, Double> getColdStartWeights() {
        return new HashMap<>(COLD_START_WEIGHTS);
    }

    @Override
    public Map<String, Double> getWeightsByActivity(Integer activityScore) {
        // 活跃度低时，增加热门推荐权重
        // 活跃度高时，增加协同过滤权重
        Map<String, Double> weights = new HashMap<>();

        if (activityScore < 30) {
            // 低活跃度：热门40% + 内容35% + 协同15% + 知识10%
            weights.put("trending", 0.40);
            weights.put("content", 0.35);
            weights.put("collaborative", 0.15);
            weights.put("knowledge", 0.10);
        } else if (activityScore < 70) {
            // 中活跃度：协同35% + 内容30% + 热门20% + 知识15%
            weights.put("collaborative", 0.35);
            weights.put("content", 0.30);
            weights.put("trending", 0.20);
            weights.put("knowledge", 0.15);
        } else {
            // 高活跃度：协同45% + 内容25% + 知识20% + 热门10%
            weights.put("collaborative", 0.45);
            weights.put("content", 0.25);
            weights.put("knowledge", 0.20);
            weights.put("trending", 0.10);
        }

        return weights;
    }
}
