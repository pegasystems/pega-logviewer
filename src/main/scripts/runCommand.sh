#!/bin/sh
JAR_EXEC=${bin.name}
JAVA_OPTS="-Xms${java.heap.min} -Xmx${java.heap.max} -XX:+UseParNewGC -XX:+UseConcMarkSweepGC"
java ${JAVA_OPTS} -jar ${JAR_EXEC} $*
