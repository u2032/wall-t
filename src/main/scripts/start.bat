@echo off
rem @ <-- workaround for http://jira.codehaus.org/browse/MRESOURCES-104
@cd %~dp0
rem @ <-- workaround for http://jira.codehaus.org/browse/MRESOURCES-104

rem For example: set "JAVA=C:\Progam Files\java\jdk8"
set "JAVA=%JAVA8_HOME%"


rem -------- Do not edit under this line

if "%JAVA%" == "" (
	set "JAVA=%JAVA_HOME%"
)

if "%JAVA%" == "" (
    echo Java path is not configured.
    echo Please define JAVA8_HOME environment variable or edit JAVA path into this script.
    pause
    exit
)

for /f tokens^=2-5^ delims^=.-_^" %%j in ('"%JAVA%\bin\java.exe" -fullversion 2^>^&1') do set "JAVA_VER=%%j%%k"
if %JAVA_VER% LSS 18 (
    echo This program requires Java version greater than 1.8.
    echo Please define JAVA8_HOME environment variable or edit JAVA path into this script.
    pause
    exit
)

start "Wall-T" /B "%JAVA%\bin\javaw.exe" -jar ./bin/${project.artifactId}.jar
