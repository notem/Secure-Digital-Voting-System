# Secure-Digital-Voting-System
This project uses the Maven application for dependency management and WAR packaging.
The web application archive can be loaded by your favorite Java Servlet Container application 
(Apache Tomcat is provided with a standard configuration which should fill this need).
This application also requires that a PostGreSQL database be prepared to run properly.

### PostGreSQL Instructions
1) Install PostGreSQL <https://www.postgresql.org/>
2) Create a database and user
3) Set system environment variables
    * dbUser -> user account
    * dbPassword -> user's password
    * dbURI -> something similar to "jdbc:postgresql://localhost:5432/mydb"
    
### Building from Source Instructions
1) Install Maven <https://maven.apache.org/>
2) Use Maven to compile to a .war package (ex. "mvn war:war" or "mvn package")

### Installing the WAR file and Running Tomcat
1) Move "ROOT.war" to apache-tomcat-9.0.5/webapps/
2) Run "apache-tomcat-9.0.5/bin/catalina.sh start" 
    (or "apache-tomcat-9.0.5/bin/catalina.bat start" on Windows Systems)
3) The web app should be accessible via "localhost:8080/"
