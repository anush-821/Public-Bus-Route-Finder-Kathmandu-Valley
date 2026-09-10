@echo off
cd /d "%~dp0.."
if not exist "bin\com\kathmandu\transit\Main.class" (
    echo Project not compiled yet. Running compile first...
    call "%~dp0compile.bat"
)
start javaw -cp bin com.kathmandu.transit.Main --gui