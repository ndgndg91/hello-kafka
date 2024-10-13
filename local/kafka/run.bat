@echo off
rem Check if Docker is running
docker info >nul 2>&1
if ERRORLEVEL 1 (
    echo Docker is not running. Please start Docker Desktop and try again.
    pause
    exit /b 1
)

rem Navigate to the directory containing the docker-compose.yml file
cd /d %~dp0

rem Run docker-compose up
docker-compose up -d

if ERRORLEVEL 1 (
    echo Failed to start Docker Compose.
    pause
    exit /b 1
)

echo Docker Compose started successfully.
pause