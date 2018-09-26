IF not exist "logs" (mkdir "logs")

java -Dlog4j.configurationFile=conf/log4j2.xml -jar ${project.artifactId}-${project.version}.jar

pause