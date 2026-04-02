-- 智慧教育平台数据库表结构定义

-- 用户表
CREATE TABLE IF NOT EXISTS edu_user (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    user_type TINYINT NOT NULL COMMENT '用户类型：1-学生 2-教师 3-家长 4-管理员',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    avatar VARCHAR(200) COMMENT '头像URL',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_phone (phone),
    INDEX idx_email (email),
    INDEX idx_user_type (user_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 课程表
CREATE TABLE IF NOT EXISTS edu_course (
    course_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '课程ID',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    course_code VARCHAR(50) UNIQUE NOT NULL COMMENT '课程编码',
    description TEXT COMMENT '课程描述',
    teacher_id BIGINT NOT NULL COMMENT '授课教师ID',
    grade_level TINYINT NOT NULL COMMENT '年级',
    subject_id BIGINT NOT NULL COMMENT '学科ID',
    credit DECIMAL(3,1) DEFAULT 0 COMMENT '学分',
    status TINYINT DEFAULT 1 COMMENT '状态：0-停用 1-启用',
    cover_image VARCHAR(200) COMMENT '封面图片',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (teacher_id) REFERENCES edu_user(user_id),
    INDEX idx_course_code (course_code),
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_grade_level (grade_level),
    INDEX idx_subject_id (subject_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

-- 学习记录表
CREATE TABLE IF NOT EXISTS edu_learning_record (
    record_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    lesson_id BIGINT COMMENT '课时ID',
    study_duration INT DEFAULT 0 COMMENT '学习时长（分钟）',
    progress TINYINT DEFAULT 0 COMMENT '学习进度百分比',
    is_completed BOOLEAN DEFAULT FALSE COMMENT '是否完成',
    last_study_time DATETIME COMMENT '最后学习时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (student_id) REFERENCES edu_user(user_id),
    FOREIGN KEY (course_id) REFERENCES edu_course(course_id),
    INDEX idx_student_course (student_id, course_id),
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_lesson_id (lesson_id),
    INDEX idx_progress (progress),
    INDEX idx_is_completed (is_completed),
    INDEX idx_last_study_time (last_study_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习记录表';

-- 学科表
CREATE TABLE IF NOT EXISTS edu_subject (
    subject_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '学科ID',
    subject_name VARCHAR(50) NOT NULL COMMENT '学科名称',
    subject_code VARCHAR(20) UNIQUE NOT NULL COMMENT '学科编码',
    description VARCHAR(200) COMMENT '学科描述',
    grade_level TINYINT COMMENT '适用年级',
    status TINYINT DEFAULT 1 COMMENT '状态：0-停用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_subject_name (subject_name),
    INDEX idx_subject_code (subject_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学科表';

-- 课时表
CREATE TABLE IF NOT EXISTS edu_lesson (
    lesson_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '课时ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    lesson_title VARCHAR(100) NOT NULL COMMENT '课时标题',
    lesson_order INT NOT NULL COMMENT '课时序号',
    content LONGTEXT COMMENT '课时内容',
    video_url VARCHAR(200) COMMENT '视频地址',
    duration INT COMMENT '时长(分钟)',
    status TINYINT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (course_id) REFERENCES edu_course(course_id),
    INDEX idx_course_id (course_id),
    INDEX idx_lesson_order (lesson_order),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课时表';

-- 作业表
CREATE TABLE IF NOT EXISTS edu_assignment (
    assignment_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '作业ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    title VARCHAR(100) NOT NULL COMMENT '作业标题',
    content TEXT COMMENT '作业内容',
    deadline DATETIME NOT NULL COMMENT '截止时间',
    assign_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '布置时间',
    status TINYINT DEFAULT 1 COMMENT '状态：0-草稿 1-发布 2-结束',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (course_id) REFERENCES edu_course(course_id),
    FOREIGN KEY (teacher_id) REFERENCES edu_user(user_id),
    INDEX idx_course_id (course_id),
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_deadline (deadline),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作业表';

-- 学生作业提交表
CREATE TABLE IF NOT EXISTS edu_student_assignment (
    submit_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '提交ID',
    assignment_id BIGINT NOT NULL COMMENT '作业ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    content TEXT COMMENT '提交内容',
    attachment_url VARCHAR(200) COMMENT '附件地址',
    submit_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    score DECIMAL(5,2) COMMENT '得分',
    feedback TEXT COMMENT '教师评语',
    status TINYINT DEFAULT 0 COMMENT '状态：0-未提交 1-已提交 2-已批改',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (assignment_id) REFERENCES edu_assignment(assignment_id),
    FOREIGN KEY (student_id) REFERENCES edu_user(user_id),
    INDEX idx_assignment_id (assignment_id),
    INDEX idx_student_id (student_id),
    INDEX idx_submit_time (submit_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生作业提交表';

-- 角色表
CREATE TABLE IF NOT EXISTS edu_role (
    role_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    role_name VARCHAR(50) UNIQUE NOT NULL COMMENT '角色名称',
    role_code VARCHAR(20) UNIQUE NOT NULL COMMENT '角色编码',
    description VARCHAR(200) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_role_name (role_name),
    INDEX idx_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS edu_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES edu_user(user_id),
    FOREIGN KEY (role_id) REFERENCES edu_role(role_id),
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 权限表
CREATE TABLE IF NOT EXISTS edu_permission (
    permission_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '权限ID',
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(50) UNIQUE NOT NULL COMMENT '权限编码',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID',
    type TINYINT DEFAULT 1 COMMENT '权限类型：1-菜单 2-按钮',
    url VARCHAR(200) COMMENT '权限URL',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent_id (parent_id),
    INDEX idx_permission_code (permission_code),
    INDEX idx_type (type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS edu_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES edu_role(role_id),
    FOREIGN KEY (permission_id) REFERENCES edu_permission(permission_id),
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 初始化基础数据
-- 插入默认角色
INSERT IGNORE INTO edu_role (role_name, role_code, description) VALUES
('超级管理员', 'ADMIN', '系统超级管理员'),
('教师', 'TEACHER', '授课教师'),
('学生', 'STUDENT', '学习学生'),
('家长', 'PARENT', '学生家长');

-- 插入默认用户类型
-- 用户类型：1-学生 2-教师 3-家长 4-管理员