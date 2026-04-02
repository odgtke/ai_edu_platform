-- 能力评估模块数据库表结构（简化版，不依赖其他表）

-- 评估表
CREATE TABLE IF NOT EXISTS edu_assessment (
    assessment_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评估ID',
    assessment_name VARCHAR(100) NOT NULL COMMENT '评估名称',
    assessment_code VARCHAR(50) UNIQUE NOT NULL COMMENT '评估编码',
    subject_id BIGINT COMMENT '学科ID',
    grade_level TINYINT COMMENT '适用年级',
    assessment_type TINYINT NOT NULL COMMENT '评估类型：1-入学测试 2-阶段性测试 3-期末测试 4-专项能力测试',
    description TEXT COMMENT '评估描述',
    total_score DECIMAL(6,2) DEFAULT 100.00 COMMENT '总分',
    passing_score DECIMAL(6,2) DEFAULT 60.00 COMMENT '及格分数',
    duration INT COMMENT '考试时长（分钟）',
    question_count INT DEFAULT 0 COMMENT '题目数量',
    status TINYINT DEFAULT 0 COMMENT '状态：0-草稿 1-发布 2-进行中 3-已结束',
    creator_id BIGINT COMMENT '创建者ID',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    allow_repeat TINYINT DEFAULT 0 COMMENT '是否允许重复参加：0-否 1-是',
    repeat_interval INT DEFAULT 24 COMMENT '重考间隔（小时）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_subject_id (subject_id),
    INDEX idx_grade_level (grade_level),
    INDEX idx_assessment_type (assessment_type),
    INDEX idx_status (status),
    INDEX idx_creator_id (creator_id),
    INDEX idx_start_time (start_time),
    INDEX idx_end_time (end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评估表';

-- 评估题目表
CREATE TABLE IF NOT EXISTS edu_assessment_question (
    question_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '题目ID',
    assessment_id BIGINT NOT NULL COMMENT '评估ID',
    question_order INT NOT NULL COMMENT '题目序号',
    question_type TINYINT NOT NULL COMMENT '题目类型：1-单选题 2-多选题 3-判断题 4-填空题 5-简答题',
    content TEXT NOT NULL COMMENT '题目内容',
    options JSON COMMENT '题目选项（JSON格式存储）',
    correct_answer TEXT COMMENT '正确答案',
    score DECIMAL(5,2) DEFAULT 2.00 COMMENT '题目分数',
    difficulty TINYINT DEFAULT 2 COMMENT '难度等级：1-简单 2-中等 3-困难',
    tags VARCHAR(200) COMMENT '知识点标签',
    analysis TEXT COMMENT '解析说明',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (assessment_id) REFERENCES edu_assessment(assessment_id) ON DELETE CASCADE,
    INDEX idx_assessment_id (assessment_id),
    INDEX idx_question_order (question_order),
    INDEX idx_question_type (question_type),
    INDEX idx_difficulty (difficulty)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评估题目表';

-- 用户评估结果表
CREATE TABLE IF NOT EXISTS edu_user_assessment (
    result_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '结果ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    assessment_id BIGINT NOT NULL COMMENT '评估ID',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    time_spent INT COMMENT '用时（秒）',
    total_score DECIMAL(6,2) DEFAULT 0.00 COMMENT '总得分',
    score_percentage DECIMAL(5,2) DEFAULT 0.00 COMMENT '得分百分比',
    is_passed TINYINT DEFAULT 0 COMMENT '是否及格：0-不及格 1-及格',
    answer_details JSON COMMENT '答题详情（JSON格式存储）',
    status TINYINT DEFAULT 1 COMMENT '评估状态：1-进行中 2-已完成 3-已批改',
    teacher_comment TEXT COMMENT '教师评语',
    capability_report JSON COMMENT '能力分析报告（JSON格式）',
    recommended_path TEXT COMMENT '推荐学习路径',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_assessment_id (assessment_id),
    INDEX idx_status (status),
    INDEX idx_is_passed (is_passed),
    INDEX idx_total_score (total_score),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户评估结果表';

-- 评估统计表
CREATE TABLE IF NOT EXISTS edu_assessment_statistics (
    stat_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '统计ID',
    assessment_id BIGINT NOT NULL COMMENT '评估ID',
    total_participants INT DEFAULT 0 COMMENT '总参与人数',
    average_score DECIMAL(6,2) DEFAULT 0.00 COMMENT '平均分',
    pass_rate DECIMAL(5,2) DEFAULT 0.00 COMMENT '及格率',
    highest_score DECIMAL(6,2) DEFAULT 0.00 COMMENT '最高分',
    lowest_score DECIMAL(6,2) DEFAULT 0.00 COMMENT '最低分',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_assessment_id (assessment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评估统计表';

-- 初始化测试数据
INSERT INTO edu_assessment (assessment_name, assessment_code, subject_id, grade_level, assessment_type, description, total_score, passing_score, duration, question_count, status, creator_id, start_time, end_time) VALUES
('数学能力测试-初级', 'MATH-001', 1, 1, 1, '小学一年级数学基础能力测试', 100.00, 60.00, 60, 5, 1, 1, '2026-01-01 00:00:00', '2026-12-31 23:59:59'),
('语文阅读理解测试', 'CHINESE-001', 2, 2, 2, '小学二年级语文阅读理解能力测试', 100.00, 70.00, 45, 5, 1, 1, '2026-01-01 00:00:00', '2026-12-31 23:59:59'),
('英语词汇掌握测试', 'ENGLISH-001', 3, 3, 4, '小学三年级英语词汇专项测试', 100.00, 65.00, 30, 5, 1, 1, '2026-01-01 00:00:00', '2026-12-31 23:59:59');

-- 插入测试题目
INSERT INTO edu_assessment_question (assessment_id, question_order, question_type, content, options, correct_answer, score, difficulty, tags, analysis) VALUES
(1, 1, 1, '2 + 3 = ?', '{"A": "4", "B": "5", "C": "6", "D": "7"}', 'B', 5.00, 1, '基础运算', '考查基本加法运算能力'),
(1, 2, 1, '一年有几个季节？', '{"A": "2个", "B": "3个", "C": "4个", "D": "5个"}', 'C', 5.00, 1, '常识认知', '考查基本常识认知'),
(1, 3, 3, '太阳从西边升起吗？', '{"A": "是", "B": "否"}', 'B', 5.00, 1, '逻辑思维', '考查基本逻辑判断能力'),
(1, 4, 2, '下面哪些是水果？（多选）', '{"A": "苹果", "B": "胡萝卜", "C": "香蕉", "D": "西红柿"}', 'A,C', 10.00, 2, '分类认知', '考查事物分类能力'),
(1, 5, 4, '请写出数字"七"', NULL, '七', 5.00, 1, '汉字书写', '考查基础汉字书写能力');
