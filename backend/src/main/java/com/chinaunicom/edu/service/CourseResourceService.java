package com.chinaunicom.edu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinaunicom.edu.entity.CourseResource;
import com.chinaunicom.edu.entity.Resource;

import java.util.List;

/**
 * 课程资源关联服务接口
 */
public interface CourseResourceService extends IService<CourseResource> {

    /**
     * 关联资源到课程
     *
     * @param courseId   课程ID
     * @param resourceId 资源ID
     * @param isRequired 是否必学
     * @return 是否成功
     */
    boolean addResourceToCourse(Long courseId, Long resourceId, Integer isRequired);

    /**
     * 从课程中移除资源
     *
     * @param courseId   课程ID
     * @param resourceId 资源ID
     * @return 是否成功
     */
    boolean removeResourceFromCourse(Long courseId, Long resourceId);

    /**
     * 获取课程的资源列表
     *
     * @param courseId 课程ID
     * @return 资源列表
     */
    List<Resource> getCourseResources(Long courseId);

    /**
     * 更新资源排序
     *
     * @param id          关联ID
     * @param resourceOrder 排序号
     * @return 是否成功
     */
    boolean updateResourceOrder(Long id, Integer resourceOrder);

    /**
     * 设置资源是否必学
     *
     * @param id         关联ID
     * @param isRequired 是否必学
     * @return 是否成功
     */
    boolean updateRequiredStatus(Long id, Integer isRequired);

    /**
     * 检查课程是否已关联资源
     *
     * @param courseId   课程ID
     * @param resourceId 资源ID
     * @return 是否已关联
     */
    boolean isResourceLinked(Long courseId, Long resourceId);
}
