package com.chinaunicom.edu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.entity.Clazz;
import com.chinaunicom.edu.service.ClazzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 班级管理控制器
 */
@RestController
@RequestMapping("/classes")
public class ClazzController {

    private final ClazzService clazzService;

    @Autowired
    public ClazzController(ClazzService clazzService) {
        this.clazzService = clazzService;
    }

    /**
     * 分页查询班级列表
     */
    @GetMapping("/page")
    public Result<Page<Clazz>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long gradeId,
            @RequestParam(required = false) String keyword) {
        
        Page<Clazz> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Clazz> wrapper = new QueryWrapper<>();
        
        if (gradeId != null) {
            wrapper.eq("grade_id", gradeId);
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("class_name", keyword);
        }
        
        wrapper.orderByDesc("create_time");
        clazzService.page(page, wrapper);
        
        // 填充年级名称
        for (Clazz clazz : page.getRecords()) {
            Clazz detail = clazzService.getDetailById(clazz.getClassId());
            if (detail != null) {
                clazz.setGradeName(detail.getGradeName());
            }
        }
        
        return Result.success(page);
    }

    /**
     * 根据年级ID获取班级列表
     */
    @GetMapping("/by-grade/{gradeId}")
    public Result<List<Clazz>> listByGradeId(@PathVariable Long gradeId) {
        List<Clazz> list = clazzService.listByGradeId(gradeId);
        return Result.success(list);
    }

    /**
     * 根据教师ID获取班级列表
     */
    @GetMapping("/by-teacher/{teacherId}")
    public Result<List<Clazz>> listByTeacherId(@PathVariable Long teacherId) {
        List<Clazz> list = clazzService.listByTeacherId(teacherId);
        return Result.success(list);
    }

    /**
     * 根据ID获取班级详情
     */
    @GetMapping("/{id}")
    public Result<Clazz> getById(@PathVariable Long id) {
        Clazz clazz = clazzService.getDetailById(id);
        if (clazz == null) {
            return Result.error("班级不存在");
        }
        return Result.success(clazz);
    }

    /**
     * 新增班级
     */
    @PostMapping
    public Result<Boolean> save(@RequestBody Clazz clazz) {
        clazz.setStudentCount(0);
        clazz.setStatus(1);
        boolean success = clazzService.save(clazz);
        return success ? Result.success(true) : Result.error("添加失败");
    }

    /**
     * 更新班级
     */
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Clazz clazz) {
        clazz.setClassId(id);
        boolean success = clazzService.updateById(clazz);
        return success ? Result.success(true) : Result.error("更新失败");
    }

    /**
     * 删除班级
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = clazzService.removeById(id);
        return success ? Result.success(true) : Result.error("删除失败");
    }
}
