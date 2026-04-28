@echo off
setlocal

set "ROOT=%~dp0"
cd /d "%ROOT%"

if not exist "%ROOT%bin" mkdir "%ROOT%bin"

rem Create a sources list and compile from project root to avoid PowerShell quoting/permission issues
if exist "%ROOT%sources.txt" del /q "%ROOT%sources.txt"
for /r "%ROOT%src" %%i in (*.java) do @echo %%~fi >> "%ROOT%sources.txt"
if not exist "%ROOT%sources.txt" (
  echo No Java source files found under %ROOT%src
  pause
  exit /b 1
)

echo Compiling Java sources...
javac -d "%ROOT%bin" @"%ROOT%sources.txt"
set "JAVAC_EXIT=%ERRORLEVEL%"

if not "%JAVAC_EXIT%"=="0" (
  echo Build failed with exit code %JAVAC_EXIT%.
  pause
  exit /b 1
)

rem Run the game
java -cp "%ROOT%bin" com.capocann.site12.Main

rem If you need JavaFX, uncomment and adjust the following lines (ensure javafx-sdk is present)
rem javac --module-path "%ROOT%javafx-sdk-25.0.3\lib" --add-modules javafx.controls,javafx.swing -d "%ROOT%bin" @"%ROOT%sources.txt"
rem java --module-path "%ROOT%javafx-sdk-25.0.3\lib" --add-modules javafx.controls,javafx.swing -cp "%ROOT%bin" com.capocann.site12.Main

endlocal
