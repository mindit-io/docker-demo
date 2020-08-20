FROM openjdk:11
ENV JAVA_OPTS="-Duser.timezone=GMT "
COPY ./target/docker-demo-*.jar app.jar
#ENTRYPOINT ["java","$JAVA_OPTS -jar","/app.jar"]
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app.jar" ]
EXPOSE 8080