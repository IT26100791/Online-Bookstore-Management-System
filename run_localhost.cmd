@echo off
setlocal
echo ========================================================
echo   Starting Aura Books - Online Bookstore Management System
echo ========================================================

set "JAVA_HOME=C:\Program Files\Java\jdk-26.0.1"
set "JRE_HOME=C:\Program Files\Java\jdk-26.0.1"
set "CATALINA_HOME=C:\PROGRA~2\APACHE~1\Tomcat 9.0"
set "CATALINA_BASE=C:\PROGRA~2\APACHE~1\Tomcat 9.0"
set "MAVEN_CMD=C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.3\plugins\maven\lib\maven3\bin\mvn.cmd"

echo [1/3] Packaging Web Application with Maven...
call "%MAVEN_CMD%" package -DskipTests

echo [2/3] Deploying to Tomcat...
copy /Y "target\bookstore-1.0.war" "%CATALINA_BASE%\webapps\bookstore.war"

echo [3/3] Launching Tomcat on localhost:8081...
start "" http://localhost:8081/bookstore/
call "%CATALINA_HOME%\bin\catalina.bat" run
