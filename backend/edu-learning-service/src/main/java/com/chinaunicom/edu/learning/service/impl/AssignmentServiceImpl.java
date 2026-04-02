package com.chinaunicom.edu.learning.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinaunicom.edu.learning.entity.Assignment;
import com.chinaunicom.edu.learning.mapper.AssignmentMapper;
import com.chinaunicom.edu.learning.service.AssignmentService;
import org.springframework.stereotype.Service;

@Service
public class AssignmentServiceImpl extends ServiceImpl<AssignmentMapper, Assignment> implements AssignmentService {
}
