@echo off
chcp 65001 >nul
echo ==========================================
echo    智慧教育平台微服务 - 启动脚本
echo ==========================================
echo.
echo 请先确保 Nacos 服务已启动: http://localhost:8848/nacos
echo.

REM 设置Java环境
set JAVA_HOME=C:\Program Files\Java\jdk-11.0.2
set PATH=%JAVA_HOME%\bin;%PATH%

REM 启动顺序：Gateway -> 其他服务

echo [1/6] 正在启动 API Gateway...
start "API Gateway" cmd /k "cd /d %~dp0edu-gateway && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [2/6] 正在启动 用户服务...
start "User Service" cmd /k "cd /d %~dp0edu-user-service && mvn spring-boot:run"
timeout /t 3 /nobreak >nul

echo [3/6] 正在启动 课程服务...
start "Course Service" cmd /k "cd /d %~dp0edu-course-service && mvn spring-boot:run"
timeout /t 3 /nobreak >nul

echo [4/6] 正在启动 学习中心服务...
start "Learning Service" cmd /k "cd /d %~dp0edu-learning-service && mvn spring-boot:run"
timeout /t 3 /nobreak >nul

echo [5/6] 正在启动 智能体服务...
start "Agent Service" cmd /k "cd /d %~dp0edu-agent-service && mvn spring-boot:run"
timeout /t 3 /nobreak >nul

echo.
echo ==========================================
echo    所有服务启动命令已执行
echo ==========================================
echo.
echo 服务访问地址：
echo   Nacos 控制台:  http://localhost:8848/nacos (nacos/nacos)
echo   API 网关:      http://localhost:8080
echo   用户服务:      http://localhost:8081
echo   课程服务:      http://localhost:8082
echo   学习中心服务:  http://localhost:8083
echo   智能体服务:    http://localhost:8084
echo.
pause
