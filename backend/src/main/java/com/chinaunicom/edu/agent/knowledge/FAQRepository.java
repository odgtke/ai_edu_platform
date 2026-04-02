package com.chinaunicom.edu.agent.knowledge;

import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * FAQ知识库
 * 存储常见问题及其答案
 */
@Repository
public class FAQRepository {
    
    // FAQ数据 (问题 -> 答案)
    private final Map<String, String> faqDatabase = new HashMap<>();
    
    // 问题关键词索引 (关键词 -> 问题列表)
    private final Map<String, Set<String>> keywordIndex = new HashMap<>();
    
    public FAQRepository() {
        initFAQData();
    }
    
    /**
     * 初始化FAQ数据
     */
    private void initFAQData() {
        // 课程相关
        addFAQ("如何选择适合的课程？", 
              "您可以根据自己的年级、学科基础和学习目标来选择课程。系统也会根据您的学习记录推荐适合的课程。");
        
        addFAQ("课程可以试听吗？", 
              "大部分课程都提供免费试听章节，您可以在课程详情页面试听后再决定是否报名。");
        
        addFAQ("课程有时间限制吗？", 
              "购买的课程在有效期内可以反复观看学习，具体有效期请查看课程详情说明。");
        
        // 学习相关
        addFAQ("如何查看学习进度？", 
              "在学习中心页面可以查看各门课程的学习进度，包括已学章节、完成练习等情况。");
        
        addFAQ("忘记密码怎么办？", 
              "您可以在登录页面点击'忘记密码'，通过手机号或邮箱找回密码。");
        
        addFAQ("如何联系客服？", 
              "您可以通过以下方式联系我们：\n- 在线客服：工作日 9:00-18:00\n- 客服电话：400-xxx-xxxx\n- 客服邮箱：support@example.com");
        
        // 技术问题
        addFAQ("视频播放不了怎么办？", 
              "请检查：\n1. 网络连接是否正常\n2. 浏览器版本是否过低\n3. 是否安装了广告拦截插件\n如仍有问题，请联系技术支持。");
        
        addFAQ("支持哪些设备学习？", 
              "支持PC、手机、平板等多种设备，推荐使用Chrome、Firefox等现代浏览器获得最佳体验。");
        
        addFAQ("可以下载课程视频吗？", 
              "为了保护版权，课程视频暂不支持下载，但可以离线缓存部分内容以便无网络时学习。");
        
        // 账户相关
        addFAQ("如何修改个人信息？", 
              "登录后进入个人中心，在'基本信息'页面可以修改姓名、头像等个人信息。");
        
        addFAQ("如何注销账户？", 
              "如需注销账户，请联系客服提供身份验证信息，我们会协助您完成注销流程。");
    }
    
    /**
     * 添加FAQ
     */
    private void addFAQ(String question, String answer) {
        faqDatabase.put(question, answer);
        
        // 建立关键词索引
        String[] keywords = extractKeywords(question);
        for (String keyword : keywords) {
            keywordIndex.computeIfAbsent(keyword.toLowerCase(), k -> new HashSet<>()).add(question);
        }
    }
    
    /**
     * 提取关键词
     */
    private String[] extractKeywords(String question) {
        // 移除标点符号和停用词
        String cleanQuestion = question.replaceAll("[？?！!，,。.；;：:]", "");
        
        // 分词（简单实现）
        List<String> keywords = new ArrayList<>();
        
        // 学科关键词
        String[] subjects = {"数学", "语文", "英语", "物理", "化学", "生物", "历史", "地理", "政治"};
        for (String subject : subjects) {
            if (cleanQuestion.contains(subject)) {
                keywords.add(subject);
            }
        }
        
        // 功能关键词
        String[] functions = {"课程", "学习", "进度", "作业", "考试", "测试", "练习", "视频", 
                             "密码", "账户", "客服", "设备", "下载", "播放"};
        for (String func : functions) {
            if (cleanQuestion.contains(func)) {
                keywords.add(func);
            }
        }
        
        return keywords.toArray(new String[0]);
    }
    
    /**
     * 根据问题查找答案
     */
    public String findAnswer(String question) {
        // 1. 精确匹配
        String answer = faqDatabase.get(question);
        if (answer != null) {
            return answer;
        }
        
        // 2. 模糊匹配（基于关键词）
        return fuzzyMatch(question);
    }
    
    /**
     * 模糊匹配
     */
    private String fuzzyMatch(String question) {
        Map<String, Integer> candidateScores = new HashMap<>();
        
        // 提取问题中的关键词
        String[] questionKeywords = extractKeywords(question);
        
        // 计算每个FAQ的匹配分数
        for (String keyword : questionKeywords) {
            Set<String> relatedQuestions = keywordIndex.get(keyword.toLowerCase());
            if (relatedQuestions != null) {
                for (String relatedQuestion : relatedQuestions) {
                    candidateScores.merge(relatedQuestion, 1, Integer::sum);
                }
            }
        }
        
        // 找到最高分的候选
        String bestMatch = null;
        int maxScore = 0;
        
        for (Map.Entry<String, Integer> entry : candidateScores.entrySet()) {
            if (entry.getValue() > maxScore) {
                maxScore = entry.getValue();
                bestMatch = entry.getKey();
            }
        }
        
        // 如果匹配度足够高，返回答案
        if (maxScore >= 2) {  // 至少匹配2个关键词
            return faqDatabase.get(bestMatch);
        }
        
        return null;
    }
    
    /**
     * 获取所有FAQ
     */
    public Map<String, String> getAllFAQs() {
        return new HashMap<>(faqDatabase);
    }
    
    /**
     * 根据类别获取FAQ
     */
    public Map<String, String> getFAQsByCategory(String category) {
        Map<String, String> result = new HashMap<>();
        
        for (Map.Entry<String, String> entry : faqDatabase.entrySet()) {
            if (entry.getKey().contains(category)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        
        return result;
    }
    
    /**
     * 添加新的FAQ（运行时）
     */
    public void addFAQRuntime(String question, String answer) {
        addFAQ(question, answer);
    }
    
    /**
     * 获取FAQ总数
     */
    public int getCount() {
        return faqDatabase.size();
    }
}
