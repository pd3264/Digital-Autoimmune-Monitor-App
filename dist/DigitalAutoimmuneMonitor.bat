@echo off
setlocal enabledelayedexpansion
title Digital Autoimmune Monitor
set JAVA_CMD=javaw
if exist "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\jbr\bin\javaw.exe" (
    set JAVA_CMD="C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\jbr\bin\javaw.exe"
) else if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\javaw.exe" set JAVA_CMD="%JAVA_HOME%\bin\javaw.exe"
)
start "" %JAVA_CMD% -jar "%~dp0DigitalAutoimmuneMonitor.jar" %*
