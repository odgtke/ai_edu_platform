package com.chinaunicom.edu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.entity.Course;
import com.chinaunicom.edu.service.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程控制器
 */
@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;
    
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * 分页查询课程
     */
    @GetMapping("/page")
    public Result<Page<Course>> getPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) String courseCode,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) Integer gradeLevel,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Integer status) {
        
        Page<Course> pageObj = new Page<>(page, size);
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        
        // 添加查询条件
        if (courseName != null && !courseName.isEmpty()) {
            queryWrapper.like("course_name", courseName);
        }
        if (courseCode != null && !courseCode.isEmpty()) {
            queryWrapper.like("course_code", courseCode);
        }
        if (teacherId != null) {
            queryWrapper.eq("teacher_id", teacherId);
        }
        if (gradeLevel != null) {
            queryWrapper.eq("grade_level", gradeLevel);
        }
        if (subjectId != null) {
            queryWrapper.eq("subject_id", subjectId);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        
        // 按创建时间降序排列
        queryWrapper.orderByDesc("create_time");
        
        Page<Course> result = courseService.page(pageObj, queryWrapper);
        return Result.success(result);
    }

    /**
     * 获取所有课程
     */
    @GetMapping
    public Result<List<Course>> getAll() {
        List<Course> list = courseService.list();
        return Result.success(list);
    }

    /**
     * 根据ID获取课程
     */
    @GetMapping("/{id}")
    public Result<Course> getById(@PathVariable Long id) {
        Course course = courseService.getById(id);
        return Result.success(course);
    }

    /**
     * 创建课程
     */
    @PostMapping
    public Result<Boolean> create(@RequestBody Course course) {
        boolean result = courseService.save(course);
        return Result.success(result);
    }

    /**
     * 更新课程
     */
    @PutMapping
    public Result<Boolean> update(@RequestBody Course course) {
        boolean result = courseService.updateById(course);
        return Result.success(result);
    }

    /**
     * 获取课程总数
     */
    @GetMapping("/count")
    public Result<Long> getCount() {
        long count = courseService.count();
        return Result.success(count);
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean result = courseService.removeById(id);
        return Result.success(result);
    }

    /**
     * 根据教师ID获取课程列表
     */
    @GetMapping("/teacher/{teacherId}")
    public Result<List<Course>> getByTeacherId(@PathVariable Long teacherId) {
        List<Course> courses = courseService.lambdaQuery()
                .eq(Course::getTeacherId, teacherId)
                .list();
        return Result.success(courses);
    }

    /**
     * 根据年级获取课程列表
     */
    @GetMapping("/grade/{gradeLevel}")
    public Result<List<Course>> getByGradeLevel(@PathVariable Integer gradeLevel) {
        List<Course> courses = courseService.lambdaQuery()
                .eq(Course::getGradeLevel, gradeLevel)
                .list();
        return Result.success(courses);
    }
}