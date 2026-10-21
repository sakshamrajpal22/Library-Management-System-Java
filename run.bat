@echo off
setlocal
cd /d "%~dp0"

echo [1/3] Checking Database...
netstat -ano | findstr :3306 >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo Starting MariaDB Server...
    start "" /b "C:\Program Files\MariaDB 13.0\bin\mysqld.exe" --console
    timeout /t 2 /nobreak >nul
) else (
    echo MariaDB is already running on port 3306.
)

echo [2/3] Compiling Java classes...
if not exist "bin" mkdir bin
"C:\Program Files\Android\Android Studio\jbr\bin\javac.exe" -d bin -cp ".;mysql-connector-j-8.3.0.jar" LibraryBackend.java LibraryFrontend.java LibraryLogoPanel.java

echo [3/3] Launching Library Management System...
start "" "C:\Program Files\Android\Android Studio\jbr\bin\javaw.exe" -cp "bin;mysql-connector-j-8.3.0.jar" LibraryFrontend

echo App launched!
exit /b 0
