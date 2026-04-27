@echo off
setlocal

set "ROOT=%~dp0"
cd /d "%ROOT%"

if not exist "%ROOT%bin" mkdir "%ROOT%bin"
powershell -NoProfile -ExecutionPolicy Bypass -Command "& { $files = Get-ChildItem -LiteralPath '%ROOT%src' -Filter *.java -Recurse | ForEach-Object { $_.FullName }; if (-not $files) { Write-Host 'No Java source files found.'; exit 1 }; & javac -d '%ROOT%bin' $files; exit $LASTEXITCODE }"
set "JAVAC_EXIT=%ERRORLEVEL%"

if not "%JAVAC_EXIT%"=="0" (
  echo Build failed.
  pause
  exit /b 1
)

java -cp "%ROOT%bin" com.capocann.site12.Main

endlocal
