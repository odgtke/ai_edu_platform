package com.chinaunicom.edu.service.notification.channel;

import com.chinaunicom.edu.service.notification.NotificationChannel;
import com.chinaunicom.edu.service.notification.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket通知渠道
 * 观察者模式具体实现：通过WebSocket实时推送通知
 * 
 * 注意：此实现为基础版本，实际使用时需要集成Spring WebSocket
 */
@Component
public class WebSocketNotificationChannel implements NotificationChannel {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketNotificationChannel.class);
    
    // 在线用户ID集合
    private final Set<Long> onlineUsers = ConcurrentHashMap.newKeySet();
    
    @Override
    public String getChannelName() {
        return "WebSocket";
    }
    
    @Override
    public ChannelType getChannelType() {
        return ChannelType.WEBSOCKET;
    }
    
    @Override
    public ChannelResult send(NotificationEvent event) {
        if (!onlineUsers.contains(event.getReceiverId())) {
            logger.debug("User {} is not online, WebSocket notification skipped", event.getReceiverId());
            return ChannelResult.failure("用户不在线");
        }
        
        // 构建消息JSON
        String messageJson = buildMessageJson(event);
        
        // TODO: 实际项目中通过WebSocket发送消息
        // 这里仅记录日志表示消息已准备发送
        logger.info("WebSocket notification prepared for user {}: {}", 
                event.getReceiverId(), messageJson);
        
        return ChannelResult.success(event.getEventId());
    }
    
    @Override
    public boolean supports(NotificationEvent event) {
        // WebSocket支持高优先级的实时通知
        return event.getPriority() != null && event.getPriority() >= 2;
    }
    
    @Override
    public boolean isAvailable() {
        return !onlineUsers.isEmpty();
    }
    
    /**
     * 用户上线
     */
    public void userOnline(Long userId) {
        onlineUsers.add(userId);
        logger.debug("User {} is now online", userId);
    }
    
    /**
     * 用户下线
     */
    public void userOffline(Long userId) {
        onlineUsers.remove(userId);
        logger.debug("User {} is now offline", userId);
    }
    
    /**
     * 检查用户是否在线
     */
    public boolean isUserOnline(Long userId) {
        return onlineUsers.contains(userId);
    }
    
    /**
     * 获取在线用户数量
     */
    public int getOnlineUserCount() {
        return onlineUsers.size();
    }
    
    /**
     * 构建消息JSON
     */
    private String buildMessageJson(NotificationEvent event) {
        return String.format(
            "{\"eventId\":\"%s\",\"type\":\"%s\",\"title\":\"%s\",\"content\":\"%s\",\"timestamp\":\"%s\"}",
            event.getEventId(),
            event.getType(),
            escapeJson(event.getTitle()),
            escapeJson(event.getContent()),
            event.getTimestamp()
        );
    }
    
    /**
     * 转义JSON字符串
     */
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
