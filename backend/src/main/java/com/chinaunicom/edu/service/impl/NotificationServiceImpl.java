package com.chinaunicom.edu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinaunicom.edu.entity.Notification;
import com.chinaunicom.edu.entity.UserClass;
import com.chinaunicom.edu.mapper.NotificationMapper;
import com.chinaunicom.edu.mapper.UserClassMapper;
import com.chinaunicom.edu.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息通知服务实现类
 */
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final UserClassMapper userClassMapper;

    @Autowired
    public NotificationServiceImpl(NotificationMapper notificationMapper, UserClassMapper userClassMapper) {
        this.notificationMapper = notificationMapper;
        this.userClassMapper = userClassMapper;
    }

    @Override
    @Transactional
    public boolean sendToUser(Long senderId, String senderName, Long receiverId, 
                               String title, String content, String type, Integer priority) {
        Notification notification = new Notification();
        notification.setSenderId(senderId);
        notification.setSenderName(senderName);
        notification.setReceiverId(receiverId);
        notification.setReceiverType("user");
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setPriority(priority);
        notification.setIsRead(0);
        notification.setSendTime(LocalDateTime.now());
        
        return save(notification);
    }

    @Override
    @Transactional
    public boolean sendToClass(Long senderId, String senderName, Long classId, 
                                String title, String content, String type, Integer priority) {
        // 获取班级下的所有学生
        List<UserClass> userClasses = userClassMapper.selectByClassId(classId);
        
        for (UserClass userClass : userClasses) {
            if ("student".equals(userClass.getUserType())) {
                Notification notification = new Notification();
                notification.setSenderId(senderId);
                notification.setSenderName(senderName);
                notification.setReceiverId(userClass.getUserId());
                notification.setReceiverType("user");
                notification.setTitle(title);
                notification.setContent(content);
                notification.setType(type);
                notification.setPriority(priority);
                notification.setIsRead(0);
                notification.setSendTime(LocalDateTime.now());
                
                save(notification);
            }
        }
        
        return true;
    }

    @Override
    @Transactional
    public boolean sendSystemNotification(String title, String content) {
        Notification notification = new Notification();
        notification.setSenderId(1L); // 系统管理员ID
        notification.setSenderName("系统管理员");
        notification.setReceiverId(0L); // 0 表示所有用户
        notification.setReceiverType("all");
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType("system");
        notification.setPriority(1);
        notification.setIsRead(0);
        notification.setSendTime(LocalDateTime.now());
        
        return save(notification);
    }

    @Override
    public Integer countUnread(Long userId) {
        return notificationMapper.countUnreadByUserId(userId);
    }

    @Override
    public List<Notification> listByUserId(Long userId) {
        List<Notification> notifications = notificationMapper.selectByUserId(userId);
        
        // 设置标签
        for (Notification notification : notifications) {
            notification.setTypeLabel(getTypeLabel(notification.getType()));
            notification.setPriorityLabel(getPriorityLabel(notification.getPriority()));
        }
        
        return notifications;
    }

    @Override
    @Transactional
    public boolean markAsRead(Long notificationId) {
        return notificationMapper.markAsRead(notificationId) > 0;
    }

    @Override
    @Transactional
    public boolean markAllAsRead(Long userId) {
        return notificationMapper.markAllAsRead(userId) > 0;
    }

    private String getTypeLabel(String type) {
        switch (type) {
            case "system": return "系统";
            case "course": return "课程";
            case "assignment": return "作业";
            case "exam": return "考试";
            default: return "其他";
        }
    }

    private String getPriorityLabel(Integer priority) {
        switch (priority) {
            case 1: return "普通";
            case 2: return "重要";
            case 3: return "紧急";
            default: return "普通";
        }
    }
}
