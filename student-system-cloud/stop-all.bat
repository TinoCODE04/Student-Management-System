@echo off
echo ========================================
echo   Stopping all microservices...
echo ========================================

echo Stopping Gateway Service...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080 ^| findstr LISTENING') do taskkill /F /PID %%a 2>nul

echo Stopping Auth Service...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8081 ^| findstr LISTENING') do taskkill /F /PID %%a 2>nul

echo Stopping Student Service...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8082 ^| findstr LISTENING') do taskkill /F /PID %%a 2>nul

echo Stopping Teacher Service...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8083 ^| findstr LISTENING') do taskkill /F /PID %%a 2>nul

echo Stopping Course Service...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8084 ^| findstr LISTENING') do taskkill /F /PID %%a 2>nul

echo Stopping Selection Service...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8085 ^| findstr LISTENING') do taskkill /F /PID %%a 2>nul

echo Stopping Base Service...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8086 ^| findstr LISTENING') do taskkill /F /PID %%a 2>nul

echo.
echo ========================================
echo   All services stopped.
echo ========================================
pause
