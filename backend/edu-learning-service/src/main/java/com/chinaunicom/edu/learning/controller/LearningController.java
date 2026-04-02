package com.chinaunicom.edu.learning.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.learning.entity.LearningRecord;
import com.chinaunicom.edu.learning.service.LearningRecordService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/learning")
public class LearningController {

    private final LearningRecordService learningRecordService;

    public LearningController(LearningRecordService learningRecordService) {
        this.learningRecordService = learningRecordService;
    }

    @GetMapping("/records")
    public Result<List<LearningRecord>> list() {
        return Result.success(learningRecordService.list());
    }

    @GetMapping("/records/page")
    public Result<Page<LearningRecord>> page(@RequestParam(defaultValue = "1") Integer current,
                                              @RequestParam(defaultValue = "10") Integer size) {
        Page<LearningRecord> page = new Page<>(current, size);
        return Result.success(learningRecordService.page(page));
    }

    @GetMapping("/records/{studentId}")
    public Result<List<LearningRecord>> getByStudentId(@PathVariable Long studentId) {
        List<LearningRecord> records = learningRecordService.lambdaQuery()
                .eq(LearningRecord::getStudentId, studentId)
                .list();
        return Result.success(records);
    }

    @PostMapping("/records")
    public Result<Boolean> save(@RequestBody LearningRecord record) {
        return Result.success(learningRecordService.save(record));
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 总学习时长（小时）
        List<LearningRecord> records = learningRecordService.list();
        int totalMinutes = records.stream()
                .mapToInt(LearningRecord::getStudyDuration)
                .sum();
        stats.put("totalHours", totalMinutes / 60);
        
        // 已完成课程数
        long completedCourses = records.stream()
                .filter(LearningRecord::getIsCompleted)
                .count();
        stats.put("completedCourses", completedCourses);
        
        // 学习记录总数
        stats.put("totalRecords", records.size());
        
        // 平均进度
        double avgProgress = records.isEmpty() ? 0 : 
                records.stream().mapToInt(LearningRecord::getProgress).average().orElse(0);
        stats.put("averageProgress", Math.round(avgProgress));
        
        return Result.success(stats);
    }

    @GetMapping("/calendar")
    public Result<Map<String, Object>> getCalendar(@RequestParam(required = false) String month) {
        Map<String, Object> result = new HashMap<>();
        
        // 如果没有指定月份，使用当前月份
        if (month == null || month.isEmpty()) {
            month = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }
        
        // 获取该月的学习记录
        List<LearningRecord> records = learningRecordService.list();
        
        // 按日期统计学习时长
        Map<String, Integer> dailyHours = new HashMap<>();
        for (LearningRecord record : records) {
            if (record.getLastStudyTime() != null) {
                String date = record.getLastStudyTime().toLocalDate().toString();
                int hours = record.getStudyDuration() / 60;
                dailyHours.put(date, dailyHours.getOrDefault(date, 0) + hours);
            }
        }
        
        result.put("month", month);
        result.put("dailyHours", dailyHours);
        
        return Result.success(result);
    }
}
