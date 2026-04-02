package com.chinaunicom.edu.learning.controller;

import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.learning.entity.Assignment;
import com.chinaunicom.edu.learning.service.AssignmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping
    public Result<List<Assignment>> list() {
        return Result.success(assignmentService.list());
    }

    @GetMapping("/{id}")
    public Result<Assignment> getById(@PathVariable Long id) {
        return Result.success(assignmentService.getById(id));
    }

    @GetMapping("/course/{courseId}")
    public Result<List<Assignment>> getByCourseId(@PathVariable Long courseId) {
        List<Assignment> assignments = assignmentService.lambdaQuery()
                .eq(Assignment::getCourseId, courseId)
                .list();
        return Result.success(assignments);
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Assignment assignment) {
        return Result.success(assignmentService.save(assignment));
    }

    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Assignment assignment) {
        assignment.setAssignmentId(id);
        return Result.success(assignmentService.updateById(assignment));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(assignmentService.removeById(id));
    }
}
