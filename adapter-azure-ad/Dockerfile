FROM openjdk
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT [ "java","-Djava.security.egd=file:/dev/./unrandom","-jar","/app.jar" ]