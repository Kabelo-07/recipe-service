FROM openjdk:17-jdk-slim

ENV JAVA_OPTS="-Xmx512m -Xms256m"

ADD target/recipes-service-*.jar /app/recipe-service.jar

ENTRYPOINT ["java","-jar","/app/recipe-service.jar"]