-- 个性推荐系统数据库表结构
-- 创建时间: 2026-03-19

-- 1. 用户偏好表
CREATE TABLE user_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    preference_type VARCHAR(50) NOT NULL COMMENT '偏好类型(course_category, difficulty_level, learning_style)',
    preference_value VARCHAR(100) NOT NULL COMMENT '偏好值',
    preference_score DECIMAL(3,2) DEFAULT 1.00 COMMENT '偏好权重(0.00-1.00)',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_preference (user_id, preference_type),
    INDEX idx_preference_type (preference_type, preference_value)
) COMMENT='用户偏好表';

-- 2. 用户行为记录表
CREATE TABLE user_behaviors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    course_id BIGINT COMMENT '课程ID',
    behavior_type VARCHAR(50) NOT NULL COMMENT '行为类型(view,browse,study,complete,favorite,share)',
    behavior_value DECIMAL(5,2) DEFAULT 1.00 COMMENT '行为权重值',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    user_agent TEXT COMMENT '用户代理',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_behavior (user_id, behavior_type),
    INDEX idx_course_behavior (course_id, behavior_type),
    INDEX idx_behavior_time (created_time)
) COMMENT='用户行为记录表';

-- 3. 推荐结果表
CREATE TABLE recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    recommendation_type VARCHAR(50) NOT NULL COMMENT '推荐类型(content_based,collaborative_filtering,trending,knowledge_based)',
    recommendation_score DECIMAL(5,2) NOT NULL COMMENT '推荐分数(0-100)',
    recommendation_reason TEXT COMMENT '推荐理由',
    is_clicked BOOLEAN DEFAULT FALSE COMMENT '是否点击',
    is_enrolled BOOLEAN DEFAULT FALSE COMMENT '是否报名',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    expired_time TIMESTAMP COMMENT '过期时间',
    INDEX idx_user_recommendation (user_id, created_time),
    INDEX idx_course_recommendation (course_id, recommendation_type),
    INDEX idx_recommendation_score (recommendation_score DESC)
) COMMENT='推荐结果表';

-- 4. 课程相似度表
CREATE TABLE course_similarities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    course_id_1 BIGINT NOT NULL COMMENT '课程ID1',
    course_id_2 BIGINT NOT NULL COMMENT '课程ID2',
    similarity_score DECIMAL(5,4) NOT NULL COMMENT '相似度分数(0-1)',
    similarity_type VARCHAR(50) COMMENT '相似度类型(category,content,instructor)',
    calculated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '计算时间',
    UNIQUE KEY uk_course_pair (course_id_1, course_id_2),
    INDEX idx_similarity_score (similarity_score DESC),
    INDEX idx_course_1 (course_id_1),
    INDEX idx_course_2 (course_id_2)
) COMMENT='课程相似度表';

-- 5. 用户评分表
CREATE TABLE user_ratings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    rating DECIMAL(2,1) NOT NULL COMMENT '评分(1-5)',
    rating_type VARCHAR(50) DEFAULT 'manual' COMMENT '评分类型(manual,auto)',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_course_rating (user_id, course_id),
    INDEX idx_course_rating (course_id, rating),
    INDEX idx_user_rating (user_id, rating)
) COMMENT='用户评分表';

-- 6. 学习路径表
CREATE TABLE learning_paths (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    path_name VARCHAR(100) NOT NULL COMMENT '路径名称',
    path_description TEXT COMMENT '路径描述',
    course_ids JSON NOT NULL COMMENT '课程ID数组',
    path_status VARCHAR(20) DEFAULT 'active' COMMENT '路径状态(active,completed,archived)',
    completion_rate DECIMAL(5,2) DEFAULT 0.00 COMMENT '完成率(0-100)',
    estimated_hours INT COMMENT '预估学习小时数',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_path (user_id, path_status),
    INDEX idx_path_status (path_status)
) COMMENT='学习路径表';

-- 插入示例数据
-- 示例用户偏好数据
INSERT INTO user_preferences (user_id, preference_type, preference_value, preference_score) VALUES
(1, 'course_category', '编程开发', 0.9),
(1, 'course_category', '人工智能', 0.8),
(1, 'difficulty_level', '中级', 0.7),
(1, 'learning_style', '实践型', 0.85);

-- 示例用户行为数据
INSERT INTO user_behaviors (user_id, course_id, behavior_type, behavior_value) VALUES
(1, 1, 'view', 1.0),
(1, 2, 'browse', 0.8),
(1, 1, 'study', 2.0),
(1, 3, 'favorite', 3.0);

-- 示例课程相似度数据
INSERT INTO course_similarities (course_id_1, course_id_2, similarity_score, similarity_type) VALUES
(1, 2, 0.85, 'category'),
(1, 3, 0.72, 'content'),
(2, 4, 0.68, 'category');

-- 示例用户评分数据
INSERT INTO user_ratings (user_id, course_id, rating) VALUES
(1, 1, 4.5),
(1, 2, 4.0),
(2, 1, 5.0);

COMMIT;