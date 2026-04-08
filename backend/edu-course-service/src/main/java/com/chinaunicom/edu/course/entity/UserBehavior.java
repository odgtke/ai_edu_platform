package com.chinaunicom.edu.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
@TableName("user_behaviors")
public class UserBehavior {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long courseId;
    
    /**
     * 行为类型：view(浏览), browse(搜索查看), study(学习), complete(完成), 
     * favorite(收藏), share(分享), rate(评分), enroll(报名)
     */
    private String behaviorType;
    
    /**
     * 行为权重值
     */
    private Double behaviorValue;
    
    /**
     * IP地址
     */
    private String ipAddress;
    
    /**
     * 用户代理
     */
    private String userAgent;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}
