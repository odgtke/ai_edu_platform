package com.chinaunicom.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinaunicom.edu.entity.Clazz;
import com.chinaunicom.edu.entity.UserClass;
import com.chinaunicom.edu.mapper.ClazzMapper;
import com.chinaunicom.edu.mapper.UserClassMapper;
import com.chinaunicom.edu.service.ClazzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 班级服务实现类
 */
@Service
public class ClazzServiceImpl extends ServiceImpl<ClazzMapper, Clazz> implements ClazzService {

    private final ClazzMapper clazzMapper;
    private final UserClassMapper userClassMapper;

    @Autowired
    public ClazzServiceImpl(ClazzMapper clazzMapper, UserClassMapper userClassMapper) {
        this.clazzMapper = clazzMapper;
        this.userClassMapper = userClassMapper;
    }

    @Override
    public List<Clazz> listByGradeId(Long gradeId) {
        return clazzMapper.selectByGradeId(gradeId);
    }

    @Override
    public List<Clazz> listByTeacherId(Long teacherId) {
        return clazzMapper.selectByTeacherId(teacherId);
    }

    @Override
    public Clazz getDetailById(Long classId) {
        return clazzMapper.selectDetailById(classId);
    }

    @Override
    @Transactional
    public void updateStudentCount(Long classId) {
        // 查询班级下的学生数量
        List<UserClass> userClasses = userClassMapper.selectByClassId(classId);
        long studentCount = userClasses.stream()
                .filter(uc -> "student".equals(uc.getUserType()))
                .count();
        
        // 更新班级学生人数
        Clazz clazz = new Clazz();
        clazz.setClassId(classId);
        clazz.setStudentCount((int) studentCount);
        updateById(clazz);
    }
}
