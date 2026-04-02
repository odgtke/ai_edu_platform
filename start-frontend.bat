@echo off
chcp 65001 >nul
echo ==========================================
echo      智慧教育平台 - 前端服务启动脚本
echo ==========================================
echo.

set FRONTEND_DIR=%~dp0frontend

cd /d %FRONTEND_DIR%

:: 检查 node_modules 是否存在
if not exist "node_modules" (
    echo [警告] 依赖未安装，正在安装...
    echo.
    call npm install
    if errorlevel 1 (
        echo [错误] 依赖安装失败！
        pause
        exit /b 1
    )
    echo [成功] 依赖安装完成！
    echo.
)

echo [信息] 正在启动前端开发服务器...
echo [信息] 服务地址: http://localhost:3000
echo [信息] 按 Ctrl+C 停止服务
echo.

npm run dev

pause
