@echo off
rem @ <-- workaround for http://jira.codehaus.org/browse/MRESOURCES-104
@cd %~dp0
rem @ <-- workaround for http://jira.codehaus.org/browse/MRESOURCES-104

set JAVA=%JAVA_HOME%
if "%JAVA%" == "" (
    echo Java path is not configured.
    echo Please define JAVA_HOME environement variable or edit Java path into this script.
    pause
    exit
)

for /f tokens^=2-5^ delims^=.-_^" %%j in ('"%JAVA%\bin\java.exe" -fullversion 2^>^&1') do set "JAVA_VER=%%j%%k"
if %JAVA_VER% LSS 18 (
    echo This program requires Java version greater than 1.8.
    echo Please define JAVA_HOME environement variable or edit Java path into this script.
    pause
    exit
)

"%JAVA%\bin\java.exe" -jar ./bin/Wall-T.jar
