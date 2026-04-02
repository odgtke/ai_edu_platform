-- 内容管理模块数据库表结构
-- 创建时间: 2026-03-23

-- =============================================
-- 资源表 (edu_resource)
-- 存储上传的视频、文档等资源文件
-- =============================================
CREATE TABLE IF NOT EXISTS edu_resource (
    resource_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '资源ID',
    resource_name VARCHAR(200) NOT NULL COMMENT '资源名称',
    resource_type VARCHAR(20) NOT NULL COMMENT '资源类型: video-视频, document-文档, image-图片, audio-音频, other-其他',
    file_name VARCHAR(500) NOT NULL COMMENT '原始文件名',
    file_path VARCHAR(1000) NOT NULL COMMENT '文件存储路径',
    file_size BIGINT DEFAULT 0 COMMENT '文件大小(字节)',
    file_format VARCHAR(20) COMMENT '文件格式(mp4, pdf, doc等)',
    duration INT DEFAULT 0 COMMENT '时长(秒), 视频/音频专用',
    thumbnail_url VARCHAR(500) COMMENT '缩略图URL, 视频/图片专用',
    description TEXT COMMENT '资源描述',
    uploader_id BIGINT NOT NULL COMMENT '上传者ID',
    uploader_name VARCHAR(100) COMMENT '上传者名称',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用, 2-转码中',
    download_count INT DEFAULT 0 COMMENT '下载次数',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_type (resource_type),
    INDEX idx_uploader (uploader_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源表';

-- =============================================
-- 课程资源关联表 (edu_course_resource)
-- 建立课程与资源的关联关系
-- =============================================
CREATE TABLE IF NOT EXISTS edu_course_resource (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    resource_id BIGINT NOT NULL COMMENT '资源ID',
    chapter_id BIGINT DEFAULT NULL COMMENT '章节ID(可选)',
    resource_order INT DEFAULT 0 COMMENT '资源排序',
    is_required TINYINT DEFAULT 1 COMMENT '是否必学: 0-选学, 1-必学',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_course_resource (course_id, resource_id),
    INDEX idx_course (course_id),
    INDEX idx_resource (resource_id),
    INDEX idx_chapter (chapter_id),
    CONSTRAINT fk_courseres_course FOREIGN KEY (course_id) REFERENCES edu_course(course_id) ON DELETE CASCADE,
    CONSTRAINT fk_courseres_resource FOREIGN KEY (resource_id) REFERENCES edu_resource(resource_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程资源关联表';

-- =============================================
-- 章节表 (edu_chapter) - 可选
-- 用于对课程资源进行章节分组
-- =============================================
CREATE TABLE IF NOT EXISTS edu_chapter (
    chapter_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '章节ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    chapter_name VARCHAR(200) NOT NULL COMMENT '章节名称',
    chapter_order INT DEFAULT 0 COMMENT '章节排序',
    description TEXT COMMENT '章节描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_course (course_id),
    INDEX idx_order (chapter_order),
    CONSTRAINT fk_chapter_course FOREIGN KEY (course_id) REFERENCES edu_course(course_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='章节表';

-- =============================================
-- 资源学习记录表 (edu_resource_learning)
-- 记录学生对资源的学习进度
-- =============================================
CREATE TABLE IF NOT EXISTS edu_resource_learning (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    resource_id BIGINT NOT NULL COMMENT '资源ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    progress INT DEFAULT 0 COMMENT '学习进度(百分比)',
    current_position INT DEFAULT 0 COMMENT '当前位置(秒), 视频专用',
    is_completed TINYINT DEFAULT 0 COMMENT '是否完成: 0-未完成, 1-已完成',
    last_study_time DATETIME COMMENT '最后学习时间',
    study_duration INT DEFAULT 0 COMMENT '学习时长(秒)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_student_resource (student_id, resource_id),
    INDEX idx_student (student_id),
    INDEX idx_resource (resource_id),
    INDEX idx_course (course_id),
    CONSTRAINT fk_reslearning_resource FOREIGN KEY (resource_id) REFERENCES edu_resource(resource_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源学习记录表';

-- =============================================
-- 插入测试数据
-- =============================================

-- 测试资源数据
INSERT INTO edu_resource (resource_name, resource_type, file_name, file_path, file_size, file_format, duration, uploader_id, uploader_name, description) VALUES
('Java基础入门视频', 'video', 'java-basic.mp4', '/uploads/videos/java-basic.mp4', 157286400, 'mp4', 3600, 1, '管理员', 'Java编程基础入门教学视频'),
('Spring Boot实战教程', 'video', 'springboot-tutorial.mp4', '/uploads/videos/springboot-tutorial.mp4', 262144000, 'mp4', 5400, 1, '管理员', 'Spring Boot框架实战开发教程'),
('课程讲义PDF', 'document', 'course-slides.pdf', '/uploads/documents/course-slides.pdf', 5242880, 'pdf', 0, 1, '管理员', '课程配套讲义文档'),
('课后练习题', 'document', 'exercises.docx', '/uploads/documents/exercises.docx', 1048576, 'docx', 0, 1, '管理员', '课后练习与答案'),
('课程封面图', 'image', 'cover.jpg', '/uploads/images/cover.jpg', 2097152, 'jpg', 0, 1, '管理员', '课程封面图片');

-- 测试课程资源关联
INSERT INTO edu_course_resource (course_id, resource_id, resource_order, is_required) VALUES
(1, 1, 1, 1),
(1, 3, 2, 1),
(1, 5, 0, 0),
(2, 2, 1, 1),
(2, 4, 2, 0);
