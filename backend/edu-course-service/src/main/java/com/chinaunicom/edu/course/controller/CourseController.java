package com.chinaunicom.edu.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.course.entity.Course;
import com.chinaunicom.edu.course.service.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public Result<List<Course>> list() {
        return Result.success(courseService.list());
    }

    @GetMapping("/page")
    public Result<Page<Course>> page(@RequestParam(defaultValue = "1") Integer current,
                                      @RequestParam(defaultValue = "10") Integer size,
                                      @RequestParam(required = false) String courseName) {
        Page<Course> pageObj = new Page<>(current, size);
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        if (courseName != null && !courseName.isEmpty()) {
            queryWrapper.like("course_name", courseName);
        }
        queryWrapper.orderByDesc("create_time");
        Page<Course> result = courseService.page(pageObj, queryWrapper);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Course> getById(@PathVariable Long id) {
        Course course = courseService.getById(id);
        if (course == null) {
            return Result.error(404, "Course not found");
        }
        return Result.success(course);
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Course course) {
        return Result.success(courseService.save(course));
    }

    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Course course) {
        course.setCourseId(id);
        return Result.success(courseService.updateById(course));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(courseService.removeById(id));
    }

    @GetMapping("/count")
    public Result<Long> count() {
        return Result.success(courseService.count());
    }
}
