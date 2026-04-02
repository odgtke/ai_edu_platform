-- 权限管理和消息通知模块数据库表结构
-- 创建时间: 2026-03-24

-- =============================================
-- 年级表 (edu_grade)
-- =============================================
CREATE TABLE IF NOT EXISTS edu_grade (
    grade_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '年级ID',
    grade_name VARCHAR(50) NOT NULL COMMENT '年级名称',
    grade_level INT NOT NULL COMMENT '年级级别(1-12)',
    description VARCHAR(200) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_grade_level (grade_level),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='年级表';

-- =============================================
-- 班级表 (edu_class)
-- =============================================
CREATE TABLE IF NOT EXISTS edu_class (
    class_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '班级ID',
    class_name VARCHAR(100) NOT NULL COMMENT '班级名称',
    grade_id BIGINT NOT NULL COMMENT '年级ID',
    teacher_id BIGINT COMMENT '班主任ID',
    teacher_name VARCHAR(100) COMMENT '班主任名称',
    student_count INT DEFAULT 0 COMMENT '学生人数',
    description VARCHAR(200) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_grade (grade_id),
    INDEX idx_teacher (teacher_id),
    INDEX idx_status (status),
    CONSTRAINT fk_class_grade FOREIGN KEY (grade_id) REFERENCES edu_grade(grade_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- =============================================
-- 用户班级关联表 (edu_user_class)
-- =============================================
CREATE TABLE IF NOT EXISTS edu_user_class (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    class_id BIGINT NOT NULL COMMENT '班级ID',
    user_type VARCHAR(20) NOT NULL COMMENT '用户类型: student-学生, teacher-教师',
    join_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_class (user_id, class_id),
    INDEX idx_user (user_id),
    INDEX idx_class (class_id),
    CONSTRAINT fk_userclass_user FOREIGN KEY (user_id) REFERENCES edu_user(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_userclass_class FOREIGN KEY (class_id) REFERENCES edu_class(class_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户班级关联表';

-- =============================================
-- 消息通知表 (edu_notification)
-- =============================================
CREATE TABLE IF NOT EXISTS edu_notification (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '消息ID',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    sender_name VARCHAR(100) COMMENT '发送者名称',
    receiver_id BIGINT NOT NULL COMMENT '接收者ID',
    receiver_type VARCHAR(20) DEFAULT 'user' COMMENT '接收类型: user-个人, class-班级, grade-年级, all-全部',
    title VARCHAR(200) NOT NULL COMMENT '消息标题',
    content TEXT COMMENT '消息内容',
    type VARCHAR(20) DEFAULT 'system' COMMENT '消息类型: system-系统, course-课程, assignment-作业, exam-考试',
    priority TINYINT DEFAULT 1 COMMENT '优先级: 1-普通, 2-重要, 3-紧急',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读: 0-未读, 1-已读',
    read_time DATETIME COMMENT '阅读时间',
    send_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_receiver (receiver_id),
    INDEX idx_sender (sender_id),
    INDEX idx_type (type),
    INDEX idx_is_read (is_read),
    INDEX idx_send_time (send_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知表';

-- =============================================
-- 插入年级测试数据
-- =============================================
INSERT INTO edu_grade (grade_name, grade_level, description) VALUES
('一年级', 1, '小学一年级'),
('二年级', 2, '小学二年级'),
('三年级', 3, '小学三年级'),
('四年级', 4, '小学四年级'),
('五年级', 5, '小学五年级'),
('六年级', 6, '小学六年级'),
('七年级', 7, '初中一年级'),
('八年级', 8, '初中二年级'),
('九年级', 9, '初中三年级'),
('高一', 10, '高中一年级'),
('高二', 11, '高中二年级'),
('高三', 12, '高中三年级');

-- =============================================
-- 插入班级测试数据
-- =============================================
INSERT INTO edu_class (class_name, grade_id, teacher_id, teacher_name, student_count, description) VALUES
('一年级1班', 1, 2, '张老师', 35, '一年级1班'),
('一年级2班', 1, 3, '李老师', 32, '一年级2班'),
('二年级1班', 2, 2, '张老师', 38, '二年级1班'),
('三年级1班', 3, 4, '王老师', 36, '三年级1班'),
('七年级1班', 7, 5, '赵老师', 40, '七年级1班'),
('高一1班', 10, 6, '陈老师', 45, '高一1班');

-- =============================================
-- 插入用户班级关联测试数据
-- =============================================
INSERT INTO edu_user_class (user_id, class_id, user_type) VALUES
(11, 1, 'student'),
(12, 1, 'student'),
(13, 2, 'student'),
(14, 3, 'student'),
(2, 1, 'teacher'),
(3, 2, 'teacher');

-- =============================================
-- 插入消息通知测试数据
-- =============================================
INSERT INTO edu_notification (sender_id, sender_name, receiver_id, receiver_type, title, content, type, priority) VALUES
(1, '系统管理员', 11, 'user', '欢迎加入智慧教育平台', '欢迎使用智慧教育平台，开始您的学习之旅！', 'system', 1),
(2, '张老师', 1, 'class', '数学作业提醒', '请同学们按时完成本周的数学作业。', 'assignment', 2),
(1, '系统管理员', 11, 'user', '新课程上线', '《Java编程入门》课程已上线，欢迎学习！', 'course', 1),
(2, '张老师', 11, 'user', '考试通知', '下周一进行期中考试，请做好准备。', 'exam', 3);
