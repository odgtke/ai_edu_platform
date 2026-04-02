-- 创建评估表（与 Assessment 实体类匹配）
CREATE TABLE IF NOT EXISTS edu_assessment (
    assessment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    assessment_name VARCHAR(100) NOT NULL,
    description TEXT,
    course_id BIGINT,
    total_score INT DEFAULT 100,
    duration INT DEFAULT 30,
    status INT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试数据
INSERT INTO edu_assessment (assessment_name, description, course_id, total_score, duration, status) VALUES
('Python编程基础测试', '测试Python基础语法和编程能力', 1, 100, 60, 1),
('Java面向对象测试', '测试Java面向对象编程概念', 2, 100, 45, 1),
('前端开发技能评估', '评估HTML、CSS、JavaScript基础', 3, 100, 90, 1),
('数据分析入门测试', '测试数据分析基础知识和工具使用', 4, 100, 60, 1),
('机器学习基础评估', '评估机器学习基本概念理解', 5, 100, 120, 1);
