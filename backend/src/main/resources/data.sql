-- 创建用户表
CREATE TABLE IF NOT EXISTS edu_user (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) UNIQUE,
    email VARCHAR(100),
    user_type INT NOT NULL,
    status INT DEFAULT 1,
    avatar VARCHAR(200),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建课程表
CREATE TABLE IF NOT EXISTS edu_course (
    course_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(100) NOT NULL,
    course_code VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    teacher_id BIGINT NOT NULL,
    grade_level INT NOT NULL,
    subject_id BIGINT NOT NULL,
    credit DECIMAL(3,1) DEFAULT 0,
    status INT DEFAULT 1,
    cover_image VARCHAR(200),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建学习记录表
CREATE TABLE IF NOT EXISTS edu_learning_record (
    record_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    lesson_id BIGINT,
    study_duration INT DEFAULT 0,
    progress INT DEFAULT 0,
    is_completed BOOLEAN DEFAULT FALSE,
    last_study_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 插入测试数据
INSERT INTO edu_user (username, password, real_name, phone, email, user_type, status) SELECT 'admin', '$2a$10$abcdefghijklmnopqrstuvABCDEFGHIJKLMNO', 'Admin', '13800138000', 'admin@example.com', 4, 1 WHERE NOT EXISTS (SELECT 1 FROM edu_user WHERE username = 'admin');
INSERT INTO edu_user (username, password, real_name, phone, email, user_type, status) SELECT 'teacher001', '$2a$10$abcdefghijklmnopqrstuvABCDEFGHIJKLMNO', 'Teacher Zhang', '13800138001', 'teacher001@example.com', 2, 1 WHERE NOT EXISTS (SELECT 1 FROM edu_user WHERE username = 'teacher001');
INSERT INTO edu_user (username, password, real_name, phone, email, user_type, status) SELECT 'student001', '$2a$10$abcdefghijklmnopqrstuvABCDEFGHIJKLMNO', 'Li Ming', '13800138002', 'student001@example.com', 1, 1 WHERE NOT EXISTS (SELECT 1 FROM edu_user WHERE username = 'student001');
INSERT INTO edu_user (username, password, real_name, phone, email, user_type, status) SELECT 'student002', '$2a$10$abcdefghijklmnopqrstuvABCDEFGHIJKLMNO', 'Wang Xiaohong', '13800138003', 'student002@example.com', 1, 1 WHERE NOT EXISTS (SELECT 1 FROM edu_user WHERE username = 'student002');
INSERT INTO edu_user (username, password, real_name, phone, email, user_type, status) SELECT 'student003', '$2a$10$abcdefghijklmnopqrstuvABCDEFGHIJKLMNO', 'Xiao Li', '13800138004', 'student003@example.com', 1, 1 WHERE NOT EXISTS (SELECT 1 FROM edu_user WHERE username = 'student003');
INSERT INTO edu_user (username, password, real_name, phone, email, user_type, status) SELECT 'student004', '$2a$10$abcdefghijklmnopqrstuvABCDEFGHIJKLMNO', 'Chen Yang', '13800138005', 'student004@example.com', 1, 1 WHERE NOT EXISTS (SELECT 1 FROM edu_user WHERE username = 'student004');
INSERT INTO edu_user (username, password, real_name, phone, email, user_type, status) SELECT 'student005', '$2a$10$abcdefghijklmnopqrstuvABCDEFGHIJKLMNO', 'Liu Wei', '13800138006', 'student005@example.com', 1, 1 WHERE NOT EXISTS (SELECT 1 FROM edu_user WHERE username = 'student005');
INSERT INTO edu_user (username, password, real_name, phone, email, user_type, status) SELECT 'student006', '$2a$10$abcdefghijklmnopqrstuvABCDEFGHIJKLMNO', 'Zhao Juan', '13800138007', 'student006@example.com', 1, 1 WHERE NOT EXISTS (SELECT 1 FROM edu_user WHERE username = 'student006');
INSERT INTO edu_user (username, password, real_name, phone, email, user_type, status) SELECT 'teacher002', '$2a$10$abcdefghijklmnopqrstuvABCDEFGHIJKLMNO', 'Teacher Wang', '13800138008', 'teacher002@example.com', 2, 1 WHERE NOT EXISTS (SELECT 1 FROM edu_user WHERE username = 'teacher002');
INSERT INTO edu_user (username, password, real_name, phone, email, user_type, status) SELECT 'teacher003', '$2a$10$abcdefghijklmnopqrstuvABCDEFGHIJKLMNO', 'Teacher Liu', '13800138009', 'teacher003@example.com', 2, 1 WHERE NOT EXISTS (SELECT 1 FROM edu_user WHERE username = 'teacher003');
INSERT INTO edu_user (username, password, real_name, phone, email, user_type, status) SELECT 'parent001', '$2a$10$abcdefghijklmnopqrstuvABCDEFGHIJKLMNO', 'Parent Li', '13800138010', 'parent001@example.com', 3, 1 WHERE NOT EXISTS (SELECT 1 FROM edu_user WHERE username = 'parent001');
INSERT INTO edu_user (username, password, real_name, phone, email, user_type, status) SELECT 'parent002', '$2a$10$abcdefghijklmnopqrstuvABCDEFGHIJKLMNO', 'Parent Wang', '13800138011', 'parent002@example.com', 3, 1 WHERE NOT EXISTS (SELECT 1 FROM edu_user WHERE username = 'parent002');

INSERT INTO edu_course (course_name, course_code, description, teacher_id, grade_level, subject_id, credit, status) SELECT 'Advanced Mathematics', 'MATH101', 'College Advanced Mathematics Course', 2, 1, 1, 4.0, 1 WHERE NOT EXISTS (SELECT 1 FROM edu_course WHERE course_code = 'MATH101');
INSERT INTO edu_course (course_name, course_code, description, teacher_id, grade_level, subject_id, credit, status) SELECT 'College English', 'ENG101', 'College English Foundation Course', 2, 1, 2, 3.0, 1 WHERE NOT EXISTS (SELECT 1 FROM edu_course WHERE course_code = 'ENG101');
INSERT INTO edu_course (course_name, course_code, description, teacher_id, grade_level, subject_id, credit, status) SELECT 'Computer Science', 'CS101', 'Introduction to Computer Science', 2, 1, 3, 3.5, 1 WHERE NOT EXISTS (SELECT 1 FROM edu_course WHERE course_code = 'CS101');
INSERT INTO edu_course (course_name, course_code, description, teacher_id, grade_level, subject_id, credit, status) SELECT 'Physics', 'PHY101', 'College Physics Foundation', 2, 1, 4, 4.0, 1 WHERE NOT EXISTS (SELECT 1 FROM edu_course WHERE course_code = 'PHY101');
INSERT INTO edu_course (course_name, course_code, description, teacher_id, grade_level, subject_id, credit, status) SELECT 'Chemistry', 'CHEM101', 'General Chemistry Principles', 2, 1, 5, 3.5, 1 WHERE NOT EXISTS (SELECT 1 FROM edu_course WHERE course_code = 'CHEM101');
INSERT INTO edu_course (course_name, course_code, description, teacher_id, grade_level, subject_id, credit, status) SELECT 'Biology', 'BIO101', 'Introduction to Biology', 3, 1, 6, 3.0, 1 WHERE NOT EXISTS (SELECT 1 FROM edu_course WHERE course_code = 'BIO101');
INSERT INTO edu_course (course_name, course_code, description, teacher_id, grade_level, subject_id, credit, status) SELECT 'History', 'HIST101', 'World History Survey', 4, 1, 7, 3.0, 1 WHERE NOT EXISTS (SELECT 1 FROM edu_course WHERE course_code = 'HIST101');
INSERT INTO edu_course (course_name, course_code, description, teacher_id, grade_level, subject_id, credit, status) SELECT 'Economics', 'ECON101', 'Principles of Economics', 4, 2, 8, 3.5, 1 WHERE NOT EXISTS (SELECT 1 FROM edu_course WHERE course_code = 'ECON101');
INSERT INTO edu_course (course_name, course_code, description, teacher_id, grade_level, subject_id, credit, status) SELECT 'Psychology', 'PSYCH101', 'Introduction to Psychology', 5, 2, 9, 3.0, 1 WHERE NOT EXISTS (SELECT 1 FROM edu_course WHERE course_code = 'PSYCH101');
INSERT INTO edu_course (course_name, course_code, description, teacher_id, grade_level, subject_id, credit, status) SELECT 'Art', 'ART101', 'Introduction to Visual Arts', 6, 1, 10, 2.5, 1 WHERE NOT EXISTS (SELECT 1 FROM edu_course WHERE course_code = 'ART101');
INSERT INTO edu_course (course_name, course_code, description, teacher_id, grade_level, subject_id, credit, status) SELECT 'Music Theory', 'MUSIC101', 'Fundamentals of Music Theory', 6, 1, 11, 2.0, 1 WHERE NOT EXISTS (SELECT 1 FROM edu_course WHERE course_code = 'MUSIC101');
INSERT INTO edu_course (course_name, course_code, description, teacher_id, grade_level, subject_id, credit, status) SELECT 'Programming Fundamentals', 'PROG101', 'Basic Programming Concepts', 2, 1, 3, 4.0, 1 WHERE NOT EXISTS (SELECT 1 FROM edu_course WHERE course_code = 'PROG101');

INSERT INTO edu_learning_record (student_id, course_id, lesson_id, study_duration, progress, is_completed, last_study_time) SELECT 3, 1, 1, 45, 75, FALSE, TIMESTAMP '2026-03-11 17:30:00' WHERE NOT EXISTS (SELECT 1 FROM edu_learning_record WHERE student_id = 3 AND course_id = 1);
INSERT INTO edu_learning_record (student_id, course_id, lesson_id, study_duration, progress, is_completed, last_study_time) SELECT 3, 2, 1, 30, 50, FALSE, TIMESTAMP '2026-03-11 16:00:00' WHERE NOT EXISTS (SELECT 1 FROM edu_learning_record WHERE student_id = 3 AND course_id = 2);
INSERT INTO edu_learning_record (student_id, course_id, lesson_id, study_duration, progress, is_completed, last_study_time) SELECT 4, 1, 1, 60, 100, TRUE, TIMESTAMP '2026-03-11 18:00:00' WHERE NOT EXISTS (SELECT 1 FROM edu_learning_record WHERE student_id = 4 AND course_id = 1);