@echo off
if defined CHROME if exist "%CHROME%" goto chrome_found

set "CHROME=%ProgramFiles%\Google\Chrome\Application\chrome.exe"
if exist "%CHROME%" goto chrome_found

set "CHROME=%ProgramFiles(x86)%\Google\Chrome\Application\chrome.exe"
if exist "%CHROME%" goto chrome_found

set "CHROME=%LocalAppData%\Google\Chrome\Application\chrome.exe"
if exist "%CHROME%" goto chrome_found

for /f "delims=" %%I in ('where chrome 2^>nul') do (
  set "CHROME=%%I"
  goto chrome_found
)

echo Chrome executable was not found. Set CHROME to chrome.exe path and run again.
exit /b 1

:chrome_found
set CHROME_OPTS=--chrome-frame --allow-file-access-from-files --window-position=250,50 --window-size="720,460"
set "WD=%cd%"
set APP=--app="%WD%\app.html"

start "" "%CHROME%" %CHROME_OPTS% %APP%
