Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "Deploying Digital Autoimmune Monitor..." -ForegroundColor Cyan
Write-Host "===================================================" -ForegroundColor Cyan

# Step 1: Compile all source files
& powershell -ExecutionPolicy Bypass -File .\build.ps1
if ($LASTEXITCODE -ne 0) {
    Write-Error "Build failed. Aborting deployment."
    exit $LASTEXITCODE
}

# Step 2: Run DeployPackager
$java = "java"
if (Test-Path "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\jbr\bin\java.exe") {
    $java = "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\jbr\bin\java.exe"
} elseif ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
    $java = "$env:JAVA_HOME\bin\java.exe"
}

Write-Host "Running Standalone Deployment Engine with $java..." -ForegroundColor Gray
& $java -cp bin digitalautomonitor.DeployPackager

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n[DEPLOY SUCCESS] Application packaged into dist/ directory!" -ForegroundColor Green
    Write-Host "Execute with:  .\dist\DigitalAutoimmuneMonitor.bat" -ForegroundColor Cyan
    Write-Host "Or via JAR:    & '$java' -jar dist\DigitalAutoimmuneMonitor.jar" -ForegroundColor Cyan
} else {
    Write-Host "`n[DEPLOY ERROR] Deployment packaging failed." -ForegroundColor Red
}
