package com.chinaunicom.edu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinaunicom.edu.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 消息通知 Mapper 接口
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

    /**
     * 查询用户的未读消息数量
     */
    @Select("SELECT COUNT(*) FROM edu_notification " +
            "WHERE receiver_id = #{userId} AND is_read = 0")
    Integer countUnreadByUserId(@Param("userId") Long userId);

    /**
     * 查询用户的消息列表
     */
    @Select("SELECT * FROM edu_notification " +
            "WHERE receiver_id = #{userId} OR receiver_type = 'all' " +
            "ORDER BY send_time DESC")
    List<Notification> selectByUserId(@Param("userId") Long userId);

    /**
     * 标记消息为已读
     */
    @Update("UPDATE edu_notification SET is_read = 1, read_time = NOW() " +
            "WHERE notification_id = #{notificationId}")
    int markAsRead(@Param("notificationId") Long notificationId);

    /**
     * 标记用户所有消息为已读
     */
    @Update("UPDATE edu_notification SET is_read = 1, read_time = NOW() " +
            "WHERE receiver_id = #{userId} AND is_read = 0")
    int markAllAsRead(@Param("userId") Long userId);
}
