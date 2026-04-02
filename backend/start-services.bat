@echo off
chcp 65001 >nul
echo ==========================================
echo Starting Edu Platform Microservices
echo ==========================================

set BASE_DIR=%~dp0

:: Start Gateway
echo [1/4] Starting API Gateway...
start "API Gateway" cmd /c "cd /d %BASE_DIR%\edu-gateway && java -jar target\edu-gateway-1.0.0.jar"
timeout /t 5 /nobreak >nul

:: Start User Service
echo [2/4] Starting User Service...
start "User Service" cmd /c "cd /d %BASE_DIR%\edu-user-service && java -jar target\edu-user-service-1.0.0.jar"
timeout /t 3 /nobreak >nul

:: Start Course Service
echo [3/4] Starting Course Service...
start "Course Service" cmd /c "cd /d %BASE_DIR%\edu-course-service && java -jar target\edu-course-service-1.0.0.jar"
timeout /t 3 /nobreak >nul

:: Start Learning Service
echo [4/4] Starting Learning Service...
start "Learning Service" cmd /c "cd /d %BASE_DIR%\edu-learning-service && java -jar target\edu-learning-service-1.0.0.jar"

echo.
echo ==========================================
echo All services started!
echo ==========================================
echo.
echo Service URLs:
echo   - Gateway: http://localhost:8080
echo   - User Service: http://localhost:8082
echo   - Course Service: http://localhost:8083
echo   - Learning Service: http://localhost:8084
echo   - Agent Service: http://localhost:8085
echo.
pause
