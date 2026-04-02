@echo off
chcp 65001 >nul
echo ==========================================
echo      智慧教育平台 - 停止服务脚本
echo ==========================================
echo.

echo [信息] 正在查找并停止服务...
echo.

:: 停止 Java 进程（后端）
echo [信息] 停止后端服务 (Java)...
taskkill /F /IM java.exe 2>nul
if %errorlevel% equ 0 (
    echo [成功] 后端服务已停止
) else (
    echo [信息] 未找到运行中的后端服务
)

echo.

:: 停止 Node 进程（前端）
echo [信息] 停止前端服务 (Node)...
taskkill /F /IM node.exe 2>nul
if %errorlevel% equ 0 (
    echo [成功] 前端服务已停止
) else (
    echo [信息] 未找到运行中的前端服务
)

echo.
echo ==========================================
echo [完成] 所有服务已停止
echo ==========================================
echo.

pause
