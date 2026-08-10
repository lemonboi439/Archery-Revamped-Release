@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-21"
call gradlew.bat runClient --no-daemon
pause
