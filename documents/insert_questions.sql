INSERT INTO edu_assessment_question (assessment_id, question_order, question_type, content, options, correct_answer, score, difficulty, tags, analysis) VALUES 
(1, 1, 1, '2 + 3 = ?', '{"A": "4", "B": "5", "C": "6", "D": "7"}', 'B', 5.00, 1, 'Math', 'Addition'),
(1, 2, 1, 'How many seasons?', '{"A": "2", "B": "3", "C": "4", "D": "5"}', 'C', 5.00, 1, 'Common', 'Basic'),
(1, 3, 3, 'Sun from west?', '{"A": "Yes", "B": "No"}', 'B', 5.00, 1, 'Logic', 'Basic'),
(1, 4, 2, 'Select fruits', '{"A": "Apple", "B": "Carrot", "C": "Banana", "D": "Tomato"}', 'A,C', 10.00, 2, 'Classify', 'Sort'),
(1, 5, 4, 'Write seven', NULL, 'Seven', 5.00, 1, 'Write', 'Basic');
