package com.chinaunicom.edu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chinaunicom.edu.entity.Resource;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 资源服务接口
 */
public interface ResourceService extends IService<Resource> {

    /**
     * 上传资源文件
     *
     * @param file         文件
     * @param resourceName 资源名称
     * @param description  资源描述
     * @param uploaderId   上传者ID
     * @param uploaderName 上传者名称
     * @return 资源信息
     */
    Resource uploadResource(MultipartFile file, String resourceName, String description,
                           Long uploaderId, String uploaderName);

    /**
     * 分页查询资源
     *
     * @param page        页码
     * @param size        每页大小
     * @param resourceType 资源类型
     * @param keyword     关键词
     * @return 分页结果
     */
    Page<Resource> getResourcePage(Integer page, Integer size, String resourceType, String keyword);

    /**
     * 根据课程ID查询资源列表
     *
     * @param courseId 课程ID
     * @return 资源列表
     */
    List<Resource> getResourcesByCourseId(Long courseId);

    /**
     * 下载资源
     *
     * @param resourceId 资源ID
     * @param response   HTTP响应
     */
    void downloadResource(Long resourceId, HttpServletResponse response);

    /**
     * 增加浏览次数
     *
     * @param resourceId 资源ID
     */
    void incrementViewCount(Long resourceId);

    /**
     * 删除资源（同时删除文件）
     *
     * @param resourceId 资源ID
     * @return 是否成功
     */
    boolean deleteResource(Long resourceId);

    /**
     * 获取文件大小格式化字符串
     *
     * @param size 文件大小（字节）
     * @return 格式化字符串
     */
    String formatFileSize(Long size);
}
