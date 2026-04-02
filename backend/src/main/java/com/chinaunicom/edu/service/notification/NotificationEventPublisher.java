package com.chinaunicom.edu.service.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;

/**
 * 通知事件发布器
 * 观察者模式 + 发布订阅模式：管理通知渠道的注册和事件分发
 */
@Component
public class NotificationEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationEventPublisher.class);
    
    // 事件类型 -> 渠道列表映射
    private final Map<NotificationEvent.NotificationType, Set<NotificationChannel>> eventTypeToChannels = 
            new ConcurrentHashMap<>();
    
    // 所有注册的渠道
    private final Set<NotificationChannel> allChannels = ConcurrentHashMap.newKeySet();
    
    @Autowired
    private List<NotificationChannel> channels;
    
    // 异步执行器
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    
    /**
     * 初始化时注册所有渠道
     */
    @PostConstruct
    public void initialize() {
        for (NotificationChannel channel : channels) {
            registerChannel(channel);
        }
        logger.info("Initialized notification publisher with {} channels", channels.size());
    }
    
    /**
     * 注册通知渠道
     */
    public void registerChannel(NotificationChannel channel) {
        allChannels.add(channel);
        logger.info("Registered notification channel: {} ({})", 
                channel.getChannelName(), channel.getChannelType());
    }
    
    /**
     * 订阅特定事件类型
     */
    public void subscribe(NotificationEvent.NotificationType eventType, NotificationChannel channel) {
        eventTypeToChannels.computeIfAbsent(eventType, k -> ConcurrentHashMap.newKeySet()).add(channel);
        logger.debug("Channel {} subscribed to event type {}", channel.getChannelName(), eventType);
    }
    
    /**
     * 取消订阅
     */
    public void unsubscribe(NotificationEvent.NotificationType eventType, NotificationChannel channel) {
        Set<NotificationChannel> channels = eventTypeToChannels.get(eventType);
        if (channels != null) {
            channels.remove(channel);
        }
    }
    
    /**
     * 发布通知事件（同步）
     */
    public Map<String, NotificationChannel.ChannelResult> publish(NotificationEvent event) {
        Map<String, NotificationChannel.ChannelResult> results = new HashMap<>();
        
        // 获取支持该事件类型的渠道
        Set<NotificationChannel> targetChannels = eventTypeToChannels.getOrDefault(
                event.getType(), allChannels);
        
        for (NotificationChannel channel : targetChannels) {
            if (channel.isAvailable() && channel.supports(event)) {
                try {
                    NotificationChannel.ChannelResult result = channel.send(event);
                    results.put(channel.getChannelName(), result);
                    
                    if (result.isSuccess()) {
                        logger.debug("Notification sent via {}: {}", 
                                channel.getChannelName(), event.getEventId());
                    } else {
                        logger.warn("Failed to send notification via {}: {}", 
                                channel.getChannelName(), result.getMessage());
                    }
                } catch (Exception e) {
                    logger.error("Error sending notification via {}", channel.getChannelName(), e);
                    results.put(channel.getChannelName(), 
                            NotificationChannel.ChannelResult.failure(e.getMessage()));
                }
            }
        }
        
        return results;
    }
    
    /**
     * 发布通知事件（异步）
     */
    @Async
    public CompletableFuture<Map<String, NotificationChannel.ChannelResult>> publishAsync(NotificationEvent event) {
        return CompletableFuture.supplyAsync(() -> publish(event), executorService);
    }
    
    /**
     * 广播通知到所有渠道
     */
    public Map<String, NotificationChannel.ChannelResult> broadcast(NotificationEvent event) {
        Map<String, NotificationChannel.ChannelResult> results = new HashMap<>();
        
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (NotificationChannel channel : allChannels) {
            if (channel.isAvailable()) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        NotificationChannel.ChannelResult result = channel.send(event);
                        results.put(channel.getChannelName(), result);
                    } catch (Exception e) {
                        logger.error("Error broadcasting via {}", channel.getChannelName(), e);
                        results.put(channel.getChannelName(), 
                                NotificationChannel.ChannelResult.failure(e.getMessage()));
                    }
                }, executorService);
                futures.add(future);
            }
        }
        
        // 等待所有发送完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        return results;
    }
    
    /**
     * 获取已注册的渠道列表
     */
    public Set<NotificationChannel> getRegisteredChannels() {
        return new HashSet<>(allChannels);
    }
    
    /**
     * 获取指定事件类型的订阅渠道
     */
    public Set<NotificationChannel> getSubscribedChannels(NotificationEvent.NotificationType eventType) {
        return new HashSet<>(eventTypeToChannels.getOrDefault(eventType, Collections.emptySet()));
    }
}
