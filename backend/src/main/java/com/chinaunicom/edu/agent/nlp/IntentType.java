package com.chinaunicom.edu.agent.nlp;

/**
 * 意图类型枚举
 */
public enum IntentType {
    
    // 问候类
    GREETING("greeting", "问候", "打招呼、寒暄"),
    
    // 课程相关
    COURSE_QUERY("course.query", "课程查询", "查询课程信息"),
    COURSE_RECOMMEND("course.recommend", "课程推荐", "推荐适合的课程"),
    COURSE_ENROLL("course.enroll", "课程报名", "报名参加课程"),
    
    // 学习相关
    LEARNING_START("learning.start", "开始学习", "开始学习某个内容"),
    LEARNING_PROGRESS("learning.progress", "学习进度", "查询学习进度"),
    LEARNING_HISTORY("learning.history", "学习历史", "查看学习记录"),
    
    // 问答相关
    QA_ASK("qa.ask", "提问", "提出问题寻求解答"),
    QA_EXPLAIN("qa.explain", "解释", "请求详细解释"),
    
    // 作业相关
    HOMEWORK_SUBMIT("homework.submit", "提交作业", "提交已完成的作业"),
    HOMEWORK_CHECK("homework.check", "检查作业", "检查作业完成情况"),
    
    // 评估相关
    ASSESSMENT_START("assessment.start", "开始评估", "开始进行能力评估"),
    ASSESSMENT_RESULT("assessment.result", "评估结果", "查看评估结果"),
    
    // 推荐相关
    RESOURCE_RECOMMEND("resource.recommend", "资源推荐", "推荐学习资源"),
    EXERCISE_RECOMMEND("exercise.recommend", "练习推荐", "推荐练习题目"),
    
    // 系统操作
    SYSTEM_HELP("system.help", "帮助", "请求使用帮助"),
    SYSTEM_FEEDBACK("system.feedback", "反馈", "提供反馈意见"),
    SYSTEM_SETTING("system.setting", "设置", "修改个人设置"),
    
    // 其他
    UNKNOWN("unknown", "未知", "无法识别的意图");
    
    private final String code;
    private final String name;
    private final String description;
    
    IntentType(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static IntentType fromCode(String code) {
        for (IntentType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
