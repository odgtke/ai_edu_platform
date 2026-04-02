@echo off
chcp 65001 >nul
echo ==========================================
echo      智慧教育平台 - 一键启动脚本
echo ==========================================
echo.

set ROOT_DIR=%~dp0
set BACKEND_DIR=%ROOT_DIR%backend
set FRONTEND_DIR=%ROOT_DIR%frontend
set JAR_FILE=%BACKEND_DIR%\target\ai-edu-platform-1.0.0.jar

echo [信息] 正在检查服务状态...
echo.

:: 检查后端端口
netstat -ano | findstr :8081 >nul
if %errorlevel% equ 0 (
    echo [警告] 后端服务已在运行 (端口: 8081)
) else (
    echo [信息] 后端服务未运行，准备启动...
    
    :: 检查 JAR 文件
    if not exist "%JAR_FILE%" (
        echo [警告] JAR 文件不存在，正在编译后端...
        cd /d "%BACKEND_DIR%"
        call mvn clean package -DskipTests -q
        if errorlevel 1 (
            echo [错误] 后端编译失败！
            pause
            exit /b 1
        )
        echo [成功] 后端编译完成！
    )
    
    :: 启动后端（后台运行）
    echo [信息] 正在启动后端服务...
    start "后端服务" cmd /c "cd /d %BACKEND_DIR% && java -jar target/ai-edu-platform-1.0.0.jar"
    
    :: 等待后端启动
    echo [信息] 等待后端服务启动...
    timeout /t 5 /nobreak >nul
    
    :: 检查后端是否启动成功
    netstat -ano | findstr :8081 >nul
    if %errorlevel% equ 0 (
        echo [成功] 后端服务启动成功！
    ) else (
        echo [警告] 后端服务可能仍在启动中...
    )
)

echo.

:: 检查前端端口
netstat -ano | findstr :3000 >nul
if %errorlevel% equ 0 (
    echo [警告] 前端服务已在运行 (端口: 3000)
) else (
    echo [信息] 前端服务未运行，准备启动...
    
    :: 检查依赖
    if not exist "%FRONTEND_DIR%\node_modules" (
        echo [警告] 前端依赖未安装，正在安装...
        cd /d "%FRONTEND_DIR%"
        call npm install
        if errorlevel 1 (
            echo [错误] 前端依赖安装失败！
            pause
            exit /b 1
        )
        echo [成功] 前端依赖安装完成！
    )
    
    :: 启动前端（后台运行）
    echo [信息] 正在启动前端服务...
    start "前端服务" cmd /c "cd /d %FRONTEND_DIR% && npm run dev"
)

echo.
echo ==========================================
echo [成功] 启动命令已执行！
echo.
echo 服务地址:
echo   - 前端: http://localhost:3000
echo   - 后端: http://localhost:8081
echo.
echo 注意: 关闭窗口不会停止服务
echo       请在任务管理器中结束 Java/Node 进程
echo ==========================================
echo.

pause
