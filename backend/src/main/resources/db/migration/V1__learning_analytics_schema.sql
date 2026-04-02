-- 智能学习路径与知识掌握度功能数据库迁移脚本

-- 1. 修改学习路径表，添加动态调整字段
ALTER TABLE learning_paths 
ADD COLUMN IF NOT EXISTS adaptive_level VARCHAR(20) COMMENT '适应性等级: easy,normal,hard',
ADD COLUMN IF NOT EXISTS difficulty_factor DECIMAL(3,2) DEFAULT 1.0 COMMENT '难度系数 0.5-2.0',
ADD COLUMN IF NOT EXISTS adjustment_reason TEXT COMMENT '调整原因',
ADD COLUMN IF NOT EXISTS last_adjustment_time TIMESTAMP NULL COMMENT '上次调整时间',
ADD COLUMN IF NOT EXISTS performance_metrics JSON COMMENT '性能指标(JSON)',
ADD COLUMN IF NOT EXISTS alternative_paths JSON COMMENT '备选路径(JSON)',
ADD COLUMN IF NOT EXISTS current_stage_index INT DEFAULT 0 COMMENT '当前阶段索引',
ADD COLUMN IF NOT EXISTS stage_completion_status JSON COMMENT '各阶段完成状态(JSON)';

-- 2. 创建知识点表
CREATE TABLE IF NOT EXISTS knowledge_points (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    point_name VARCHAR(100) NOT NULL COMMENT '知识点名称',
    point_code VARCHAR(50) UNIQUE NOT NULL COMMENT '知识点编码',
    course_id BIGINT COMMENT '所属课程ID',
    category_name VARCHAR(50) COMMENT '分类名称',
    description TEXT COMMENT '知识点描述',
    difficulty_level INT DEFAULT 1 COMMENT '难度等级 1-5',
    prerequisite_points JSON COMMENT '前置知识点IDs (JSON数组)',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_course_id (course_id),
    INDEX idx_category (category_name),
    INDEX idx_difficulty (difficulty_level)
) COMMENT='知识点表';

-- 3. 创建用户知识点掌握情况表
CREATE TABLE IF NOT EXISTS user_knowledge_mastery (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    knowledge_point_id BIGINT NOT NULL COMMENT '知识点ID',
    mastery_level DECIMAL(5,2) DEFAULT 0.0 COMMENT '掌握程度 0.0-100.0',
    attempt_count INT DEFAULT 0 COMMENT '尝试次数',
    correct_count INT DEFAULT 0 COMMENT '正确次数',
    average_score DECIMAL(5,2) DEFAULT 0.0 COMMENT '平均得分',
    last_study_time TIMESTAMP NULL COMMENT '最后学习时间',
    study_duration INT DEFAULT 0 COMMENT '累计学习时长(分钟)',
    learning_methods JSON COMMENT '学习方式记录 (JSON)',
    weak_areas JSON COMMENT '薄弱环节分析 (JSON)',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_user_point (user_id, knowledge_point_id),
    INDEX idx_user_id (user_id),
    INDEX idx_knowledge_point_id (knowledge_point_id),
    INDEX idx_mastery_level (mastery_level)
) COMMENT='用户知识点掌握情况表';

-- 4. 创建学习路径调整记录表
CREATE TABLE IF NOT EXISTS learning_path_adjustments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    learning_path_id BIGINT NOT NULL COMMENT '学习路径ID',
    adjustment_type VARCHAR(20) NOT NULL COMMENT '调整类型: difficulty,content,sequence',
    old_value TEXT COMMENT '调整前的值',
    new_value TEXT COMMENT '调整后的值',
    reason TEXT COMMENT '调整原因',
    performance_data JSON COMMENT '当时的性能数据',
    adjusted_by VARCHAR(50) COMMENT '调整者(admin/system/user)',
    adjustment_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_user_path (user_id, learning_path_id),
    INDEX idx_adjustment_time (adjustment_time)
) COMMENT='学习路径调整记录表';

-- 5. 插入示例知识点数据
INSERT INTO knowledge_points (point_name, point_code, course_id, category_name, description, difficulty_level) 
SELECT '变量与数据类型', 'CS-BASIC-001', 3, '编程基础', '掌握编程语言中的基本变量声明和数据类型', 1 
WHERE NOT EXISTS (SELECT 1 FROM knowledge_points WHERE point_code = 'CS-BASIC-001');

INSERT INTO knowledge_points (point_name, point_code, course_id, category_name, description, difficulty_level) 
SELECT '条件语句', 'CS-BASIC-002', 3, '编程基础', '掌握if-else等条件控制语句', 1 
WHERE NOT EXISTS (SELECT 1 FROM knowledge_points WHERE point_code = 'CS-BASIC-002');

INSERT INTO knowledge_points (point_name, point_code, course_id, category_name, description, difficulty_level) 
SELECT '循环结构', 'CS-BASIC-003', 3, '编程基础', '掌握for、while等循环语句', 2 
WHERE NOT EXISTS (SELECT 1 FROM knowledge_points WHERE point_code = 'CS-BASIC-003');

INSERT INTO knowledge_points (point_name, point_code, course_id, category_name, description, difficulty_level) 
SELECT '函数定义与调用', 'CS-BASIC-004', 3, '编程基础', '掌握函数的定义、参数传递和返回值', 2 
WHERE NOT EXISTS (SELECT 1 FROM knowledge_points WHERE point_code = 'CS-BASIC-004');

INSERT INTO knowledge_points (point_name, point_code, course_id, category_name, description, difficulty_level) 
SELECT '数组操作', 'CS-DATA-001', 3, '数据结构', '掌握数组的创建、访问和基本操作', 2 
WHERE NOT EXISTS (SELECT 1 FROM knowledge_points WHERE point_code = 'CS-DATA-001');

INSERT INTO knowledge_points (point_name, point_code, course_id, category_name, description, difficulty_level) 
SELECT '链表基础', 'CS-DATA-002', 3, '数据结构', '掌握单向链表的基本操作', 3 
WHERE NOT EXISTS (SELECT 1 FROM knowledge_points WHERE point_code = 'CS-DATA-002');

INSERT INTO knowledge_points (point_name, point_code, course_id, category_name, description, difficulty_level) 
SELECT '栈和队列', 'CS-DATA-003', 3, '数据结构', '掌握栈和队列的概念及应用场景', 3 
WHERE NOT EXISTS (SELECT 1 FROM knowledge_points WHERE point_code = 'CS-DATA-003');

INSERT INTO knowledge_points (point_name, point_code, course_id, category_name, description, difficulty_level) 
SELECT 'HTML基础', 'FE-BASIC-001', 3, '前端开发', '掌握HTML标签和基本结构', 1 
WHERE NOT EXISTS (SELECT 1 FROM knowledge_points WHERE point_code = 'FE-BASIC-001');

INSERT INTO knowledge_points (point_name, point_code, course_id, category_name, description, difficulty_level) 
SELECT 'CSS选择器', 'FE-BASIC-002', 3, '前端开发', '掌握CSS各种选择器的使用', 2 
WHERE NOT EXISTS (SELECT 1 FROM knowledge_points WHERE point_code = 'FE-BASIC-002');

INSERT INTO knowledge_points (point_name, point_code, course_id, category_name, description, difficulty_level) 
SELECT 'JavaScript DOM操作', 'FE-JS-001', 3, '前端开发', '掌握JavaScript操作DOM的方法', 3 
WHERE NOT EXISTS (SELECT 1 FROM knowledge_points WHERE point_code = 'FE-JS-001');

INSERT INTO knowledge_points (point_name, point_code, course_id, category_name, description, difficulty_level) 
SELECT 'Vue组件基础', 'FE-VUE-001', 3, '前端开发', '掌握Vue组件的基本概念和使用', 3 
WHERE NOT EXISTS (SELECT 1 FROM knowledge_points WHERE point_code = 'FE-VUE-001');

-- 6. 插入示例用户知识点掌握数据
INSERT INTO user_knowledge_mastery (user_id, knowledge_point_id, mastery_level, attempt_count, correct_count, average_score, last_study_time, study_duration)
SELECT 3, 1, 95.0, 12, 11, 92.5, TIMESTAMP '2026-03-11 17:30:00', 120
WHERE NOT EXISTS (SELECT 1 FROM user_knowledge_mastery WHERE user_id = 3 AND knowledge_point_id = 1);

INSERT INTO user_knowledge_mastery (user_id, knowledge_point_id, mastery_level, attempt_count, correct_count, average_score, last_study_time, study_duration)
SELECT 3, 2, 88.0, 8, 7, 85.0, TIMESTAMP '2026-03-10 16:00:00', 80
WHERE NOT EXISTS (SELECT 1 FROM user_knowledge_mastery WHERE user_id = 3 AND knowledge_point_id = 2);

INSERT INTO user_knowledge_mastery (user_id, knowledge_point_id, mastery_level, attempt_count, correct_count, average_score, last_study_time, study_duration)
SELECT 3, 3, 75.0, 6, 4, 78.0, TIMESTAMP '2026-03-09 15:30:00', 65
WHERE NOT EXISTS (SELECT 1 FROM user_knowledge_mastery WHERE user_id = 3 AND knowledge_point_id = 3);

INSERT INTO user_knowledge_mastery (user_id, knowledge_point_id, mastery_level, attempt_count, correct_count, average_score, last_study_time, study_duration)
SELECT 3, 8, 92.0, 10, 9, 89.0, TIMESTAMP '2026-03-11 14:00:00', 95
WHERE NOT EXISTS (SELECT 1 FROM user_knowledge_mastery WHERE user_id = 3 AND knowledge_point_id = 8);

INSERT INTO user_knowledge_mastery (user_id, knowledge_point_id, mastery_level, attempt_count, correct_count, average_score, last_study_time, study_duration)
SELECT 4, 1, 85.0, 10, 8, 82.0, TIMESTAMP '2026-03-11 13:00:00', 100
WHERE NOT EXISTS (SELECT 1 FROM user_knowledge_mastery WHERE user_id = 4 AND knowledge_point_id = 1);

-- 7. 更新现有学习路径表结构（如果表存在）
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_name = 'learning_paths' AND column_name = 'difficulty_factor');
SET @sqlstmt := IF(@exist > 0, 'SELECT ''Column already exists''', 
                   'ALTER TABLE learning_paths ADD COLUMN difficulty_factor DECIMAL(3,2) DEFAULT 1.0');
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
