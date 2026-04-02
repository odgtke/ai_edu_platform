package com.chinaunicom.edu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinaunicom.edu.entity.Assignment;
import com.chinaunicom.edu.mapper.AssignmentMapper;
import com.chinaunicom.edu.service.AssignmentService;
import org.springframework.stereotype.Service;

/**
 * 作业服务实现类
 */
@Service
public class AssignmentServiceImpl extends ServiceImpl<AssignmentMapper, Assignment> implements AssignmentService {
}
