@echo off
setlocal

if "%CATALINA_HOME%"=="" (
    echo CATALINA_HOME is not set. Point it to your Tomcat 10.1 installation.
    exit /b 1
)

call "%CATALINA_HOME%\bin\startup.bat"
if errorlevel 1 exit /b %errorlevel%

start "" "http://localhost:8080/Project/login"
endlocal
