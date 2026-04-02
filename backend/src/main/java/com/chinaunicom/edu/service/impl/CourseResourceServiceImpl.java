package com.chinaunicom.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinaunicom.edu.entity.CourseResource;
import com.chinaunicom.edu.entity.Resource;
import com.chinaunicom.edu.exception.BusinessException;
import com.chinaunicom.edu.mapper.CourseResourceMapper;
import com.chinaunicom.edu.mapper.ResourceMapper;
import com.chinaunicom.edu.service.CourseResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 课程资源关联服务实现类
 */
@Service
public class CourseResourceServiceImpl extends ServiceImpl<CourseResourceMapper, CourseResource>
        implements CourseResourceService {

    private static final Logger log = LoggerFactory.getLogger(CourseResourceServiceImpl.class);

    private final ResourceMapper resourceMapper;

    public CourseResourceServiceImpl(ResourceMapper resourceMapper) {
        this.resourceMapper = resourceMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addResourceToCourse(Long courseId, Long resourceId, Integer isRequired) {
        // 检查是否已关联
        if (isResourceLinked(courseId, resourceId)) {
            throw new BusinessException("该资源已关联到此课程");
        }

        // 获取当前最大排序号
        LambdaQueryWrapper<CourseResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseResource::getCourseId, courseId);
        wrapper.orderByDesc(CourseResource::getResourceOrder);
        wrapper.last("LIMIT 1");
        CourseResource lastOne = this.getOne(wrapper);
        int newOrder = (lastOne != null ? lastOne.getResourceOrder() : 0) + 1;

        // 创建关联
        CourseResource courseResource = new CourseResource();
        courseResource.setCourseId(courseId);
        courseResource.setResourceId(resourceId);
        courseResource.setResourceOrder(newOrder);
        courseResource.setIsRequired(isRequired != null ? isRequired : 1);

        boolean result = this.save(courseResource);
        if (result) {
            log.info("资源关联课程成功: courseId={}, resourceId={}", courseId, resourceId);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeResourceFromCourse(Long courseId, Long resourceId) {
        LambdaQueryWrapper<CourseResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseResource::getCourseId, courseId)
                .eq(CourseResource::getResourceId, resourceId);

        boolean result = this.remove(wrapper);
        if (result) {
            log.info("资源从课程移除成功: courseId={}, resourceId={}", courseId, resourceId);
        }
        return result;
    }

    @Override
    public List<Resource> getCourseResources(Long courseId) {
        return resourceMapper.selectByCourseId(courseId);
    }

    @Override
    public boolean updateResourceOrder(Long id, Integer resourceOrder) {
        CourseResource courseResource = this.getById(id);
        if (courseResource == null) {
            throw new BusinessException("关联记录不存在");
        }

        courseResource.setResourceOrder(resourceOrder);
        return this.updateById(courseResource);
    }

    @Override
    public boolean updateRequiredStatus(Long id, Integer isRequired) {
        CourseResource courseResource = this.getById(id);
        if (courseResource == null) {
            throw new BusinessException("关联记录不存在");
        }

        courseResource.setIsRequired(isRequired);
        return this.updateById(courseResource);
    }

    @Override
    public boolean isResourceLinked(Long courseId, Long resourceId) {
        return baseMapper.checkExist(courseId, resourceId) > 0;
    }
}
