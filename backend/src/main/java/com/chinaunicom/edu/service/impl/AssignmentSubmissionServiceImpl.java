package com.chinaunicom.edu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinaunicom.edu.entity.AssignmentSubmission;
import com.chinaunicom.edu.mapper.AssignmentSubmissionMapper;
import com.chinaunicom.edu.service.AssignmentSubmissionService;
import org.springframework.stereotype.Service;

/**
 * 作业提交记录服务实现类
 */
@Service
public class AssignmentSubmissionServiceImpl extends ServiceImpl<AssignmentSubmissionMapper, AssignmentSubmission> 
        implements AssignmentSubmissionService {
    
    private final AssignmentSubmissionMapper assignmentSubmissionMapper;
    
    public AssignmentSubmissionServiceImpl(AssignmentSubmissionMapper assignmentSubmissionMapper) {
        this.assignmentSubmissionMapper = assignmentSubmissionMapper;
    }
    
    @Override
    public double getSubmissionRate(Long studentId) {
        int submitted = assignmentSubmissionMapper.countSubmittedByStudent(studentId);
        int total = assignmentSubmissionMapper.countTotalAssignmentsByStudent(studentId);
        
        if (total == 0) {
            return 100.0; // 没有作业时返回100%
        }
        
        return Math.round((double) submitted / total * 100 * 100) / 100.0;
    }
}
