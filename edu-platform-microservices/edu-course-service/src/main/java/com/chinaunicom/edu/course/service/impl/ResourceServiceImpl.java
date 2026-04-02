package com.chinaunicom.edu.course.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinaunicom.edu.course.entity.Resource;
import com.chinaunicom.edu.course.mapper.ResourceMapper;
import com.chinaunicom.edu.course.service.ResourceService;
import org.springframework.stereotype.Service;

@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {
}
