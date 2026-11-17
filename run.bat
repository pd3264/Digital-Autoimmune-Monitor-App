@echo off
echo ===================================================
echo Launching Digital Autoimmune Monitor...
echo ===================================================

REM Check for java in PATH
where java >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    set JAVA_CMD=java
) else (
    REM Check standard JetBrains JBR location
    if exist "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\jbr\bin\java.exe" (
        set "JAVA_CMD=C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\jbr\bin\java.exe"
    ) else if exist "%JAVA_HOME%\bin\java.exe" (
        set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
    ) else (
        echo [ERROR] java launcher not found in PATH or standard JDK directories.
        echo Please ensure Java 17+ JRE/JDK is installed.
        pause
        exit /b 1
    )
)

if not exist bin\digitalautomonitor\Main.class (
    echo [INFO] Binaries not found. Invoking build.bat first...
    call build.bat
)

echo Starting Digital Autoimmune Monitor GUI...
"%JAVA_CMD%" -cp bin digitalautomonitor.Main
