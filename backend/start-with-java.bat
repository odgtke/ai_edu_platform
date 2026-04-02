@echo off
chcp 65001 >nul
echo ==========================================
echo Starting Edu Platform Microservices
echo ==========================================

:: 设置 JAVA_HOME
set JAVA_HOME=D:\Java\jdk1.8.0
set PATH=%JAVA_HOME%\bin;%PATH%

echo JAVA_HOME=%JAVA_HOME%
java -version
echo.

set BASE_DIR=%~dp0

:: Start Gateway
echo [1/5] Starting API Gateway...
start "API Gateway" cmd /c "cd /d %BASE_DIR%\edu-gateway && %JAVA_HOME%\bin\java -jar target\edu-gateway-1.0.0.jar"
timeout /t 8 /nobreak >nul

:: Start User Service
echo [2/5] Starting User Service...
start "User Service" cmd /c "cd /d %BASE_DIR%\edu-user-service && %JAVA_HOME%\bin\java -jar target\edu-user-service-1.0.0.jar"
timeout /t 5 /nobreak >nul

:: Start Course Service
echo [3/5] Starting Course Service...
start "Course Service" cmd /c "cd /d %BASE_DIR%\edu-course-service && %JAVA_HOME%\bin\java -jar target\edu-course-service-1.0.0.jar"
timeout /t 5 /nobreak >nul

:: Start Learning Service
echo [4/5] Starting Learning Service...
start "Learning Service" cmd /c "cd /d %BASE_DIR%\edu-learning-service && %JAVA_HOME%\bin\java -jar target\edu-learning-service-1.0.0.jar"
timeout /t 5 /nobreak >nul

:: Start Agent Service
echo [5/5] Starting Agent Service...
start "Agent Service" cmd /c "cd /d %BASE_DIR%\edu-agent-service && %JAVA_HOME%\bin\java -jar target\edu-agent-service-1.0.0.jar"

echo.
echo ==========================================
echo All services started!
echo ==========================================
echo.
echo Service URLs:
echo   - Gateway: http://localhost:8080
echo   - User Service: http://localhost:8081
echo   - Course Service: http://localhost:8082
echo   - Learning Service: http://localhost:8083
echo   - Agent Service: http://localhost:8084
echo.
pause
