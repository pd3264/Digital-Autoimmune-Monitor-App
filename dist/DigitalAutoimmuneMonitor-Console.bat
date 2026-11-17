@echo off
title Digital Autoimmune Monitor (Console Logs)
set JAVA_CMD=java
if exist "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\jbr\bin\java.exe" (
    set JAVA_CMD="C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\jbr\bin\java.exe"
) else if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" set JAVA_CMD="%JAVA_HOME%\bin\java.exe"
)
%JAVA_CMD% -jar "%~dp0DigitalAutoimmuneMonitor.jar" %*
if %ERRORLEVEL% NEQ 0 pause
