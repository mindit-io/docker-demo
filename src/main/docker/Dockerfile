FROM openjdk:11
COPY ./target/docker-demo-*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080