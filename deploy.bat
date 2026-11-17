@echo off
setlocal enabledelayedexpansion

echo ===================================================
echo Deploying Digital Autoimmune Monitor...
echo ===================================================

call build.bat
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Build failed. Aborting deployment.
    exit /b %ERRORLEVEL%
)

set JAVA_CMD=java
if exist "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\jbr\bin\java.exe" (
    set JAVA_CMD="C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\jbr\bin\java.exe"
) else if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" set JAVA_CMD="%JAVA_HOME%\bin\java.exe"
)

echo Packaging into standalone distribution...
%JAVA_CMD% -cp bin digitalautomonitor.DeployPackager

if %ERRORLEVEL% EQU 0 (
    echo.
    echo [DEPLOY SUCCESS] Executable distribution ready in dist\
    echo Run: dist\DigitalAutoimmuneMonitor.bat
) else (
    echo.
    echo [DEPLOY ERROR] Deployment packaging failed.
)
