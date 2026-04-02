package com.chinaunicom.edu.component;

import com.chinaunicom.edu.entity.UserAssessment;
import com.chinaunicom.edu.service.AssessmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 答案提交队列组件
 * 使用Redis实现消息队列，削峰填谷处理提交请求
 */
@Component
public class AnswerSubmitQueueComponent {

    private static final Logger logger = LoggerFactory.getLogger(AnswerSubmitQueueComponent.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private ObjectMapper objectMapper;

    // 队列键名
    private static final String QUEUE_KEY = "queue:answer:submit";
    private static final String PROCESSING_KEY = "queue:answer:processing";
    private static final String DLQ_KEY = "queue:answer:dlq"; // 死信队列

    // 批量处理大小
    private static final int BATCH_SIZE = 10;
    // 队列最大长度
    private static final long MAX_QUEUE_SIZE = 10000;

    @PostConstruct
    public void init() {
        logger.info("Answer submit queue component initialized");
    }

    /**
     * 提交答案到队列
     * @param userId 用户ID
     * @param assessmentId 评估ID
     * @param answers 答案
     * @return true: 入队成功, false: 队列已满
     */
    public boolean enqueueSubmit(Long userId, Long assessmentId, Map<Integer, String> answers) {
        try {
            // 检查队列长度
            Long queueSize = redisTemplate.opsForList().size(QUEUE_KEY);
            if (queueSize != null && queueSize >= MAX_QUEUE_SIZE) {
                logger.warn("Submit queue is full, size: {}", queueSize);
                return false;
            }

            // 创建提交任务
            SubmitTask task = new SubmitTask();
            task.setUserId(userId);
            task.setAssessmentId(assessmentId);
            task.setAnswers(answers);
            task.setSubmitTime(LocalDateTime.now());
            task.setRetryCount(0);

            // 序列化并入队
            String taskJson = objectMapper.writeValueAsString(task);
            redisTemplate.opsForList().rightPush(QUEUE_KEY, taskJson);
            
            logger.debug("Enqueued submit task for user {}, assessment {}", userId, assessmentId);
            return true;
        } catch (Exception e) {
            logger.error("Error enqueuing submit task: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 批量处理队列中的提交任务
     */
    @Scheduled(fixedRate = 1000) // 每秒处理一次
    @Async("dataProcessingExecutor")
    public void processQueue() {
        try {
            for (int i = 0; i < BATCH_SIZE; i++) {
                // 从队列取出任务
                Object taskObj = redisTemplate.opsForList().leftPop(QUEUE_KEY, 1, TimeUnit.SECONDS);
                if (taskObj == null) {
                    break; // 队列为空
                }

                // 反序列化
                SubmitTask task = objectMapper.readValue(taskObj.toString(), SubmitTask.class);
                
                // 处理任务
                boolean success = processTask(task);
                
                if (!success && task.getRetryCount() < 3) {
                    // 重试
                    task.setRetryCount(task.getRetryCount() + 1);
                    requeueTask(task);
                } else if (!success) {
                    // 超过重试次数，进入死信队列
                    moveToDLQ(task);
                }
            }
        } catch (Exception e) {
            logger.error("Error processing submit queue: {}", e.getMessage());
        }
    }

    /**
     * 处理单个提交任务
     */
    private boolean processTask(SubmitTask task) {
        try {
            logger.debug("Processing submit task for user {}, assessment {}", 
                task.getUserId(), task.getAssessmentId());
            
            UserAssessment result = assessmentService.submitAnswer(
                task.getUserId(), 
                task.getAssessmentId(), 
                task.getAnswers()
            );
            
            if (result != null) {
                logger.info("Successfully processed submit for user {}, assessment {}, score: {}",
                    task.getUserId(), task.getAssessmentId(), result.getTotalScore());
                return true;
            }
            
            return false;
        } catch (Exception e) {
            logger.error("Error processing submit task: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 重新入队（延迟重试）
     */
    private void requeueTask(SubmitTask task) throws Exception {
        // 延迟重试：根据重试次数增加延迟
        int delaySeconds = task.getRetryCount() * 2;
        
        // 使用延迟队列（通过Redis的过期时间实现简单延迟）
        String delayKey = "queue:answer:delay:" + System.currentTimeMillis();
        redisTemplate.opsForValue().set(delayKey, objectMapper.writeValueAsString(task), delaySeconds, TimeUnit.SECONDS);
        
        logger.debug("Requeued task with {}s delay, retry count: {}", delaySeconds, task.getRetryCount());
    }

    /**
     * 移入死信队列
     */
    private void moveToDLQ(SubmitTask task) {
        try {
            redisTemplate.opsForList().rightPush(DLQ_KEY, objectMapper.writeValueAsString(task));
            logger.error("Moved task to DLQ for user {}, assessment {}", 
                task.getUserId(), task.getAssessmentId());
        } catch (Exception e) {
            logger.error("Error moving task to DLQ: {}", e.getMessage());
        }
    }

    /**
     * 获取队列统计信息
     */
    public Map<String, Long> getQueueStats() {
        Map<String, Long> stats = new java.util.HashMap<>();
        try {
            Long queueSize = redisTemplate.opsForList().size(QUEUE_KEY);
            Long dlqSize = redisTemplate.opsForList().size(DLQ_KEY);
            
            stats.put("queueSize", queueSize != null ? queueSize : 0);
            stats.put("dlqSize", dlqSize != null ? dlqSize : 0);
            stats.put("maxSize", MAX_QUEUE_SIZE);
        } catch (Exception e) {
            logger.error("Error getting queue stats: {}", e.getMessage());
        }
        return stats;
    }

    /**
     * 清空队列（用于紧急处理）
     */
    public void clearQueue() {
        try {
            redisTemplate.delete(QUEUE_KEY);
            logger.warn("Submit queue cleared");
        } catch (Exception e) {
            logger.error("Error clearing queue: {}", e.getMessage());
        }
    }

    /**
     * 处理死信队列中的任务（手动重试）
     */
    public int retryDLQ() {
        int count = 0;
        try {
            while (true) {
                Object taskObj = redisTemplate.opsForList().leftPop(DLQ_KEY);
                if (taskObj == null) break;
                
                SubmitTask task = objectMapper.readValue(taskObj.toString(), SubmitTask.class);
                task.setRetryCount(0); // 重置重试次数
                
                // 重新入队
                redisTemplate.opsForList().rightPush(QUEUE_KEY, objectMapper.writeValueAsString(task));
                count++;
            }
            logger.info("Retried {} tasks from DLQ", count);
        } catch (Exception e) {
            logger.error("Error retrying DLQ: {}", e.getMessage());
        }
        return count;
    }

    /**
     * 提交任务内部类
     */
    public static class SubmitTask {
        private Long userId;
        private Long assessmentId;
        private Map<Integer, String> answers;
        private LocalDateTime submitTime;
        private int retryCount;

        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public Long getAssessmentId() { return assessmentId; }
        public void setAssessmentId(Long assessmentId) { this.assessmentId = assessmentId; }
        
        public Map<Integer, String> getAnswers() { return answers; }
        public void setAnswers(Map<Integer, String> answers) { this.answers = answers; }
        
        public LocalDateTime getSubmitTime() { return submitTime; }
        public void setSubmitTime(LocalDateTime submitTime) { this.submitTime = submitTime; }
        
        public int getRetryCount() { return retryCount; }
        public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
    }
}
