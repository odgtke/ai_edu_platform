@echo off
chcp 65001 >nul
echo ==========================================
echo      智慧教育平台 - 后端服务启动脚本
echo ==========================================
echo.

set BACKEND_DIR=%~dp0backend
set JAR_FILE=%BACKEND_DIR%\target\ai-edu-platform-1.0.0.jar

cd /d %BACKEND_DIR%

:: 检查 JAR 文件是否存在
if not exist "%JAR_FILE%" (
    echo [警告] JAR 文件不存在，正在编译...
    echo.
    call mvn clean package -DskipTests -q
    if errorlevel 1 (
        echo [错误] 编译失败！
        pause
        exit /b 1
    )
    echo [成功] 编译完成！
    echo.
)

echo [信息] 正在启动后端服务...
echo [信息] 服务地址: http://localhost:8081
echo [信息] 按 Ctrl+C 停止服务
echo.

java -jar "%JAR_FILE%"

pause
