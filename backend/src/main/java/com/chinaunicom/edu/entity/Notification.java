package com.chinaunicom.edu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 消息通知实体类
 */
@Data
@TableName("edu_notification")
public class Notification {

    /**
     * 消息ID
     */
    @TableId(type = IdType.AUTO)
    private Long notificationId;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 发送者名称
     */
    private String senderName;

    /**
     * 接收者ID
     */
    private Long receiverId;

    /**
     * 接收类型: user-个人, class-班级, grade-年级, all-全部
     */
    private String receiverType;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型: system-系统, course-课程, assignment-作业, exam-考试
     */
    private String type;

    /**
     * 优先级: 1-普通, 2-重要, 3-紧急
     */
    private Integer priority;

    /**
     * 是否已读: 0-未读, 1-已读
     */
    private Integer isRead;

    /**
     * 阅读时间
     */
    private LocalDateTime readTime;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 消息类型标签（非数据库字段）
     */
    @TableField(exist = false)
    private String typeLabel;

    /**
     * 优先级标签（非数据库字段）
     */
    @TableField(exist = false)
    private String priorityLabel;
}
