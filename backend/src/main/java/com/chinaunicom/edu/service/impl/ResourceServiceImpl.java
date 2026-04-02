package com.chinaunicom.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinaunicom.edu.entity.Resource;
import com.chinaunicom.edu.exception.BusinessException;
import com.chinaunicom.edu.mapper.ResourceMapper;
import com.chinaunicom.edu.service.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 资源服务实现类
 */
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {

    private static final Logger log = LoggerFactory.getLogger(ResourceServiceImpl.class);

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    // 允许的文件类型
    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList("mp4", "avi", "mov", "wmv", "flv", "mkv");
    private static final List<String> ALLOWED_DOC_TYPES = Arrays.asList("pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt");
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp");
    private static final List<String> ALLOWED_AUDIO_TYPES = Arrays.asList("mp3", "wav", "wma", "aac", "flac");

    // 最大文件大小 500MB
    private static final long MAX_FILE_SIZE = 500 * 1024 * 1024;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resource uploadResource(MultipartFile file, String resourceName, String description,
                                   Long uploaderId, String uploaderName) {
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("文件大小不能超过500MB");
        }

        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);

        // 判断资源类型
        String resourceType = determineResourceType(extension);
        if (resourceType == null) {
            throw new BusinessException("不支持的文件类型: " + extension);
        }

        try {
            // 生成存储路径
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String savePath = uploadPath + "/" + resourceType + "s/" + datePath;
            File dir = new File(savePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成新文件名
            String newFileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
            String fullPath = savePath + "/" + newFileName;

            // 保存文件
            File destFile = new File(fullPath);
            file.transferTo(destFile);

            // 创建资源记录
            Resource resource = new Resource();
            resource.setResourceName(StringUtils.hasText(resourceName) ? resourceName : originalFilename);
            resource.setResourceType(resourceType);
            resource.setFileName(originalFilename);
            resource.setFilePath(fullPath.replace(uploadPath, ""));
            resource.setFileSize(file.getSize());
            resource.setFileFormat(extension.toLowerCase());
            resource.setDescription(description);
            resource.setUploaderId(uploaderId);
            resource.setUploaderName(uploaderName);
            resource.setStatus(1);
            resource.setDownloadCount(0);
            resource.setViewCount(0);

            // 如果是视频，可以提取时长信息
            if ("video".equals(resourceType)) {
                resource.setDuration(0); // 需要集成视频处理工具提取时长
            }

            this.save(resource);
            log.info("资源上传成功: {}, 类型: {}, 大小: {}", resource.getResourceName(), resourceType, formatFileSize(file.getSize()));

            return resource;
        } catch (Exception e) {
            log.error("资源上传失败", e);
            throw new BusinessException("资源上传失败: " + e.getMessage());
        }
    }

    @Override
    public Page<Resource> getResourcePage(Integer page, Integer size, String resourceType, String keyword) {
        LambdaQueryWrapper<Resource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resource::getStatus, 1);

        // 按类型筛选
        if (StringUtils.hasText(resourceType)) {
            wrapper.eq(Resource::getResourceType, resourceType);
        }

        // 按关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Resource::getResourceName, keyword)
                    .or()
                    .like(Resource::getDescription, keyword));
        }

        wrapper.orderByDesc(Resource::getCreateTime);
        return this.page(new Page<>(page, size), wrapper);
    }

    @Override
    public List<Resource> getResourcesByCourseId(Long courseId) {
        return baseMapper.selectByCourseId(courseId);
    }

    @Override
    public void downloadResource(Long resourceId, HttpServletResponse response) {
        Resource resource = this.getById(resourceId);
        if (resource == null || resource.getStatus() != 1) {
            throw new BusinessException("资源不存在或已禁用");
        }

        File file = new File(uploadPath + resource.getFilePath());
        if (!file.exists()) {
            throw new BusinessException("文件不存在");
        }

        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {

            // 设置响应头
            response.setContentType("application/octet-stream");
            response.setContentLengthLong(file.length());
            String encodedFileName = URLEncoder.encode(resource.getFileName(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");

            // 写入文件内容
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();

            // 增加下载次数
            baseMapper.incrementDownloadCount(resourceId);
            log.info("资源下载成功: {}", resource.getResourceName());

        } catch (Exception e) {
            log.error("资源下载失败", e);
            throw new BusinessException("资源下载失败: " + e.getMessage());
        }
    }

    @Override
    public void incrementViewCount(Long resourceId) {
        baseMapper.incrementViewCount(resourceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteResource(Long resourceId) {
        Resource resource = this.getById(resourceId);
        if (resource == null) {
            return false;
        }

        // 删除物理文件
        try {
            File file = new File(uploadPath + resource.getFilePath());
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            log.warn("删除物理文件失败: {}", e.getMessage());
        }

        // 删除数据库记录
        return this.removeById(resourceId);
    }

    @Override
    public String formatFileSize(Long size) {
        if (size == null || size <= 0) {
            return "0 B";
        }
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int index = 0;
        double fileSize = size;
        while (fileSize >= 1024 && index < units.length - 1) {
            fileSize /= 1024;
            index++;
        }
        return String.format("%.2f %s", fileSize, units[index]);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 判断资源类型
     */
    private String determineResourceType(String extension) {
        if (ALLOWED_VIDEO_TYPES.contains(extension)) {
            return "video";
        } else if (ALLOWED_DOC_TYPES.contains(extension)) {
            return "document";
        } else if (ALLOWED_IMAGE_TYPES.contains(extension)) {
            return "image";
        } else if (ALLOWED_AUDIO_TYPES.contains(extension)) {
            return "audio";
        }
        return "other";
    }
}
