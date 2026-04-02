package com.chinaunicom.edu.service.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 通知服务门面
 * 门面模式：为通知系统提供统一的入口
 * 组合了观察者模式、建造者模式等多种设计模式
 */
@Service
public class NotificationServiceFacade {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceFacade.class);
    
    @Autowired
    private NotificationEventPublisher eventPublisher;
    
    /**
     * 发送系统通知
     */
    public Map<String, NotificationChannel.ChannelResult> sendSystemNotification(
            Long senderId, String senderName, Long receiverId, 
            String title, String content, Integer priority) {
        
        NotificationEvent event = NotificationEvent.builder()
                .type(NotificationEvent.NotificationType.SYSTEM)
                .senderId(senderId)
                .senderName(senderName)
                .receiverId(receiverId)
                .title(title)
                .content(content)
                .priority(priority)
                .build();
        
        return eventPublisher.publish(event);
    }
    
    /**
     * 发送课程通知
     */
    public Map<String, NotificationChannel.ChannelResult> sendCourseNotification(
            Long senderId, String senderName, Long receiverId,
            String title, String content, Long courseId, Integer priority) {
        
        NotificationEvent event = NotificationEvent.builder()
                .type(NotificationEvent.NotificationType.COURSE)
                .senderId(senderId)
                .senderName(senderName)
                .receiverId(receiverId)
                .title(title)
                .content(content)
                .priority(priority)
                .addMetadata("courseId", courseId)
                .build();
        
        return eventPublisher.publish(event);
    }
    
    /**
     * 发送作业通知
     */
    public Map<String, NotificationChannel.ChannelResult> sendAssignmentNotification(
            Long senderId, String senderName, Long receiverId,
            String title, String content, Long assignmentId, Integer priority) {
        
        NotificationEvent event = NotificationEvent.builder()
                .type(NotificationEvent.NotificationType.ASSIGNMENT)
                .senderId(senderId)
                .senderName(senderName)
                .receiverId(receiverId)
                .title(title)
                .content(content)
                .priority(priority)
                .addMetadata("assignmentId", assignmentId)
                .build();
        
        return eventPublisher.publish(event);
    }
    
    /**
     * 发送考试通知
     */
    public Map<String, NotificationChannel.ChannelResult> sendExamNotification(
            Long senderId, String senderName, Long receiverId,
            String title, String content, Long examId, Integer priority) {
        
        NotificationEvent event = NotificationEvent.builder()
                .type(NotificationEvent.NotificationType.EXAM)
                .senderId(senderId)
                .senderName(senderName)
                .receiverId(receiverId)
                .title(title)
                .content(content)
                .priority(priority)
                .addMetadata("examId", examId)
                .build();
        
        return eventPublisher.publish(event);
    }
    
    /**
     * 广播通知（发送给所有渠道）
     */
    public Map<String, NotificationChannel.ChannelResult> broadcastNotification(
            NotificationEvent.NotificationType type,
            Long senderId, String senderName,
            String title, String content, Integer priority) {
        
        NotificationEvent event = NotificationEvent.builder()
                .type(type)
                .senderId(senderId)
                .senderName(senderName)
                .title(title)
                .content(content)
                .priority(priority)
                .build();
        
        return eventPublisher.broadcast(event);
    }
    
    /**
     * 发送自定义事件
     */
    public Map<String, NotificationChannel.ChannelResult> sendCustomNotification(NotificationEvent event) {
        return eventPublisher.publish(event);
    }
}
