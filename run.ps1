# Runner for Library Management System
Set-Location -Path $PSScriptRoot

Write-Host "[1/3] Checking Database..." -ForegroundColor Cyan
$conn = Test-NetConnection -ComputerName localhost -Port 3306 -WarningAction SilentlyContinue
if (-not $conn.TcpTestSucceeded) {
    Write-Host "Starting MariaDB Server..." -ForegroundColor Yellow
    Start-Process -FilePath "C:\Program Files\MariaDB 13.0\bin\mysqld.exe" -ArgumentList "--console" -WindowStyle Hidden
    Start-Sleep -Seconds 2
} else {
    Write-Host "MariaDB is already running on port 3306." -ForegroundColor Green
}

Write-Host "[2/3] Checking compiled classes..." -ForegroundColor Cyan
if (-not (Test-Path "bin\LibraryFrontend.class")) {
    if (-not (Test-Path "bin")) { New-Item -ItemType Directory -Path "bin" | Out-Null }
    & "C:\Program Files\Android\Android Studio\jbr\bin\javac.exe" -d bin -cp ".;mysql-connector-j-8.3.0.jar" LibraryBackend.java LibraryFrontend.java
}

Write-Host "[3/3] Launching Library Management System..." -ForegroundColor Green
Start-Process -FilePath "C:\Program Files\Android\Android Studio\jbr\bin\javaw.exe" -ArgumentList "-cp `"bin;mysql-connector-j-8.3.0.jar`" LibraryFrontend"
Write-Host "Library Management System is running!" -ForegroundColor Green
