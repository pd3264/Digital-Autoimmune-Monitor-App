Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "Launching Digital Autoimmune Monitor..." -ForegroundColor Cyan
Write-Host "===================================================" -ForegroundColor Cyan

$java = "java"
if (-not (Get-Command "java" -ErrorAction SilentlyContinue)) {
    if (Test-Path "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\jbr\bin\java.exe") {
        $java = "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\jbr\bin\java.exe"
    } elseif ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
        $java = "$env:JAVA_HOME\bin\java.exe"
    } else {
        Write-Error "java runtime not found. Please verify JDK 17+ installation."
        exit 1
    }
}

if (-not (Test-Path "bin\digitalautomonitor\Main.class")) {
    Write-Host "[INFO] Binaries not found in bin/. Compiling first..." -ForegroundColor Yellow
    & ".\build.ps1"
}

Write-Host "Starting Digital Autoimmune Monitor GUI..." -ForegroundColor Green
Start-Process -FilePath $java -ArgumentList "-cp bin digitalautomonitor.Main" -WorkingDirectory $PSScriptRoot
Write-Host "[OK] Desktop GUI process spawned." -ForegroundColor Green
