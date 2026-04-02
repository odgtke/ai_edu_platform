package com.chinaunicom.edu.controller;

import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.entity.Notification;
import com.chinaunicom.edu.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息通知控制器
 */
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * 获取用户的未读消息数量
     */
    @GetMapping("/unread-count/{userId}")
    public Result<Integer> countUnread(@PathVariable Long userId) {
        Integer count = notificationService.countUnread(userId);
        return Result.success(count);
    }

    /**
     * 获取用户的消息列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<Notification>> listByUserId(@PathVariable Long userId) {
        List<Notification> list = notificationService.listByUserId(userId);
        return Result.success(list);
    }

    /**
     * 发送消息给个人
     */
    @PostMapping("/send-to-user")
    public Result<Boolean> sendToUser(
            @RequestParam Long senderId,
            @RequestParam String senderName,
            @RequestParam Long receiverId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(defaultValue = "system") String type,
            @RequestParam(defaultValue = "1") Integer priority) {
        
        boolean success = notificationService.sendToUser(senderId, senderName, receiverId, 
                title, content, type, priority);
        return success ? Result.success(true) : Result.error("发送失败");
    }

    /**
     * 发送消息给班级
     */
    @PostMapping("/send-to-class")
    public Result<Boolean> sendToClass(
            @RequestParam Long senderId,
            @RequestParam String senderName,
            @RequestParam Long classId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(defaultValue = "system") String type,
            @RequestParam(defaultValue = "1") Integer priority) {
        
        boolean success = notificationService.sendToClass(senderId, senderName, classId, 
                title, content, type, priority);
        return success ? Result.success(true) : Result.error("发送失败");
    }

    /**
     * 发送系统消息
     */
    @PostMapping("/send-system")
    public Result<Boolean> sendSystemNotification(
            @RequestParam String title,
            @RequestParam String content) {
        
        boolean success = notificationService.sendSystemNotification(title, content);
        return success ? Result.success(true) : Result.error("发送失败");
    }

    /**
     * 标记消息为已读
     */
    @PostMapping("/mark-read/{notificationId}")
    public Result<Boolean> markAsRead(@PathVariable Long notificationId) {
        boolean success = notificationService.markAsRead(notificationId);
        return success ? Result.success(true) : Result.error("标记失败");
    }

    /**
     * 标记用户所有消息为已读
     */
    @PostMapping("/mark-all-read/{userId}")
    public Result<Boolean> markAllAsRead(@PathVariable Long userId) {
        boolean success = notificationService.markAllAsRead(userId);
        return success ? Result.success(true) : Result.error("标记失败");
    }
}
