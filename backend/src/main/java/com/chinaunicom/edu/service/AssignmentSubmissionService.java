package com.chinaunicom.edu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinaunicom.edu.entity.AssignmentSubmission;

/**
 * 作业提交记录服务接口
 */
public interface AssignmentSubmissionService extends IService<AssignmentSubmission> {
    
    /**
     * 获取学生的作业提交率
     * @param studentId 学生ID
     * @return 提交率 (0-100)
     */
    double getSubmissionRate(Long studentId);
}
