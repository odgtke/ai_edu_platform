package com.chinaunicom.edu.learning.controller;

import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.learning.entity.Assessment;
import com.chinaunicom.edu.learning.service.AssessmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessments")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @GetMapping
    public Result<List<Assessment>> list() {
        return Result.success(assessmentService.list());
    }

    @GetMapping("/{id}")
    public Result<Assessment> getById(@PathVariable Long id) {
        return Result.success(assessmentService.getById(id));
    }

    @GetMapping("/course/{courseId}")
    public Result<List<Assessment>> getByCourseId(@PathVariable Long courseId) {
        List<Assessment> assessments = assessmentService.lambdaQuery()
                .eq(Assessment::getCourseId, courseId)
                .list();
        return Result.success(assessments);
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Assessment assessment) {
        return Result.success(assessmentService.save(assessment));
    }

    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Assessment assessment) {
        assessment.setAssessmentId(id);
        return Result.success(assessmentService.updateById(assessment));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(assessmentService.removeById(id));
    }
}
