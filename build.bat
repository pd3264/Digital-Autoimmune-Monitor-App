@echo off
echo ===================================================
echo Compiling Digital Autoimmune Monitor...
echo ===================================================

REM Check for javac in PATH
where javac >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    set JAVAC_CMD=javac
) else (
    REM Check standard JetBrains JBR location
    if exist "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\jbr\bin\javac.exe" (
        set "JAVAC_CMD=C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\jbr\bin\javac.exe"
    ) else if exist "%JAVA_HOME%\bin\javac.exe" (
        set "JAVAC_CMD=%JAVA_HOME%\bin\javac.exe"
    ) else (
        echo [ERROR] javac compiler not found in PATH or standard JDK directories.
        echo Please ensure Java 17+ JDK is installed and added to PATH.
        pause
        exit /b 1
    )
)

if not exist bin mkdir bin

echo Using compiler: %JAVAC_CMD%
dir /s /b src\*.java > sources.txt
"%JAVAC_CMD%" -d bin -sourcepath src @sources.txt
del sources.txt

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Compilation completed successfully into bin/ directory.
) else (
    echo [ERROR] Compilation failed.
)
pause
