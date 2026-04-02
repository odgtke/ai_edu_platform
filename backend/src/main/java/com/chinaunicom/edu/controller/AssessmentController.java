package com.chinaunicom.edu.controller;

import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.component.AnswerSubmitQueueComponent;
import com.chinaunicom.edu.component.AssessmentWarmupComponent;
import com.chinaunicom.edu.component.DistributedLockComponent;
import com.chinaunicom.edu.component.RateLimiterComponent;
import com.chinaunicom.edu.entity.Assessment;
import com.chinaunicom.edu.entity.UserAssessment;
import com.chinaunicom.edu.service.AssessmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 能力评估控制器 - 高并发优化版
 * 包含限流、分布式锁、预热缓存、消息队列等优化
 */
@RestController
@RequestMapping("/api/assessments")
public class AssessmentController {
    
    private static final Logger log = LoggerFactory.getLogger(AssessmentController.class);
    
    @Autowired
    private AssessmentService assessmentService;
    
    @Autowired
    private RateLimiterComponent rateLimiter;
    
    @Autowired
    private DistributedLockComponent distributedLock;
    
    @Autowired
    private AssessmentWarmupComponent warmupComponent;
    
    @Autowired
    private AnswerSubmitQueueComponent submitQueue;
    
    /**
     * 获取可参加的评估列表
     */
    @GetMapping("/available")
    public Result<List<Assessment>> getAvailableAssessments(@RequestParam Long userId) {
        try {
            List<Assessment> assessments = assessmentService.getAvailableAssessments(userId);
            return Result.success(assessments);
        } catch (Exception e) {
            log.error("获取可参加评估列表失败", e);
            return Result.error("获取评估列表失败");
        }
    }
    
    /**
     * 根据学科获取评估列表
     */
    @GetMapping("/subject/{subjectId}")
    public Result<List<Assessment>> getAssessmentsBySubject(@PathVariable Long subjectId) {
        try {
            List<Assessment> assessments = assessmentService.getAssessmentsBySubject(subjectId);
            return Result.success(assessments);
        } catch (Exception e) {
            log.error("根据学科获取评估列表失败", e);
            return Result.error("获取评估列表失败");
        }
    }
    
    /**
     * 根据年级获取评估列表
     */
    @GetMapping("/grade/{gradeLevel}")
    public Result<List<Assessment>> getAssessmentsByGrade(@PathVariable Integer gradeLevel) {
        try {
            List<Assessment> assessments = assessmentService.getAssessmentsByGrade(gradeLevel);
            return Result.success(assessments);
        } catch (Exception e) {
            log.error("根据年级获取评估列表失败", e);
            return Result.error("获取评估列表失败");
        }
    }
    
    /**
     * 获取评估详情（支持预热缓存）
     */
    @GetMapping("/{assessmentId}")
    public Result<Map<String, Object>> getAssessmentDetail(@PathVariable Long assessmentId) {
        try {
            // 优先从预热缓存获取
            if (warmupComponent.isWarmup(assessmentId)) {
                log.debug("Serving assessment {} from warmup cache", assessmentId);
            }
            
            Map<String, Object> detail = assessmentService.getAssessmentDetail(assessmentId);
            return Result.success(detail);
        } catch (Exception e) {
            log.error("获取评估详情失败", e);
            return Result.error("获取评估详情失败");
        }
    }
    
    /**
     * 开始评估 - 高并发优化版
     * 1. 全局限流保护
     * 2. 单用户限流
     * 3. 分布式锁防止重复开始
     * 4. 考试预热检查
     */
    @PostMapping("/{assessmentId}/start")
    public Result<UserAssessment> startAssessment(
            @PathVariable Long assessmentId,
            @RequestParam Long userId) {
        try {
            // 1. 全局限流检查
            if (!rateLimiter.tryGlobalAssessment()) {
                log.warn("Global assessment rate limit exceeded");
                return Result.error("系统繁忙，请稍后重试");
            }
            
            // 2. 考试开始限流
            if (!rateLimiter.tryStartAssessment(assessmentId)) {
                log.warn("Assessment {} start rate limit exceeded", assessmentId);
                return Result.error("该考试当前人数过多，请稍后重试");
            }
            
            // 3. 分布式锁防止重复开始
            String lockValue = distributedLock.tryStartLock(userId);
            if (lockValue == null) {
                log.warn("User {} is already starting an assessment", userId);
                return Result.error("操作过于频繁，请稍后再试");
            }
            
            try {
                // 4. 检查考试是否已预热
                if (!warmupComponent.isWarmup(assessmentId)) {
                    log.info("Assessment {} not warmed up, warming up now", assessmentId);
                    warmupComponent.warmupAssessment(assessmentId);
                }
                
                UserAssessment userAssessment = assessmentService.startAssessment(userId, assessmentId);
                return Result.success(userAssessment);
            } finally {
                distributedLock.releaseStartLock(userId, lockValue);
            }
        } catch (Exception e) {
            log.error("开始评估失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 提交答案 - 高并发优化版
     * 1. 用户提交限流
     * 2. 分布式锁防止重复提交
     * 3. 支持同步提交或队列异步提交
     */
    @PostMapping("/{assessmentId}/submit")
    public Result<?> submitAnswer(
            @PathVariable Long assessmentId,
            @RequestParam Long userId,
            @RequestBody Map<Integer, String> answers,
            @RequestParam(defaultValue = "false") boolean async) {
        try {
            // 1. 用户提交限流
            if (!rateLimiter.trySubmitAnswer(userId, assessmentId)) {
                log.warn("User {} submit rate limit exceeded for assessment {}", userId, assessmentId);
                return Result.error("提交过于频繁，请稍后再试");
            }
            
            // 2. 分布式锁防止重复提交
            String lockValue = distributedLock.trySubmitLock(userId, assessmentId);
            if (lockValue == null) {
                log.warn("User {} is already submitting assessment {}", userId, assessmentId);
                return Result.error("正在处理提交，请勿重复操作");
            }
            
            try {
                if (async) {
                    // 异步提交：进入消息队列
                    boolean enqueued = submitQueue.enqueueSubmit(userId, assessmentId, answers);
                    if (enqueued) {
                        return Result.success("提交已入队，正在异步处理");
                    } else {
                        return Result.error("提交队列已满，请稍后重试");
                    }
                } else {
                    // 同步提交：直接处理
                    UserAssessment result = assessmentService.submitAnswer(userId, assessmentId, answers);
                    return Result.success(result);
                }
            } finally {
                distributedLock.releaseSubmitLock(userId, assessmentId, lockValue);
            }
        } catch (Exception e) {
            log.error("提交答案失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取提交队列状态（管理接口）
     */
    @GetMapping("/queue/stats")
    public Result<Map<String, Long>> getQueueStats() {
        try {
            Map<String, Long> stats = submitQueue.getQueueStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取队列统计失败", e);
            return Result.error("获取队列统计失败");
        }
    }
    
    /**
     * 手动触发考试预热（管理接口）
     */
    @PostMapping("/{assessmentId}/warmup")
    public Result<String> warmupAssessment(@PathVariable Long assessmentId) {
        try {
            boolean success = warmupComponent.manualWarmup(assessmentId);
            if (success) {
                return Result.success("考试预热成功");
            } else {
                return Result.error("考试预热失败");
            }
        } catch (Exception e) {
            log.error("考试预热失败", e);
            return Result.error("考试预热失败");
        }
    }
    
    /**
     * 获取用户评估历史
     */
    @GetMapping("/history/{userId}")
    public Result<List<UserAssessment>> getUserAssessmentHistory(@PathVariable Long userId) {
        try {
            List<UserAssessment> history = assessmentService.getUserAssessmentHistory(userId);
            return Result.success(history);
        } catch (Exception e) {
            log.error("获取评估历史失败", e);
            return Result.error("获取评估历史失败");
        }
    }
    
    /**
     * 获取评估统计信息
     */
    @GetMapping("/statistics/{userId}")
    public Result<Map<String, Object>> getAssessmentStatistics(@PathVariable Long userId) {
        try {
            Map<String, Object> statistics = assessmentService.getAssessmentStatistics(userId);
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取评估统计信息失败", e);
            return Result.error("获取统计信息失败");
        }
    }
    
    /**
     * 生成能力分析报告
     */
    @GetMapping("/report/{userAssessmentId}")
    public Result<Map<String, Object>> generateCapabilityReport(@PathVariable Long userAssessmentId) {
        try {
            Map<String, Object> report = assessmentService.generateCapabilityReport(userAssessmentId);
            return Result.success(report);
        } catch (Exception e) {
            log.error("生成能力分析报告失败", e);
            return Result.error("生成报告失败");
        }
    }
    
    /**
     * 检查用户是否有权限参加评估
     */
    @GetMapping("/{assessmentId}/permission/{userId}")
    public Result<Boolean> checkPermission(
            @PathVariable Long assessmentId,
            @PathVariable Long userId) {
        try {
            boolean canTake = assessmentService.canUserTakeAssessment(userId, assessmentId);
            return Result.success(canTake);
        } catch (Exception e) {
            log.error("检查权限失败", e);
            return Result.error("检查权限失败");
        }
    }
    
    /**
     * 获取用户当前进行中的评估
     */
    @GetMapping("/in-progress/{userId}")
    public Result<List<UserAssessment>> getInProgressAssessments(@PathVariable Long userId) {
        try {
            List<UserAssessment> inProgress = assessmentService.getInProgressAssessments(userId);
            return Result.success(inProgress);
        } catch (Exception e) {
            log.error("获取进行中的评估失败", e);
            return Result.error("获取进行中的评估失败");
        }
    }
}