@echo off
setlocal
set "ROOT=%~dp0"
cd /d "%ROOT%"

:: 1. Cleanup
if not exist "bin" mkdir "bin"
if exist "sources.txt" del /q "sources.txt"

:: 2. Find files using a direct pipe (This preserves all slashes)
echo Finding Java files...
dir /s /b *.java > sources.txt

:: 3. Compile
echo Compiling project...
javac -d bin @sources.txt

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Build failed. Check the code for errors.
    pause
    exit /b 1
)

:: 4. Run
echo Launching Site 12...
java -cp bin com.capocann.site12.Main
pause