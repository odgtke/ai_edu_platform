-- Drop old table if exists
DROP TABLE IF EXISTS edu_assessment;

-- Create assessment table
CREATE TABLE edu_assessment (
    assessment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    assessment_name VARCHAR(100) NOT NULL,
    description TEXT,
    course_id BIGINT,
    total_score INT DEFAULT 100,
    duration INT DEFAULT 30,
    status INT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert test data
INSERT INTO edu_assessment (assessment_name, description, course_id, total_score, duration, status) VALUES
('Python Programming Test', 'Test Python basic syntax and programming skills', 1, 100, 60, 1),
('Java OOP Assessment', 'Test Java object-oriented programming concepts', 2, 100, 45, 1),
('Frontend Development Skills', 'Evaluate HTML, CSS, JavaScript fundamentals', 3, 100, 90, 1),
('Data Analysis Basics', 'Test data analysis knowledge and tools', 4, 100, 60, 1),
('Machine Learning Fundamentals', 'Evaluate machine learning basic concepts', 5, 100, 120, 1);
