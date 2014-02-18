@echo off
rem @ <-- workaround for http://jira.codehaus.org/browse/MRESOURCES-104


"%JAVA8_HOME%\bin\java.exe" -jar ${project.artifactId}.jar
