@echo off
echo ========================================
echo   Student Management System - Microservices
echo   Starting all services...
echo ========================================

echo.
echo [1/7] Starting Gateway Service (Port: 8080)...
start "Gateway Service" cmd /c "cd /d %~dp0gateway-service && mvn spring-boot:run"
timeout /t 10 /nobreak > nul

echo [2/7] Starting Auth Service (Port: 8081)...
start "Auth Service" cmd /c "cd /d %~dp0auth-service && mvn spring-boot:run"
timeout /t 5 /nobreak > nul

echo [3/7] Starting Base Service (Port: 8086)...
start "Base Service" cmd /c "cd /d %~dp0base-service && mvn spring-boot:run"
timeout /t 5 /nobreak > nul

echo [4/7] Starting Student Service (Port: 8082)...
start "Student Service" cmd /c "cd /d %~dp0student-service && mvn spring-boot:run"
timeout /t 5 /nobreak > nul

echo [5/7] Starting Teacher Service (Port: 8083)...
start "Teacher Service" cmd /c "cd /d %~dp0teacher-service && mvn spring-boot:run"
timeout /t 5 /nobreak > nul

echo [6/7] Starting Course Service (Port: 8084)...
start "Course Service" cmd /c "cd /d %~dp0course-service && mvn spring-boot:run"
timeout /t 5 /nobreak > nul

echo [7/7] Starting Selection Service (Port: 8085)...
start "Selection Service" cmd /c "cd /d %~dp0selection-service && mvn spring-boot:run"

echo.
echo ========================================
echo   All services are starting...
echo   Please wait for services to be ready.
echo.
echo   Gateway URL: http://localhost:8080
echo   Nacos Console: http://localhost:8848/nacos
echo ========================================
pause
