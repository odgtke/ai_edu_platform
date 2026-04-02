package com.chinaunicom.edu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinaunicom.edu.entity.Notification;

import java.util.List;

/**
 * 消息通知服务接口
 */
public interface NotificationService extends IService<Notification> {

    /**
     * 发送消息给个人
     */
    boolean sendToUser(Long senderId, String senderName, Long receiverId, 
                       String title, String content, String type, Integer priority);

    /**
     * 发送消息给班级
     */
    boolean sendToClass(Long senderId, String senderName, Long classId, 
                        String title, String content, String type, Integer priority);

    /**
     * 发送系统消息给所有人
     */
    boolean sendSystemNotification(String title, String content);

    /**
     * 获取用户的未读消息数量
     */
    Integer countUnread(Long userId);

    /**
     * 获取用户的消息列表
     */
    List<Notification> listByUserId(Long userId);

    /**
     * 标记消息为已读
     */
    boolean markAsRead(Long notificationId);

    /**
     * 标记用户所有消息为已读
     */
    boolean markAllAsRead(Long userId);
}
