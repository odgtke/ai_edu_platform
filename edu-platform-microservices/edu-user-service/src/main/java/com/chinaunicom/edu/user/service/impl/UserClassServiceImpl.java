package com.chinaunicom.edu.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinaunicom.edu.user.entity.UserClass;
import com.chinaunicom.edu.user.mapper.UserClassMapper;
import com.chinaunicom.edu.user.service.UserClassService;
import org.springframework.stereotype.Service;

@Service
public class UserClassServiceImpl extends ServiceImpl<UserClassMapper, UserClass> implements UserClassService {
}
