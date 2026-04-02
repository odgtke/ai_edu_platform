CREATE TABLE IF NOT EXISTS edu_assessment (
    assessment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    assessment_name VARCHAR(100) NOT NULL,
    assessment_code VARCHAR(50) UNIQUE NOT NULL,
    subject_id BIGINT,
    grade_level TINYINT,
    assessment_type TINYINT NOT NULL,
    description TEXT,
    total_score DECIMAL(6,2) DEFAULT 100.00,
    passing_score DECIMAL(6,2) DEFAULT 60.00,
    duration INT,
    question_count INT DEFAULT 0,
    status TINYINT DEFAULT 0,
    creator_id BIGINT,
    start_time DATETIME,
    end_time DATETIME,
    allow_repeat TINYINT DEFAULT 0,
    repeat_interval INT DEFAULT 24,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_subject_id (subject_id),
    INDEX idx_grade_level (grade_level),
    INDEX idx_assessment_type (assessment_type),
    INDEX idx_status (status),
    INDEX idx_start_time (start_time),
    INDEX idx_end_time (end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS edu_assessment_question (
    question_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    assessment_id BIGINT NOT NULL,
    question_order INT NOT NULL,
    question_type TINYINT NOT NULL,
    content TEXT NOT NULL,
    options JSON,
    correct_answer TEXT,
    score DECIMAL(5,2) DEFAULT 2.00,
    difficulty TINYINT DEFAULT 2,
    tags VARCHAR(200),
    analysis TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (assessment_id) REFERENCES edu_assessment(assessment_id) ON DELETE CASCADE,
    INDEX idx_assessment_id (assessment_id),
    INDEX idx_question_order (question_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS edu_user_assessment (
    result_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    assessment_id BIGINT NOT NULL,
    start_time DATETIME,
    end_time DATETIME,
    time_spent INT,
    total_score DECIMAL(6,2) DEFAULT 0.00,
    score_percentage DECIMAL(5,2) DEFAULT 0.00,
    is_passed TINYINT DEFAULT 0,
    answer_details JSON,
    status TINYINT DEFAULT 1,
    teacher_comment TEXT,
    capability_report JSON,
    recommended_path TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_assessment_id (assessment_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO edu_assessment (assessment_name, assessment_code, subject_id, grade_level, assessment_type, description, total_score, passing_score, duration, question_count, status, creator_id, start_time, end_time) VALUES
('Math Test Basic', 'MATH-001', 1, 1, 1, 'Math basic test', 100.00, 60.00, 60, 5, 1, 1, '2026-01-01 00:00:00', '2026-12-31 23:59:59'),
('Chinese Reading', 'CHINESE-001', 2, 2, 2, 'Reading test', 100.00, 70.00, 45, 5, 1, 1, '2026-01-01 00:00:00', '2026-12-31 23:59:59'),
('English Vocabulary', 'ENGLISH-001', 3, 3, 4, 'Vocabulary test', 100.00, 65.00, 30, 5, 1, 1, '2026-01-01 00:00:00', '2026-12-31 23:59:59');

INSERT INTO edu_assessment_question (assessment_id, question_order, question_type, content, options, correct_answer, score, difficulty, tags, analysis) VALUES
(1, 1, 1, '2 + 3 = ?', '{"A": "4", "B": "5", "C": "6", "D": "7"}', 'B', 5.00, 1, 'Math', 'Addition'),
(1, 2, 1, 'Seasons count?', '{"A": "2", "B": "3", "C": "4", "D": "5"}', 'C', 5.00, 1, 'Common', 'Basic'),
(1, 3, 3, 'Sun rises from west?', '{"A": "Yes", "B": "No"}', 'B', 5.00, 1, 'Logic', 'Basic'),
(1, 4, 2, 'Select fruits', '{"A": "Apple", "B": "Carrot", "C": "Banana", "D": "Tomato"}', 'A,C', 10.00, 2, 'Classify', 'Sort'),
(1, 5, 4, 'Write seven', NULL, 'Seven', 5.00, 1, 'Write', 'Basic');
