@echo off
cd /d "%~dp0.."
if not exist "bin" mkdir "bin"

echo Gathering source files...
powershell -NoProfile -Command "Get-ChildItem -Recurse -Filter *.java src | Resolve-Path -Relative | ForEach-Object { $_ -replace '\\', '/' } | Set-Content -Encoding ASCII sources.txt"

echo Compiling Java source files...
javac -encoding UTF-8 -d bin @sources.txt
set STATUS=%ERRORLEVEL%
if exist sources.txt del sources.txt

if %STATUS% equ 0 (
    echo [SUCCESS] Compilation finished successfully into bin\ folder!
) else (
    echo [ERROR] Compilation failed!
)