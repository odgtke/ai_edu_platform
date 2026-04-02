-- Assessment Module Database Tables (English Version)

-- Assessment Table
CREATE TABLE IF NOT EXISTS edu_assessment (
    assessment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    assessment_name VARCHAR(100) NOT NULL,
    assessment_code VARCHAR(50) UNIQUE NOT NULL,
    subject_id BIGINT,
    grade_level TINYINT,
    assessment_type TINYINT NOT NULL COMMENT '1-Entrance 2-Stage 3-Final 4-Special',
    description TEXT,
    total_score DECIMAL(6,2) DEFAULT 100.00,
    passing_score DECIMAL(6,2) DEFAULT 60.00,
    duration INT,
    question_count INT DEFAULT 0,
    status TINYINT DEFAULT 0 COMMENT '0-Draft 1-Published 2-Active 3-Ended',
    creator_id BIGINT,
    start_time DATETIME,
    end_time DATETIME,
    allow_repeat TINYINT DEFAULT 0 COMMENT '0-No 1-Yes',
    repeat_interval INT DEFAULT 24,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_subject_id (subject_id),
    INDEX idx_grade_level (grade_level),
    INDEX idx_assessment_type (assessment_type),
    INDEX idx_status (status),
    INDEX idx_creator_id (creator_id),
    INDEX idx_start_time (start_time),
    INDEX idx_end_time (end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Assessment Question Table
CREATE TABLE IF NOT EXISTS edu_assessment_question (
    question_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    assessment_id BIGINT NOT NULL,
    question_order INT NOT NULL,
    question_type TINYINT NOT NULL COMMENT '1-Single 2-Multi 3-Judge 4-Fill 5-Essay',
    content TEXT NOT NULL,
    options JSON,
    correct_answer TEXT,
    score DECIMAL(5,2) DEFAULT 2.00,
    difficulty TINYINT DEFAULT 2 COMMENT '1-Easy 2-Medium 3-Hard',
    tags VARCHAR(200),
    analysis TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (assessment_id) REFERENCES edu_assessment(assessment_id) ON DELETE CASCADE,
    INDEX idx_assessment_id (assessment_id),
    INDEX idx_question_order (question_order),
    INDEX idx_question_type (question_type),
    INDEX idx_difficulty (difficulty)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- User Assessment Result Table
CREATE TABLE IF NOT EXISTS edu_user_assessment (
    result_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    assessment_id BIGINT NOT NULL,
    start_time DATETIME,
    end_time DATETIME,
    time_spent INT,
    total_score DECIMAL(6,2) DEFAULT 0.00,
    score_percentage DECIMAL(5,2) DEFAULT 0.00,
    is_passed TINYINT DEFAULT 0 COMMENT '0-Fail 1-Pass',
    answer_details JSON,
    status TINYINT DEFAULT 1 COMMENT '1-InProgress 2-Completed 3-Graded',
    teacher_comment TEXT,
    capability_report JSON,
    recommended_path TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_assessment_id (assessment_id),
    INDEX idx_status (status),
    INDEX idx_is_passed (is_passed),
    INDEX idx_total_score (total_score),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Assessment Statistics Table
CREATE TABLE IF NOT EXISTS edu_assessment_statistics (
    stat_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    assessment_id BIGINT NOT NULL,
    total_participants INT DEFAULT 0,
    average_score DECIMAL(6,2) DEFAULT 0.00,
    pass_rate DECIMAL(5,2) DEFAULT 0.00,
    highest_score DECIMAL(6,2) DEFAULT 0.00,
    lowest_score DECIMAL(6,2) DEFAULT 0.00,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_assessment_id (assessment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert Test Data
INSERT INTO edu_assessment (assessment_name, assessment_code, subject_id, grade_level, assessment_type, description, total_score, passing_score, duration, question_count, status, creator_id, start_time, end_time) VALUES
('Math Test - Basic', 'MATH-001', 1, 1, 1, 'Primary School Math Basic Test', 100.00, 60.00, 60, 5, 1, 1, '2026-01-01 00:00:00', '2026-12-31 23:59:59'),
('Chinese Reading Test', 'CHINESE-001', 2, 2, 2, 'Chinese Reading Comprehension Test', 100.00, 70.00, 45, 5, 1, 1, '2026-01-01 00:00:00', '2026-12-31 23:59:59'),
('English Vocabulary Test', 'ENGLISH-001', 3, 3, 4, 'English Vocabulary Special Test', 100.00, 65.00, 30, 5, 1, 1, '2026-01-01 00:00:00', '2026-12-31 23:59:59');

-- Insert Test Questions
INSERT INTO edu_assessment_question (assessment_id, question_order, question_type, content, options, correct_answer, score, difficulty, tags, analysis) VALUES
(1, 1, 1, '2 + 3 = ?', '{"A": "4", "B": "5", "C": "6", "D": "7"}', 'B', 5.00, 1, 'Basic Math', 'Basic addition operation'),
(1, 2, 1, 'How many seasons in a year?', '{"A": "2", "B": "3", "C": "4", "D": "5"}', 'C', 5.00, 1, 'Common Sense', 'Basic common sense knowledge'),
(1, 3, 3, 'Does the sun rise from the west?', '{"A": "Yes", "B": "No"}', 'B', 5.00, 1, 'Logic', 'Basic logical judgment'),
(1, 4, 2, 'Which are fruits? (Multiple)', '{"A": "Apple", "B": "Carrot", "C": "Banana", "D": "Tomato"}', 'A,C', 10.00, 2, 'Classification', 'Object classification ability'),
(1, 5, 4, 'Write the number "seven" in Chinese', NULL, '七', 5.00, 1, 'Writing', 'Basic Chinese character writing');
