package com.chinaunicom.edu.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinaunicom.edu.user.entity.Grade;
import com.chinaunicom.edu.user.mapper.GradeMapper;
import com.chinaunicom.edu.user.service.GradeService;
import org.springframework.stereotype.Service;

@Service
public class GradeServiceImpl extends ServiceImpl<GradeMapper, Grade> implements GradeService {
}
