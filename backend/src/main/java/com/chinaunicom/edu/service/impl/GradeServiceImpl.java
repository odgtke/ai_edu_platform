package com.chinaunicom.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinaunicom.edu.entity.Grade;
import com.chinaunicom.edu.mapper.GradeMapper;
import com.chinaunicom.edu.service.GradeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 年级服务实现类
 */
@Service
public class GradeServiceImpl extends ServiceImpl<GradeMapper, Grade> implements GradeService {

    @Override
    public List<Grade> listActiveGrades() {
        QueryWrapper<Grade> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1)
               .orderByAsc("grade_level");
        return list(wrapper);
    }

    @Override
    public Grade getByLevel(Integer gradeLevel) {
        QueryWrapper<Grade> wrapper = new QueryWrapper<>();
        wrapper.eq("grade_level", gradeLevel)
               .eq("status", 1);
        return getOne(wrapper);
    }
}
