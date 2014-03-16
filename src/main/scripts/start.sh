#!/bin/sh

cd $(dirname "$0")

# For example: JAVA=/etc/java/jre8
JAVA=$JAVA8_HOME


# -------- Do not edit under this line

if [ -z "$JAVA" ]
then
JAVA=$JAVA_HOME
fi

if [ -z "$JAVA" ]
then
        echo "Java path is not configured."
        echo "Please define JAVA8_HOME environement variable or edit JAVA path into this script."
        exit
fi


JAVA_VER=$($JAVA/bin/java -version 2>&1 | sed 's/java version "\(.*\)\.\(.*\)\..*"/\1\2/; 1q')
if [ "$JAVA_VER" -ge 18 ] 
then
        echo "Checking Java version: It's Ok." 
else 
        echo "This program requires Java version greater than 1.8."
        echo "Please define JAVA8_HOME environement variable or edit JAVA path into this script."
        exit
fi

$JAVA/bin/java -jar ./bin/${project.artifactId}.jar > /dev/null &