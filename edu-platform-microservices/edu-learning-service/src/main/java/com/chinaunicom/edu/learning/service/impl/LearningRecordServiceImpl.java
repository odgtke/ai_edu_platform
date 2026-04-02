package com.chinaunicom.edu.learning.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinaunicom.edu.learning.entity.LearningRecord;
import com.chinaunicom.edu.learning.mapper.LearningRecordMapper;
import com.chinaunicom.edu.learning.service.LearningRecordService;
import org.springframework.stereotype.Service;

@Service
public class LearningRecordServiceImpl extends ServiceImpl<LearningRecordMapper, LearningRecord> implements LearningRecordService {
}
