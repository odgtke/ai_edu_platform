@echo off
echo ================================
echo 智慧教育平台 API 测试脚本
echo ================================

echo 测试1: 获取所有用户
curl -X GET "http://localhost:8080/api/users" -H "Content-Type: application/json"
echo.
echo.

echo 测试2: 创建新用户
curl -X POST "http://localhost:8080/api/users" ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"testuser001\",\"password\":\"password123\",\"realName\":\"测试用户\",\"phone\":\"13800138001\",\"email\":\"test@example.com\",\"userType\":1,\"status\":1}"
echo.
echo.

echo 测试3: 获取所有课程
curl -X GET "http://localhost:8080/api/courses" -H "Content-Type: application/json"
echo.
echo.

echo 测试4: 创建新课程
curl -X POST "http://localhost:8080/api/courses" ^
  -H "Content-Type: application/json" ^
  -d "{\"courseName\":\"高等数学\",\"courseCode\":\"MATH101\",\"description\":\"大学一年级高等数学课程\",\"teacherId\":2,\"gradeLevel\":1,\"subjectId\":1,\"credit\":4.0,\"status\":1}"
echo.
echo.

echo 测试5: 获取学习记录
curl -X GET "http://localhost:8080/api/learning/records/student/1" -H "Content-Type: application/json"
echo.
echo.

echo 测试6: 创建学习记录
curl -X POST "http://localhost:8080/api/learning/record" ^
  -H "Content-Type: application/json" ^
  -d "{\"studentId\":1,\"courseId\":1,\"lessonId\":1,\"studyDuration\":45,\"progress\":75,\"isCompleted\":false,\"lastStudyTime\":\"2026-03-11T17:56:00\"}"
echo.
echo.

echo ================================
echo API测试完成！
echo 访问 http://localhost:8080/api/h2-console 查看数据库
echo JDBC URL: jdbc:h2:mem:testdb
echo 用户名: sa
echo 密码: (留空)
echo ================================
pause