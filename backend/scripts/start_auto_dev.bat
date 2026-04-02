@echo off
REM 智慧教育平台自动化开发启动脚本
REM 作者：AI开发助手
REM 日期：2026年3月

echo ========================================
echo   智慧教育平台自动化开发工具
echo ========================================
echo.

REM 检查Python环境
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误：未找到Python环境，请先安装Python 3.7+
    pause
    exit /b 1
)

REM 检查项目目录
if not exist "scripts\auto_dev\auto_dev.py" (
    echo 错误：未找到自动化开发脚本
    pause
    exit /b 1
)

echo 请选择操作模式：
echo 1. 完整自动化开发（推荐）
echo 2. 开发指定模块
echo 3. 仅运行测试
echo 4. 仅构建项目
echo 5. 查看帮助信息
echo.

set /p choice=请输入选项编号（1-5）: 

if "%choice%"=="1" (
    echo 开始完整自动化开发流程...
    python scripts\auto_dev\auto_dev.py
) else if "%choice%"=="2" (
    set /p module=请输入要开发的模块名称（user/course/assignment/message/resource）: 
    echo 开始开发 %module% 模块...
    python scripts\auto_dev\auto_dev.py --module %module%
) else if "%choice%"=="3" (
    set /p module=请输入要测试的模块名称（可为空表示全部测试）: 
    if "%module%"=="" (
        python scripts\auto_dev\auto_dev.py --test-only
    ) else (
        python scripts\auto_dev\auto_dev.py --test-only --module %module%
    )
) else if "%choice%"=="4" (
    echo 开始构建项目...
    python scripts\auto_dev\auto_dev.py --build-only
) else if "%choice%"=="5" (
    python scripts\auto_dev\auto_dev.py --help
) else (
    echo 无效的选择
)

echo.
echo 操作完成！
pause