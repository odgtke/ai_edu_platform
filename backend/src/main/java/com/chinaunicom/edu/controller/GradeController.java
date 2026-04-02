package com.chinaunicom.edu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.entity.Grade;
import com.chinaunicom.edu.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 年级管理控制器
 */
@RestController
@RequestMapping("/grades")
public class GradeController {

    private final GradeService gradeService;

    @Autowired
    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    /**
     * 获取所有年级列表
     */
    @GetMapping("/list")
    public Result<List<Grade>> list() {
        List<Grade> list = gradeService.list();
        return Result.success(list);
    }

    /**
     * 获取启用的年级列表
     */
    @GetMapping("/active")
    public Result<List<Grade>> listActive() {
        List<Grade> list = gradeService.listActiveGrades();
        return Result.success(list);
    }

    /**
     * 根据ID获取年级
     */
    @GetMapping("/{id}")
    public Result<Grade> getById(@PathVariable Long id) {
        Grade grade = gradeService.getById(id);
        if (grade == null) {
            return Result.error("年级不存在");
        }
        return Result.success(grade);
    }

    /**
     * 新增年级
     */
    @PostMapping
    public Result<Boolean> save(@RequestBody Grade grade) {
        boolean success = gradeService.save(grade);
        return success ? Result.success(true) : Result.error("添加失败");
    }

    /**
     * 更新年级
     */
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Grade grade) {
        grade.setGradeId(id);
        boolean success = gradeService.updateById(grade);
        return success ? Result.success(true) : Result.error("更新失败");
    }

    /**
     * 删除年级
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = gradeService.removeById(id);
        return success ? Result.success(true) : Result.error("删除失败");
    }
}
