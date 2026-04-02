package com.chinaunicom.edu.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.course.entity.Resource;
import com.chinaunicom.edu.course.service.ResourceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping
    public Result<List<Resource>> list() {
        return Result.success(resourceService.list());
    }

    @GetMapping("/page")
    public Result<Page<Resource>> page(@RequestParam(defaultValue = "1") Integer current,
                                        @RequestParam(defaultValue = "10") Integer size,
                                        @RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) String resourceType) {
        Page<Resource> page = new Page<>(current, size);
        QueryWrapper<Resource> queryWrapper = new QueryWrapper<>();
        
        // 按名称搜索
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.like("resource_name", keyword);
        }
        
        // 按类型筛选
        if (resourceType != null && !resourceType.isEmpty()) {
            queryWrapper.eq("resource_type", resourceType);
        }
        
        // 按创建时间倒序
        queryWrapper.orderByDesc("create_time");
        
        return Result.success(resourceService.page(page, queryWrapper));
    }

    @GetMapping("/{id}")
    public Result<Resource> getById(@PathVariable Long id) {
        return Result.success(resourceService.getById(id));
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Resource resource) {
        return Result.success(resourceService.save(resource));
    }

    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Resource resource) {
        resource.setResourceId(id);
        return Result.success(resourceService.updateById(resource));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(resourceService.removeById(id));
    }
}
