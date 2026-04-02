package com.chinaunicom.edu.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.user.entity.Clazz;
import com.chinaunicom.edu.user.service.ClazzService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
public class ClazzController {

    private final ClazzService clazzService;

    public ClazzController(ClazzService clazzService) {
        this.clazzService = clazzService;
    }

    @GetMapping
    public Result<List<Clazz>> list() {
        return Result.success(clazzService.list());
    }

    @GetMapping("/page")
    public Result<Page<Clazz>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String className,
            @RequestParam(required = false) Long gradeId,
            @RequestParam(required = false) Integer status) {
        Page<Clazz> page = new Page<>(current, size);
        QueryWrapper<Clazz> queryWrapper = new QueryWrapper<>();
        
        if (className != null && !className.isEmpty()) {
            queryWrapper.like("class_name", className);
        }
        if (gradeId != null) {
            queryWrapper.eq("grade_id", gradeId);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        
        queryWrapper.orderByDesc("create_time");
        return Result.success(clazzService.page(page, queryWrapper));
    }

    @GetMapping("/{id}")
    public Result<Clazz> getById(@PathVariable Long id) {
        return Result.success(clazzService.getById(id));
    }

    @GetMapping("/by-grade/{gradeId}")
    public Result<List<Clazz>> getByGrade(@PathVariable Long gradeId) {
        QueryWrapper<Clazz> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("grade_id", gradeId);
        return Result.success(clazzService.list(queryWrapper));
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Clazz clazz) {
        return Result.success(clazzService.save(clazz));
    }

    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Clazz clazz) {
        clazz.setClassId(id);
        return Result.success(clazzService.updateById(clazz));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(clazzService.removeById(id));
    }
}
