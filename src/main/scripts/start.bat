@echo off
rem @ <-- workaround for http://jira.codehaus.org/browse/MRESOURCES-104
@cd %~dp0
rem @ <-- workaround for http://jira.codehaus.org/browse/MRESOURCES-104

set JAVA="%JAVA8_HOME%\bin\java.exe"

%JAVA% -jar ${project.artifactId}.jar
