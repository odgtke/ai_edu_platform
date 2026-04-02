package com.chinaunicom.edu.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinaunicom.edu.user.entity.Clazz;
import com.chinaunicom.edu.user.mapper.ClazzMapper;
import com.chinaunicom.edu.user.service.ClazzService;
import org.springframework.stereotype.Service;

@Service
public class ClazzServiceImpl extends ServiceImpl<ClazzMapper, Clazz> implements ClazzService {
}
