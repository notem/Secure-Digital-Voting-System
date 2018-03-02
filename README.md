# Secure-Digital-Voting-System

### PostGreSQL Instructions
1) Install PostGreSQL <https://www.postgresql.org/>
2) Create a database and user
3) Set system environment variables (if using IntelliJ, this is done in the run configuration)
    * dbUser -> user account
    * dbPassword -> user's password
    * dbURI -> something similar to "jdbc:postgresql://localhost:5432/mydb"

### Build Instructions
1) Install Maven <https://maven.apache.org/>
1) Use Maven to compile to a .war package (ex. "mvn war:war" or "mvn package")
2) Move "my-webapp.jar" to apache-tomcat-9.0.5/webapps/
3) Run "apache-tomcat-9.0.5/bin/catalina.sh start"

Alternatively, modify the tomcat web server configuration preset for one-button building and running
