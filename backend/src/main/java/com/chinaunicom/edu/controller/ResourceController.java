package com.chinaunicom.edu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.entity.Resource;
import com.chinaunicom.edu.service.CourseResourceService;
import com.chinaunicom.edu.service.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资源管理控制器
 */
@RestController
@RequestMapping("/resources")
public class ResourceController {

    private static final Logger log = LoggerFactory.getLogger(ResourceController.class);

    private final ResourceService resourceService;
    private final CourseResourceService courseResourceService;

    // 允许的资源类型
    private static final List<String> ALLOWED_TYPES = Arrays.asList("video", "document", "image", "audio", "other");

    public ResourceController(ResourceService resourceService, CourseResourceService courseResourceService) {
        this.resourceService = resourceService;
        this.courseResourceService = courseResourceService;
    }

    /**
     * 上传资源文件
     */
    @PostMapping("/upload")
    public Result<Resource> uploadResource(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "resourceName", required = false) String resourceName,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("uploaderId") Long uploaderId,
            @RequestParam("uploaderName") String uploaderName) {
        try {
            Resource resource = resourceService.uploadResource(file, resourceName, description, uploaderId, uploaderName);
            return Result.success("上传成功", resource);
        } catch (Exception e) {
            log.error("资源上传失败", e);
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询资源列表
     */
    @GetMapping("/page")
    public Result<Page<Resource>> getResourcePage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) String keyword) {
        try {
            // 验证资源类型（忽略空字符串）
            if (resourceType != null && !resourceType.isEmpty() && !ALLOWED_TYPES.contains(resourceType)) {
                return Result.validateFailed("无效的资源类型");
            }

            Page<Resource> result = resourceService.getResourcePage(page, size, resourceType, keyword);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询资源列表失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有资源类型
     */
    @GetMapping("/types")
    public Result<List<Map<String, String>>> getResourceTypes() {
        List<Map<String, String>> types = Arrays.asList(
                createTypeMap("video", "视频"),
                createTypeMap("document", "文档"),
                createTypeMap("image", "图片"),
                createTypeMap("audio", "音频"),
                createTypeMap("other", "其他")
        );
        return Result.success(types);
    }

    private Map<String, String> createTypeMap(String value, String label) {
        Map<String, String> map = new HashMap<>();
        map.put("value", value);
        map.put("label", label);
        return map;
    }

    /**
     * 根据ID获取资源详情
     */
    @GetMapping("/{resourceId}")
    public Result<Resource> getResourceById(@PathVariable Long resourceId) {
        try {
            Resource resource = resourceService.getById(resourceId);
            if (resource == null || resource.getStatus() != 1) {
                return Result.error("资源不存在或已禁用");
            }
            // 增加浏览次数
            resourceService.incrementViewCount(resourceId);
            return Result.success(resource);
        } catch (Exception e) {
            log.error("获取资源详情失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 下载资源
     */
    @GetMapping("/{resourceId}/download")
    public void downloadResource(@PathVariable Long resourceId, HttpServletResponse response) {
        try {
            resourceService.downloadResource(resourceId, response);
        } catch (Exception e) {
            log.error("资源下载失败", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 删除资源
     */
    @DeleteMapping("/{resourceId}")
    public Result<Boolean> deleteResource(@PathVariable Long resourceId) {
        try {
            boolean result = resourceService.deleteResource(resourceId);
            if (result) {
                return Result.success("删除成功", true);
            } else {
                return Result.error("删除失败：资源不存在");
            }
        } catch (Exception e) {
            log.error("删除资源失败", e);
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取课程的资源列表
     */
    @GetMapping("/course/{courseId}")
    public Result<List<Resource>> getCourseResources(@PathVariable Long courseId) {
        try {
            List<Resource> resources = courseResourceService.getCourseResources(courseId);
            return Result.success(resources);
        } catch (Exception e) {
            log.error("获取课程资源失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 关联资源到课程
     */
    @PostMapping("/course/{courseId}/resource/{resourceId}")
    public Result<Boolean> addResourceToCourse(
            @PathVariable Long courseId,
            @PathVariable Long resourceId,
            @RequestParam(required = false, defaultValue = "1") Integer isRequired) {
        try {
            boolean result = courseResourceService.addResourceToCourse(courseId, resourceId, isRequired);
            return Result.success("关联成功", result);
        } catch (Exception e) {
            log.error("关联资源到课程失败", e);
            return Result.error("关联失败: " + e.getMessage());
        }
    }

    /**
     * 从课程中移除资源
     */
    @DeleteMapping("/course/{courseId}/resource/{resourceId}")
    public Result<Boolean> removeResourceFromCourse(
            @PathVariable Long courseId,
            @PathVariable Long resourceId) {
        try {
            boolean result = courseResourceService.removeResourceFromCourse(courseId, resourceId);
            return Result.success("移除成功", result);
        } catch (Exception e) {
            log.error("从课程移除资源失败", e);
            return Result.error("移除失败: " + e.getMessage());
        }
    }

    /**
     * 检查资源是否已关联课程
     */
    @GetMapping("/course/{courseId}/check/{resourceId}")
    public Result<Boolean> checkResourceLinked(
            @PathVariable Long courseId,
            @PathVariable Long resourceId) {
        try {
            boolean linked = courseResourceService.isResourceLinked(courseId, resourceId);
            return Result.success(linked);
        } catch (Exception e) {
            log.error("检查资源关联状态失败", e);
            return Result.error("检查失败: " + e.getMessage());
        }
    }
}
