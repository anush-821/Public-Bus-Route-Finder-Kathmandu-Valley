@echo off
cd /d "%~dp0.."
chcp 65001 >nul
if not exist "bin\com\kathmandu\transit\Main.class" (
    echo Project not compiled yet. Running compile first...
    call "%~dp0compile.bat"
)
java -Dfile.encoding=UTF-8 -cp bin com.kathmandu.transit.Main --cli
pause
