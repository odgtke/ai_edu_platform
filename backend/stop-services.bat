@echo off
echo Stopping all Edu Platform services...

taskkill /FI "WINDOWTITLE eq API Gateway*" /T /F 2>nul
taskkill /FI "WINDOWTITLE eq User Service*" /T /F 2>nul
taskkill /FI "WINDOWTITLE eq Course Service*" /T /F 2>nul
taskkill /FI "WINDOWTITLE eq Learning Service*" /T /F 2>nul
taskkill /FI "WINDOWTITLE eq Agent Service*" /T /F 2>nul

echo All services stopped.
pause
