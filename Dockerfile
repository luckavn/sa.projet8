FROM openjdk:8-jdk-alpine
LABEL responsable="lucasvannier@gmail.com"
EXPOSE 8081:8080
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]