package com.chinaunicom.edu.learning.controller;

import com.chinaunicom.edu.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @GetMapping("/student/{studentId}/overview")
    public Result<Map<String, Object>> getStudentOverview(@PathVariable Long studentId) {
        Map<String, Object> overview = new HashMap<>();
        overview.put("studentId", studentId);
        overview.put("totalLearningHours", 120);
        overview.put("completedCourses", 5);
        overview.put("averageScore", 85.5);
        overview.put("ranking", 10);
        return Result.success(overview);
    }

    @GetMapping("/student/{studentId}/calendar")
    public Result<Map<String, Object>> getLearningCalendar(@PathVariable Long studentId) {
        Map<String, Object> calendar = new HashMap<>();
        calendar.put("studentId", studentId);
        calendar.put("learningDays", 20);
        calendar.put("totalDuration", 3600);
        return Result.success(calendar);
    }

    @GetMapping("/course/{courseId}/statistics")
    public Result<Map<String, Object>> getCourseStatistics(@PathVariable Long courseId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("courseId", courseId);
        stats.put("totalStudents", 50);
        stats.put("averageProgress", 75.0);
        stats.put("completionRate", 0.8);
        return Result.success(stats);
    }
}
