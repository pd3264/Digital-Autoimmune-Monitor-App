Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "Compiling Digital Autoimmune Monitor..." -ForegroundColor Cyan
Write-Host "===================================================" -ForegroundColor Cyan

$javac = "javac"
if (-not (Get-Command "javac" -ErrorAction SilentlyContinue)) {
    if (Test-Path "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\jbr\bin\javac.exe") {
        $javac = "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\jbr\bin\javac.exe"
    } elseif ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\javac.exe")) {
        $javac = "$env:JAVA_HOME\bin\javac.exe"
    } else {
        Write-Error "javac compiler not found. Please verify JDK 17+ installation."
        exit 1
    }
}

if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

$sources = Get-ChildItem -Path "src" -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName
Write-Host "Compiling $($sources.Count) Java source files with $javac..." -ForegroundColor Gray

& $javac -d bin -sourcepath src $sources

if ($LASTEXITCODE -eq 0) {
    Write-Host "[SUCCESS] Compilation completed successfully into bin/ directory." -ForegroundColor Green
} else {
    Write-Host "[ERROR] Compilation failed with exit code $LASTEXITCODE." -ForegroundColor Red
}
