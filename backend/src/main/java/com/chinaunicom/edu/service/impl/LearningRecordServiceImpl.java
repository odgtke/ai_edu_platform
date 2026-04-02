package com.chinaunicom.edu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinaunicom.edu.entity.LearningRecord;
import com.chinaunicom.edu.mapper.LearningRecordMapper;
import com.chinaunicom.edu.service.LearningRecordService;
import org.springframework.stereotype.Service;

/**
 * 学习记录Service实现类
 */
@Service
public class LearningRecordServiceImpl extends ServiceImpl<LearningRecordMapper, LearningRecord> implements LearningRecordService {
}