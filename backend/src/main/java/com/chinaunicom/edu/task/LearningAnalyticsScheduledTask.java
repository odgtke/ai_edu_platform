package com.chinaunicom.edu.task;

import com.chinaunicom.edu.entity.LearningPath;
import com.chinaunicom.edu.mapper.LearningPathMapper;
import com.chinaunicom.edu.service.LearningPathAdjustmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 学习分析定时任务
 * 定期执行学习路径评估和调整
 */
@Component
public class LearningAnalyticsScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(LearningAnalyticsScheduledTask.class);

    private final LearningPathAdjustmentService pathAdjustmentService;
    private final LearningPathMapper learningPathMapper;

    public LearningAnalyticsScheduledTask(LearningPathAdjustmentService pathAdjustmentService,
                                        LearningPathMapper learningPathMapper) {
        this.pathAdjustmentService = pathAdjustmentService;
        this.learningPathMapper = learningPathMapper;
    }

    /**
     * 每日凌晨2点执行：批量评估和调整学习路径
     * cron表达式: 0 0 2 * * ?
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void batchAdjustLearningPaths() {
        logger.info("开始执行批量学习路径调整任务...");
        
        try {
            // 获取所有活跃的学习路径
            List<LearningPath> activePaths = learningPathMapper.findByPathStatus("active");
            
            int adjustedCount = 0;
            int totalCount = activePaths.size();
            
            for (LearningPath path : activePaths) {
                try {
                    // 执行路径调整
                    LearningPath adjustedPath = pathAdjustmentService.adjustPathDifficulty(
                        path.getUserId(), path);
                    
                    if (adjustedPath != null && 
                        !adjustedPath.equals(path)) { // 路径发生了调整
                        adjustedCount++;
                        logger.info("用户{}的学习路径{}已调整，新难度系数: {}", 
                            path.getUserId(), path.getId(), 
                            adjustedPath.getDifficultyFactor());
                    }
                } catch (Exception e) {
                    logger.error("调整用户{}的学习路径{}时发生错误: {}", 
                        path.getUserId(), path.getId(), e.getMessage(), e);
                }
            }
            
            logger.info("批量学习路径调整完成，总共处理{}条路径，调整了{}条路径", 
                totalCount, adjustedCount);
                
        } catch (Exception e) {
            logger.error("批量学习路径调整任务执行失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 每小时执行：更新用户知识掌握度统计
     * cron表达式: 0 0 * * * ?
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void updateKnowledgeStatistics() {
        logger.info("开始更新用户知识掌握度统计数据...");
        
        try {
            // 这里应该调用知识掌握度服务来更新统计信息
            // 例如：重新计算用户的平均掌握度、识别新的薄弱知识点等
            
            logger.info("知识掌握度统计更新完成");
            
        } catch (Exception e) {
            logger.error("知识掌握度统计更新失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 每周日凌晨3点执行：生成学习报告
     * cron表达式: 0 0 3 * * SUN
     */
    @Scheduled(cron = "0 0 3 * * SUN")
    public void generateWeeklyReports() {
        logger.info("开始生成周学习报告...");
        
        try {
            // 生成周报逻辑
            // 1. 统计本周学习时长
            // 2. 分析学习进步情况
            // 3. 识别学习瓶颈
            // 4. 生成个性化建议
            
            logger.info("周学习报告生成完成");
            
        } catch (Exception e) {
            logger.error("周学习报告生成失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 每月1号凌晨4点执行：清理过期数据
     * cron表达式: 0 0 4 1 * ?
     */
    @Scheduled(cron = "0 0 4 1 * ?")
    public void cleanExpiredData() {
        logger.info("开始清理过期的学习分析数据...");
        
        try {
            // 清理逻辑
            // 1. 删除超过一年的历史调整记录
            // 2. 归档旧的掌握度数据
            // 3. 清理临时文件
            
            logger.info("过期数据清理完成");
            
        } catch (Exception e) {
            logger.error("过期数据清理失败: {}", e.getMessage(), e);
        }
    }
}
