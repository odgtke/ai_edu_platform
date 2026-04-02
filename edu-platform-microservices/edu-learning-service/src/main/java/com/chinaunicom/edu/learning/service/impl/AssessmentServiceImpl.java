package com.chinaunicom.edu.learning.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinaunicom.edu.learning.entity.Assessment;
import com.chinaunicom.edu.learning.mapper.AssessmentMapper;
import com.chinaunicom.edu.learning.service.AssessmentService;
import org.springframework.stereotype.Service;

@Service
public class AssessmentServiceImpl extends ServiceImpl<AssessmentMapper, Assessment> implements AssessmentService {
}
