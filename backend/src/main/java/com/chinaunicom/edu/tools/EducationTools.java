package com.chinaunicom.edu.tools;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 教育领域工具类 - 提供课程查询、学习建议等功能
 */
@Component
public class EducationTools {

    private final Random random = new Random();

    @Tool("查询用户的学习进度")
    public String getUserLearningProgress(Long userId) {
        // 模拟学习进度数据
        int completedCourses = 5 + random.nextInt(10);
        int totalCourses = 15 + random.nextInt(10);
        double progress = (double) completedCourses / totalCourses * 100;
        
        return String.format("用户ID %d 的学习进度：已完成 %d/%d 门课程，完成率 %.1f%%", 
                           userId, completedCourses, totalCourses, progress);
    }

    @Tool("根据用户兴趣推荐课程")
    public String recommendCoursesByInterest(String interest) {
        List<String> courses;
        
        switch (interest.toLowerCase()) {
            case "编程":
                courses = Arrays.asList("Python编程入门", "Java核心技术", "前端开发实战");
                break;
            case "数学":
                courses = Arrays.asList("高等数学", "线性代数", "概率统计");
                break;
            case "英语":
                courses = Arrays.asList("商务英语", "雅思备考", "口语训练营");
                break;
            default:
                courses = Arrays.asList("学习方法论", "时间管理", "高效学习技巧");
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("根据您的兴趣【").append(interest).append("】，为您推荐以下课程：\n");
        for (int i = 0; i < courses.size(); i++) {
            sb.append((i + 1)).append(". ").append(courses.get(i)).append("\n");
        }
        return sb.toString();
    }

    @Tool("获取学习建议")
    public String getLearningAdvice(String learningArea) {
        String[] advice = {
            "制定明确的学习目标，分解为小步骤",
            "保持每天固定的学习时间",
            "及时复习巩固知识点",
            "积极参与讨论和练习",
            "定期自我检测学习效果"
        };
        
        return "关于【" + learningArea + "】的学习建议：\n" + 
               advice[random.nextInt(advice.length)];
    }

    @Tool("查询课程详情")
    public String getCourseDetails(String courseName) {
        // 模拟课程详情
        return String.format("课程《%s》详情：\n• 课时：20节\n• 难度：中级\n• 适合人群：有一定基础的学习者\n• 学习周期：4-6周\n• 包含：视频讲解、实战练习、在线答疑", 
                           courseName);
    }

    @Tool("评估学习水平")
    public String assessLearningLevel(String subject) {
        String[] levels = {"初级", "中级", "高级"};
        String level = levels[random.nextInt(levels.length)];
        String[] suggestions = {
            "建议从基础知识开始学习",
            "可以尝试更有挑战性的内容",
            "已经掌握得很好，可以考虑深入研究"
        };
        
        return String.format("您的%s水平评估为：%s\n建议：%s", 
                           subject, level, suggestions[random.nextInt(suggestions.length)]);
    }
}