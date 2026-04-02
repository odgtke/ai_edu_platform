package com.chinaunicom.edu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinaunicom.edu.entity.LearningRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学习记录Mapper接口
 */
@Mapper
public interface LearningRecordMapper extends BaseMapper<LearningRecord> {
}