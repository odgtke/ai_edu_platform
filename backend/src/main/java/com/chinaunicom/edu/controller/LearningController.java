package com.chinaunicom.edu.controller;

import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.dto.LearningRecordDTO;
import com.chinaunicom.edu.entity.Course;
import com.chinaunicom.edu.entity.LearningRecord;
import com.chinaunicom.edu.service.AssignmentSubmissionService;
import com.chinaunicom.edu.service.CourseService;
import com.chinaunicom.edu.service.LearningRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 学习中心控制器
 */
@RestController
@RequestMapping("/learning")
public class LearningController {

    private static final Logger log = LoggerFactory.getLogger(LearningController.class);
    
    /** 默认课时数 */
    private static final int DEFAULT_TOTAL_LESSONS = 20;
    /** 每周目标学习时长（小时） */
    private static final int WEEKLY_TARGET_HOURS = 15;
    /** 最近记录数量 */
    private static final int RECENT_RECORDS_LIMIT = 5;
    /** 日历统计天数 */
    private static final int CALENDAR_DAYS = 30;
    
    private final LearningRecordService learningRecordService;
    private final CourseService courseService;
    private final AssignmentSubmissionService assignmentSubmissionService;
    
    public LearningController(LearningRecordService learningRecordService,
                             CourseService courseService,
                             AssignmentSubmissionService assignmentSubmissionService) {
        this.learningRecordService = learningRecordService;
        this.courseService = courseService;
        this.assignmentSubmissionService = assignmentSubmissionService;
    }

    /**
     * 获取学生学习记录（包含课程名称）
     */
    @GetMapping("/records/student/{studentId}")
    public Result<List<LearningRecordDTO>> getStudentRecords(@PathVariable Long studentId) {
        try {
            List<LearningRecord> records = learningRecordService.lambdaQuery()
                    .eq(LearningRecord::getStudentId, studentId)
                    .orderByDesc(LearningRecord::getLastStudyTime)
                    .list();
            
            // 批量查询课程信息，避免N+1查询
            Set<Long> courseIds = records.stream()
                    .map(LearningRecord::getCourseId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            
            Map<Long, Course> courseMap = courseIds.isEmpty() ? Collections.emptyMap() :
                    courseService.listByIds(courseIds).stream()
                            .collect(Collectors.toMap(Course::getCourseId, c -> c));
            
            // 转换为DTO并填充课程名称
            List<LearningRecordDTO> dtoList = records.stream()
                    .map(record -> convertToDTO(record, courseMap.get(record.getCourseId())))
                    .collect(Collectors.toList());
            
            return Result.success(dtoList);
        } catch (Exception e) {
            log.error("获取学生学习记录失败, studentId={}", studentId, e);
            return Result.error("获取学习记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 将LearningRecord转换为DTO
     */
    private LearningRecordDTO convertToDTO(LearningRecord record, Course course) {
        LearningRecordDTO dto = new LearningRecordDTO();
        dto.setRecordId(record.getRecordId());
        dto.setStudentId(record.getStudentId());
        dto.setCourseId(record.getCourseId());
        dto.setLessonId(record.getLessonId());
        dto.setStudyDuration(record.getStudyDuration());
        dto.setProgress(record.getProgress());
        dto.setIsCompleted(record.getIsCompleted());
        dto.setLastStudyTime(record.getLastStudyTime());
        dto.setCreateTime(record.getCreateTime());
        dto.setUpdateTime(record.getUpdateTime());
        
        // 填充课程信息
        if (course != null) {
            dto.setCourseName(course.getCourseName());
            int totalLessons = course.getTotalLessons() != null ? course.getTotalLessons() : DEFAULT_TOTAL_LESSONS;
            dto.setTotalLessons(totalLessons);
            // 根据进度计算已学课时
            dto.setCompletedLessons(Math.round(totalLessons * record.getProgress() / 100.0f));
        } else {
            dto.setCourseName("未知课程");
            dto.setTotalLessons(DEFAULT_TOTAL_LESSONS);
            dto.setCompletedLessons(0);
        }
        
        return dto;
    }

    /**
     * 获取课程学习记录
     */
    @GetMapping("/records/course/{courseId}")
    public Result<List<LearningRecord>> getCourseRecords(@PathVariable Long courseId) {
        try {
            List<LearningRecord> records = learningRecordService.lambdaQuery()
                    .eq(LearningRecord::getCourseId, courseId)
                    .orderByDesc(LearningRecord::getLastStudyTime)
                    .list();
            return Result.success(records);
        } catch (Exception e) {
            log.error("获取课程学习记录失败, courseId={}", courseId, e);
            return Result.error("获取课程学习记录失败: " + e.getMessage());
        }
    }

    /**
     * 创建或更新学习记录
     */
    @PostMapping("/record")
    public Result<Boolean> saveOrUpdateRecord(@RequestBody LearningRecord record) {
        try {
            boolean result = learningRecordService.saveOrUpdate(record);
            return Result.success(result);
        } catch (Exception e) {
            log.error("保存学习记录失败", e);
            return Result.error("保存学习记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取学生某课程的学习进度
     */
    @GetMapping("/progress/student/{studentId}/course/{courseId}")
    public Result<LearningRecord> getProgress(@PathVariable Long studentId, @PathVariable Long courseId) {
        try {
            LearningRecord record = learningRecordService.lambdaQuery()
                    .eq(LearningRecord::getStudentId, studentId)
                    .eq(LearningRecord::getCourseId, courseId)
                    .one();
            return Result.success(record);
        } catch (Exception e) {
            log.error("获取学习进度失败, studentId={}, courseId={}", studentId, courseId, e);
            return Result.error("获取学习进度失败: " + e.getMessage());
        }
    }

    /**
     * 获取学生学习进度
     */
    @GetMapping("/student/{studentId}/progress")
    public Result<Map<String, Object>> getStudentProgress(@PathVariable Long studentId) {
        try {
            // 查询该学生的所有学习记录
            List<LearningRecord> records = learningRecordService.lambdaQuery()
                    .eq(LearningRecord::getStudentId, studentId)
                    .list();
            
            // 计算统计数据
            int totalDuration = records.stream()
                    .mapToInt(LearningRecord::getStudyDuration)
                    .sum();
            
            long completedCourses = records.stream()
                    .filter(LearningRecord::getIsCompleted)
                    .count();
            
            double avgProgress = records.isEmpty() ? 0 : 
                records.stream().mapToInt(LearningRecord::getProgress).average().orElse(0);
            
            // 批量查询课程信息
            Set<Long> courseIds = records.stream()
                    .map(LearningRecord::getCourseId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            
            Map<Long, Course> courseMap = courseIds.isEmpty() ? Collections.emptyMap() :
                    courseService.listByIds(courseIds).stream()
                            .collect(Collectors.toMap(Course::getCourseId, c -> c));
            
            // 获取最近记录
            List<LearningRecordDTO> recentRecords = records.stream()
                    .sorted(Comparator.comparing(LearningRecord::getLastStudyTime).reversed())
                    .limit(RECENT_RECORDS_LIMIT)
                    .map(record -> convertToDTO(record, courseMap.get(record.getCourseId())))
                    .collect(Collectors.toList());
            
            // 获取作业提交率
            double assignmentSubmissionRate = assignmentSubmissionService.getSubmissionRate(studentId);
            
            // 构造统计结果
            Map<String, Object> progressInfo = new HashMap<>();
            progressInfo.put("totalStudyTime", totalDuration);
            progressInfo.put("completedCoursesCount", completedCourses);
            progressInfo.put("learningRecordsCount", records.size());
            progressInfo.put("averageProgress", Math.round(avgProgress * 100.0) / 100.0);
            progressInfo.put("assignmentSubmissionRate", assignmentSubmissionRate);
            progressInfo.put("recentRecords", recentRecords);
            
            return Result.success(progressInfo);
        } catch (Exception e) {
            log.error("获取学生学习进度失败, studentId={}", studentId, e);
            return Result.error("获取学习进度失败: " + e.getMessage());
        }
    }

    /**
     * 获取班级统计信息
     */
    @GetMapping("/class/{classId}/statistics")
    public Result<Map<String, Object>> getClassStatistics(@PathVariable Long classId) {
        try {
            // 根据实际业务逻辑实现班级统计
            Map<String, Object> classStats = new HashMap<>();
            classStats.put("classId", classId);
            classStats.put("totalStudents", 25);
            classStats.put("avgCompletionRate", 78.5);
            classStats.put("totalCourses", 8);
            classStats.put("updateTime", LocalDateTime.now().toString());
            
            return Result.success(classStats);
        } catch (Exception e) {
            log.error("获取班级统计失败, classId={}", classId, e);
            return Result.error("获取班级统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取学生的总体学习统计
     */
    @GetMapping("/statistics/student/{studentId}")
    public Result<Map<String, Object>> getStudentStatistics(@PathVariable Long studentId) {
        try {
            // 查询该学生的所有学习记录
            List<LearningRecord> records = learningRecordService.lambdaQuery()
                    .eq(LearningRecord::getStudentId, studentId)
                    .list();
            
            // 计算统计数据
            int totalDuration = records.stream()
                    .mapToInt(LearningRecord::getStudyDuration)
                    .sum();
            
            long completedCourses = records.stream()
                    .filter(LearningRecord::getIsCompleted)
                    .count();
            
            // 构造统计结果
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalStudyTime", totalDuration);
            statistics.put("completedCoursesCount", completedCourses);
            statistics.put("learningRecordsCount", records.size());
            
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取学生统计失败, studentId={}", studentId, e);
            return Result.error("获取学生统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取学习日历 - 按日期统计学习时长
     */
    @GetMapping("/student/{studentId}/calendar")
    public Result<List<Map<String, Object>>> getLearningCalendar(@PathVariable Long studentId) {
        try {
            // 查询最近30天的学习记录
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(CALENDAR_DAYS);
            List<LearningRecord> records = learningRecordService.lambdaQuery()
                    .eq(LearningRecord::getStudentId, studentId)
                    .ge(LearningRecord::getLastStudyTime, thirtyDaysAgo)
                    .list();
            
            // 按日期分组统计学习时长
            Map<String, Integer> dailyStudyTime = records.stream()
                    .collect(Collectors.groupingBy(
                        record -> record.getLastStudyTime().toLocalDate().toString(),
                        Collectors.summingInt(LearningRecord::getStudyDuration)
                    ));
            
            // 构建日历数据（最近30天）
            List<Map<String, Object>> calendarData = new ArrayList<>();
            LocalDate today = LocalDate.now();
            
            for (int i = CALENDAR_DAYS - 1; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                String dateStr = date.toString();
                int studyMinutes = dailyStudyTime.getOrDefault(dateStr, 0);
                
                Map<String, Object> dayData = new HashMap<>();
                dayData.put("date", dateStr);
                dayData.put("dayOfMonth", date.getDayOfMonth());
                dayData.put("dayOfWeek", date.getDayOfWeek().getValue());
                dayData.put("studyMinutes", studyMinutes);
                dayData.put("studyHours", Math.round(studyMinutes / 60.0 * 10) / 10.0);
                dayData.put("hasStudy", studyMinutes > 0);
                calendarData.add(dayData);
            }
            
            return Result.success(calendarData);
        } catch (Exception e) {
            log.error("获取学习日历失败, studentId={}", studentId, e);
            return Result.error("获取学习日历失败: " + e.getMessage());
        }
    }

    /**
     * 获取本周学习统计
     */
    @GetMapping("/student/{studentId}/weekly")
    public Result<Map<String, Object>> getWeeklyStatistics(@PathVariable Long studentId) {
        try {
            // 获取本周开始时间（周一）
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime weekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    .withHour(0).withMinute(0).withSecond(0);
            
            // 查询本周学习记录
            List<LearningRecord> records = learningRecordService.lambdaQuery()
                    .eq(LearningRecord::getStudentId, studentId)
                    .ge(LearningRecord::getLastStudyTime, weekStart)
                    .list();
            
            // 计算本周总学习时长
            int weeklyStudyMinutes = records.stream()
                    .mapToInt(LearningRecord::getStudyDuration)
                    .sum();
            
            // 按天统计
            Map<String, Integer> dailyStats = new HashMap<>();
            for (LearningRecord record : records) {
                String dayName = getDayName(record.getLastStudyTime().getDayOfWeek());
                dailyStats.merge(dayName, record.getStudyDuration(), Integer::sum);
            }
            
            // 构建返回数据
            Map<String, Object> weeklyData = new HashMap<>();
            weeklyData.put("weeklyStudyMinutes", weeklyStudyMinutes);
            weeklyData.put("weeklyStudyHours", Math.round(weeklyStudyMinutes / 60.0 * 10) / 10.0);
            weeklyData.put("targetHours", WEEKLY_TARGET_HOURS);
            weeklyData.put("completionRate", Math.min(100, Math.round((weeklyStudyMinutes / 60.0) / WEEKLY_TARGET_HOURS * 100)));
            weeklyData.put("dailyBreakdown", dailyStats);
            
            return Result.success(weeklyData);
        } catch (Exception e) {
            log.error("获取本周学习统计失败, studentId={}", studentId, e);
            return Result.error("获取本周学习统计失败: " + e.getMessage());
        }
    }
    
    private String getDayName(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY: return "周一";
            case TUESDAY: return "周二";
            case WEDNESDAY: return "周三";
            case THURSDAY: return "周四";
            case FRIDAY: return "周五";
            case SATURDAY: return "周六";
            case SUNDAY: return "周日";
            default: return "";
        }
    }
}