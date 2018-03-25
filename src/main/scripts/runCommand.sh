#!/bin/sh
JAR_EXEC=pega-logviewer-3.2-SNAPSHOT.jar
JAVA_OPTS="-Xms128M -Xmx1G -XX:+UseParNewGC -XX:+UseConcMarkSweepGC"
java ${JAVA_OPTS} -jar ${JAR_EXEC} $1 $2
