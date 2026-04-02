package com.chinaunicom.edu.service.notification.channel;

import com.chinaunicom.edu.entity.Notification;
import com.chinaunicom.edu.mapper.NotificationMapper;
import com.chinaunicom.edu.service.notification.NotificationChannel;
import com.chinaunicom.edu.service.notification.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 应用内通知渠道
 * 观察者模式具体实现：将通知保存到数据库
 */
@Component
public class InAppNotificationChannel implements NotificationChannel {
    
    private static final Logger logger = LoggerFactory.getLogger(InAppNotificationChannel.class);
    
    @Autowired
    private NotificationMapper notificationMapper;
    
    @Override
    public String getChannelName() {
        return "InApp";
    }
    
    @Override
    public ChannelType getChannelType() {
        return ChannelType.IN_APP;
    }
    
    @Override
    public ChannelResult send(NotificationEvent event) {
        try {
            // 创建通知实体
            Notification notification = new Notification();
            // notificationId是自增的，不需要设置
            notification.setSenderId(event.getSenderId());
            notification.setSenderName(event.getSenderName());
            notification.setReceiverId(event.getReceiverId());
            notification.setTitle(event.getTitle());
            notification.setContent(event.getContent());
            notification.setType(event.getType().name().toLowerCase());
            notification.setPriority(event.getPriority());
            notification.setIsRead(0);
            notification.setSendTime(LocalDateTime.now());
            // createTime由@TableField自动填充
            
            // 保存到数据库
            notificationMapper.insert(notification);
            
            logger.debug("In-app notification saved: {} for user {}", 
                    notification.getNotificationId(), event.getReceiverId());
            
            return ChannelResult.success(String.valueOf(notification.getNotificationId()));
            
        } catch (Exception e) {
            logger.error("Failed to save in-app notification", e);
            return ChannelResult.failure(e.getMessage());
        }
    }
    
    @Override
    public boolean supports(NotificationEvent event) {
        // 应用内通知支持所有类型
        return true;
    }
    
    @Override
    public boolean isAvailable() {
        return true;
    }
}
