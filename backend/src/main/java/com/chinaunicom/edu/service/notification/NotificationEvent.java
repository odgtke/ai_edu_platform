package com.chinaunicom.edu.service.notification;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 通知事件
 * 观察者模式：定义通知事件的数据结构
 */
public class NotificationEvent {
    
    private final String eventId;
    private final NotificationType type;
    private final Long senderId;
    private final String senderName;
    private final Long receiverId;
    private final String title;
    private final String content;
    private final Integer priority;
    private final LocalDateTime timestamp;
    private final Map<String, Object> metadata;
    
    private NotificationEvent(Builder builder) {
        this.eventId = builder.eventId;
        this.type = builder.type;
        this.senderId = builder.senderId;
        this.senderName = builder.senderName;
        this.receiverId = builder.receiverId;
        this.title = builder.title;
        this.content = builder.content;
        this.priority = builder.priority;
        this.timestamp = builder.timestamp != null ? builder.timestamp : LocalDateTime.now();
        this.metadata = builder.metadata != null ? new HashMap<>(builder.metadata) : new HashMap<>();
    }
    
    public String getEventId() { return eventId; }
    public NotificationType getType() { return type; }
    public Long getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public Long getReceiverId() { return receiverId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Integer getPriority() { return priority; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Map<String, Object> getMetadata() { return new HashMap<>(metadata); }
    
    /**
     * 通知类型枚举
     */
    public enum NotificationType {
        SYSTEM("系统通知"),
        COURSE("课程通知"),
        ASSIGNMENT("作业通知"),
        EXAM("考试通知"),
        MESSAGE("私信消息"),
        REMINDER("提醒通知"),
        ACHIEVEMENT("成就通知");
        
        private final String displayName;
        
        NotificationType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * 建造者模式：构建NotificationEvent
     */
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String eventId;
        private NotificationType type;
        private Long senderId;
        private String senderName;
        private Long receiverId;
        private String title;
        private String content;
        private Integer priority;
        private LocalDateTime timestamp;
        private Map<String, Object> metadata;
        
        public Builder eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }
        
        public Builder type(NotificationType type) {
            this.type = type;
            return this;
        }
        
        public Builder senderId(Long senderId) {
            this.senderId = senderId;
            return this;
        }
        
        public Builder senderName(String senderName) {
            this.senderName = senderName;
            return this;
        }
        
        public Builder receiverId(Long receiverId) {
            this.receiverId = receiverId;
            return this;
        }
        
        public Builder title(String title) {
            this.title = title;
            return this;
        }
        
        public Builder content(String content) {
            this.content = content;
            return this;
        }
        
        public Builder priority(Integer priority) {
            this.priority = priority;
            return this;
        }
        
        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        public Builder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }
        
        public Builder addMetadata(String key, Object value) {
            if (this.metadata == null) {
                this.metadata = new HashMap<>();
            }
            this.metadata.put(key, value);
            return this;
        }
        
        public NotificationEvent build() {
            if (eventId == null) {
                eventId = java.util.UUID.randomUUID().toString();
            }
            return new NotificationEvent(this);
        }
    }
}
