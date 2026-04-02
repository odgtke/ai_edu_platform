package com.chinaunicom.edu.user.controller;

import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.user.entity.Grade;
import com.chinaunicom.edu.user.service.GradeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @GetMapping
    public Result<List<Grade>> list() {
        return Result.success(gradeService.list());
    }

    @GetMapping("/{id}")
    public Result<Grade> getById(@PathVariable Long id) {
        return Result.success(gradeService.getById(id));
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Grade grade) {
        return Result.success(gradeService.save(grade));
    }

    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Grade grade) {
        grade.setGradeId(id);
        return Result.success(gradeService.updateById(grade));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(gradeService.removeById(id));
    }
}
