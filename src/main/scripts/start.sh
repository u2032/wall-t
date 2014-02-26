#!/bin/sh

cd $(dirname "$0")

JAVA=$JAVA8_HOME/bin/java

cd ./bin
$JAVA -jar ${project.artifactId}.jar