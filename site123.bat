@echo off
setlocal
cd /d "%~dp0"

:: 1. Cleanup
if not exist "bin" mkdir "bin"

:: 2. Compile everything at once
:: We are adding the \tactical and \io folders to the command
echo Compiling all packages...
javac -d bin ^
 "src\com\capocann\site12\*.java" ^
 "src\com\capocann\site12\ui\*.java" ^
 "src\com\capocann\site12\tactical\*.java" ^
 "src\com\capocann\site12\io\*.java"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Build failed. Check the errors above.
    pause
    exit /b 1
)

:: 3. Run the game
echo Launching Site 12...
java -cp bin com.capocann.site12.Main
pause