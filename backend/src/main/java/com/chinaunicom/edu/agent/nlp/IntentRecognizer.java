package com.chinaunicom.edu.agent.nlp;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 意图识别器
 * 基于规则和关键词的轻量级意图识别
 */
@Component
public class IntentRecognizer {
    
    // 关键词映射
    private final Map<IntentType, List<String>> intentKeywords = new HashMap<>();
    
    public IntentRecognizer() {
        initIntentKeywords();
    }
    
    /**
     * 初始化意图关键词映射
     */
    private void initIntentKeywords() {
        // 问候类
        intentKeywords.put(IntentType.GREETING, Arrays.asList(
            "你好", "您好", "hello", "hi", "早上好", "下午好", "晚上好"
        ));
        
        // 课程查询
        intentKeywords.put(IntentType.COURSE_QUERY, Arrays.asList(
            "课程", "课", "有哪些课", "有什么课", "课程列表", "课程信息"
        ));
        
        // 课程推荐
        intentKeywords.put(IntentType.COURSE_RECOMMEND, Arrays.asList(
            "推荐", "推荐课程", "适合我的", "给我推荐", "学什么好"
        ));
        
        // 开始学习
        intentKeywords.put(IntentType.LEARNING_START, Arrays.asList(
            "开始学习", "我要学习", "学习", "看课", "上课"
        ));
        
        // 学习进度
        intentKeywords.put(IntentType.LEARNING_PROGRESS, Arrays.asList(
            "进度", "学到哪里了", "完成了多少", "学习进度", "百分比"
        ));
        
        // 提问
        intentKeywords.put(IntentType.QA_ASK, Arrays.asList(
            "怎么", "为什么", "是什么", "请问", "请教", "?", "？"
        ));
        
        // 帮助
        intentKeywords.put(IntentType.SYSTEM_HELP, Arrays.asList(
            "帮助", "help", "怎么用", "使用说明", "教程"
        ));
    }
    
    /**
     * 识别用户输入的意图
     */
    public Intent recognize(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Intent.unknown();
        }
        
        String cleanText = text.trim().toLowerCase();
        double maxScore = 0.0;
        IntentType bestIntent = IntentType.UNKNOWN;
        
        // 计算每个意图的匹配分数
        for (Map.Entry<IntentType, List<String>> entry : intentKeywords.entrySet()) {
            IntentType intentType = entry.getKey();
            List<String> keywords = entry.getValue();
            
            double score = calculateMatchScore(cleanText, keywords);
            if (score > maxScore) {
                maxScore = score;
                bestIntent = intentType;
            }
        }
        
        // 如果置信度太低，认为是未知意图
        if (maxScore < 0.3) {
            return Intent.unknown();
        }
        
        Intent intent = new Intent(bestIntent, bestIntent.getName(), maxScore);
        intent.setRawText(text);
        
        // 提取实体信息
        extractEntities(text, intent);
        
        return intent;
    }
    
    /**
     * 计算文本与关键词的匹配分数
     */
    private double calculateMatchScore(String text, List<String> keywords) {
        int matchCount = 0;
        int totalKeywords = keywords.size();
        
        for (String keyword : keywords) {
            if (text.contains(keyword.toLowerCase())) {
                matchCount++;
            }
        }
        
        return (double) matchCount / totalKeywords;
    }
    
    /**
     * 提取实体信息
     */
    private void extractEntities(String text, Intent intent) {
        // 提取数字（可能是课程ID、章节号等）
        Pattern numberPattern = Pattern.compile("\\d+");
        java.util.regex.Matcher matcher = numberPattern.matcher(text);
        if (matcher.find()) {
            intent.addEntity("number", matcher.group());
        }
        
        // 提取课程相关词汇
        extractCourseEntities(text, intent);
    }
    
    /**
     * 提取课程相关实体
     */
    private void extractCourseEntities(String text, Intent intent) {
        String[] subjects = {"数学", "语文", "英语", "物理", "化学", "生物", "历史", "地理", "政治"};
        for (String subject : subjects) {
            if (text.contains(subject)) {
                intent.addEntity("subject", subject);
                break;
            }
        }
        
        String[] grades = {"一年级", "二年级", "三年级", "四年级", "五年级", "六年级", 
                          "初一", "初二", "初三", "高一", "高二", "高三"};
        for (String grade : grades) {
            if (text.contains(grade)) {
                intent.addEntity("grade", grade);
                break;
            }
        }
    }
    
    /**
     * 注册新的意图关键词
     */
    public void registerIntentKeywords(IntentType intentType, List<String> keywords) {
        intentKeywords.put(intentType, keywords);
    }
}
