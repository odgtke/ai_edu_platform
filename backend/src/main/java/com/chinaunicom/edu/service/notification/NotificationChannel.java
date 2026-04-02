package com.chinaunicom.edu.service.notification;

/**
 * 通知渠道接口
 * 观察者模式：定义通知渠道的规范
 */
public interface NotificationChannel {
    
    /**
     * 获取渠道名称
     */
    String getChannelName();
    
    /**
     * 获取渠道类型
     */
    ChannelType getChannelType();
    
    /**
     * 发送通知
     * @param event 通知事件
     * @return 发送结果
     */
    ChannelResult send(NotificationEvent event);
    
    /**
     * 是否支持该事件类型
     * @param event 通知事件
     * @return 是否支持
     */
    boolean supports(NotificationEvent event);
    
    /**
     * 渠道是否可用
     */
    boolean isAvailable();
    
    /**
     * 渠道类型枚举
     */
    enum ChannelType {
        IN_APP("应用内"),
        EMAIL("邮件"),
        SMS("短信"),
        PUSH("推送"),
        WEBSOCKET("WebSocket");
        
        private final String displayName;
        
        ChannelType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * 发送结果
     */
    class ChannelResult {
        private final boolean success;
        private final String message;
        private final String messageId;
        
        private ChannelResult(boolean success, String message, String messageId) {
            this.success = success;
            this.message = message;
            this.messageId = messageId;
        }
        
        public static ChannelResult success(String messageId) {
            return new ChannelResult(true, null, messageId);
        }
        
        public static ChannelResult failure(String message) {
            return new ChannelResult(false, message, null);
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getMessageId() { return messageId; }
    }
}
