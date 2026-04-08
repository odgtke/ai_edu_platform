package com.chinaunicom.edu.course.service;

import com.chinaunicom.edu.course.entity.RecommendationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 推荐解释生成器
 */
@Slf4j
@Component
public class ExplanationGenerator {
    
    /**
     * 解释模板
     */
    private static final Map<String, List<String>> EXPLANATION_TEMPLATES = new HashMap<>();
    
    static {
        // 协同过滤解释
        EXPLANATION_TEMPLATES.put("collaborative", Arrays.asList(
            "与您相似的用户都在学习这门课程",
            "和您学习偏好相似的用户选择了这门课程",
            "基于{count}位相似用户的学习记录推荐"
        ));
        
        // 内容推荐解释
        EXPLANATION_TEMPLATES.put("content", Arrays.asList(
            "这门课程符合您的{preference}偏好",
            "基于您对{preference}的兴趣推荐",
            "与您学过的{category}类课程高度相关"
        ));
        
        // 热门推荐解释
        EXPLANATION_TEMPLATES.put("trending", Arrays.asList(
            "近期热门课程，{count}人正在学习",
            "本周最受欢迎的课程之一",
            "大家都在学的热门课程"
        ));
        
        // 知识推荐解释
        EXPLANATION_TEMPLATES.put("knowledge", Arrays.asList(
            "学习路径的下一门推荐课程",
            "学完{course}后的进阶推荐",
            "构建完整知识体系的推荐课程"
        ));
        
        // 冷启动解释
        EXPLANATION_TEMPLATES.put("cold_start_interest", Arrays.asList(
            "符合您感兴趣的{interest}领域",
            "基于您选择的{interest}兴趣推荐",
            "{interest}领域的优质入门课程"
        ));
        
        EXPLANATION_TEMPLATES.put("cold_start_beginner", Arrays.asList(
            "适合新手入门的课程",
            "零基础也能学会的课程",
            "入门级精品课程"
        ));
    }
    
    /**
     * 生成推荐解释
     */
    public String generateExplanation(RecommendationResult result, Long userId) {
        String source = result.getSource();
        List<String> templates = EXPLANATION_TEMPLATES.getOrDefault(source, 
                Collections.singletonList("为您推荐的课程"));
        
        // 随机选择一个模板
        String template = templates.get(new Random().nextInt(templates.size()));
        
        // 填充变量
        Map<String, String> variables = buildVariables(result, userId);
        return fillTemplate(template, variables);
    }
    
    /**
     * 生成详细的推荐理由
     */
    public DetailedExplanation generateDetailedExplanation(RecommendationResult result, Long userId) {
        String source = result.getSource();
        
        DetailedExplanation explanation = new DetailedExplanation();
        explanation.setMainReason(generateExplanation(result, userId));
        explanation.setConfidence(calculateConfidence(result));
        explanation.setFactors(getFactors(source));
        explanation.setTransparency(getTransparencyLevel(source));
        
        return explanation;
    }
    
    /**
     * 构建变量映射
     */
    private Map<String, String> buildVariables(RecommendationResult result, Long userId) {
        Map<String, String> variables = new HashMap<>();
        variables.put("count", "100+");
        variables.put("preference", "学习");
        variables.put("category", "编程开发");
        variables.put("interest", "编程");
        variables.put("course", "相关课程");
        return variables;
    }
    
    /**
     * 填充模板
     */
    private String fillTemplate(String template, Map<String, String> variables) {
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }
    
    /**
     * 计算置信度
     */
    private double calculateConfidence(RecommendationResult result) {
        double score = result.getFinalScore() != null ? result.getFinalScore() : result.getScore();
        return Math.min(score * 100, 99.9);
    }
    
    /**
     * 获取影响因素
     */
    private List<String> getFactors(String source) {
        switch (source) {
            case "collaborative":
                return Arrays.asList("相似用户行为", "学习历史匹配度", "群体偏好");
            case "content":
                return Arrays.asList("课程标签匹配", "难度适配", "内容相关性");
            case "trending":
                return Arrays.asList("近期热度", "学习人数", "好评率");
            case "knowledge":
                return Arrays.asList("知识连贯性", "前置课程", "学习路径");
            default:
                return Arrays.asList("综合推荐");
        }
    }
    
    /**
     * 获取透明度等级
     */
    private String getTransparencyLevel(String source) {
        switch (source) {
            case "collaborative":
                return "高";
            case "content":
                return "高";
            case "trending":
                return "中";
            case "knowledge":
                return "高";
            default:
                return "中";
        }
    }
    
    /**
     * 详细解释
     */
    public static class DetailedExplanation {
        private String mainReason;
        private double confidence;
        private List<String> factors;
        private String transparency;
        
        public String getMainReason() { return mainReason; }
        public void setMainReason(String mainReason) { this.mainReason = mainReason; }
        
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        
        public List<String> getFactors() { return factors; }
        public void setFactors(List<String> factors) { this.factors = factors; }
        
        public String getTransparency() { return transparency; }
        public void setTransparency(String transparency) { this.transparency = transparency; }
    }
}
