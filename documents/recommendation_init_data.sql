-- 个性化推荐系统初始化数据
-- 执行前请确保已创建表结构（recommendation_tables.sql）

-- 1. 用户偏好数据
INSERT INTO user_preferences (user_id, preference_type, preference_value, preference_score) VALUES
(1, 'course_category', '编程开发', 0.9),
(1, 'course_category', '人工智能', 0.8),
(1, 'difficulty_level', '中级', 0.7),
(1, 'learning_style', '实践型', 0.85),
(2, 'course_category', '前端开发', 0.85),
(2, 'difficulty_level', '初级', 0.8),
(2, 'course_category', 'UI设计', 0.75),
(3, 'course_category', '数据分析', 0.9),
(3, 'course_category', '人工智能', 0.85),
(3, 'difficulty_level', '高级', 0.7);

-- 2. 用户行为数据
INSERT INTO user_behaviors (user_id, course_id, behavior_type, behavior_value, created_time) VALUES
-- 用户1的行为
(1, 1, 'study', 2.0, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(1, 2, 'complete', 5.0, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(1, 3, 'favorite', 3.0, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, 4, 'view', 1.0, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 5, 'browse', 0.5, DATE_SUB(NOW(), INTERVAL 1 DAY)),
-- 用户2的行为
(2, 1, 'view', 1.0, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2, 2, 'study', 2.0, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 6, 'favorite', 3.0, DATE_SUB(NOW(), INTERVAL 1 DAY)),
-- 用户3的行为
(3, 2, 'complete', 5.0, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(3, 3, 'study', 2.0, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(3, 5, 'view', 1.0, DATE_SUB(NOW(), INTERVAL 2 DAY)),
-- 模拟更多浏览行为
(1, 6, 'view', 1.0, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 3, 'view', 1.0, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(3, 1, 'view', 1.0, DATE_SUB(NOW(), INTERVAL 5 DAY));

-- 3. 课程相似度数据
INSERT INTO course_similarities (course_id_1, course_id_2, similarity_score, similarity_type) VALUES
-- Python和Java都是编程语言
(1, 2, 0.85, 'category'),
-- Python和数据分析相关
(1, 3, 0.72, 'content'),
-- Java和前端开发相关度较低
(2, 4, 0.45, 'category'),
-- 前端开发和UI设计相关
(4, 6, 0.68, 'category'),
-- 数据分析和机器学习相关
(3, 5, 0.80, 'content'),
-- Java和数据库设计相关
(2, 7, 0.65, 'content'),
-- Python和人工智能导论相关
(1, 9, 0.75, 'content'),
-- 机器学习和深度学习相关
(5, 10, 0.90, 'content');

-- 4. 用户评分数据
INSERT INTO user_ratings (user_id, course_id, rating, rating_type) VALUES
(1, 1, 4.5, 'manual'),
(1, 2, 4.0, 'manual'),
(2, 1, 5.0, 'manual'),
(2, 6, 4.5, 'manual'),
(3, 2, 4.0, 'manual'),
(3, 3, 5.0, 'manual');

COMMIT;
