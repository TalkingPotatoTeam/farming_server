FROM openjdk:11-jdk

COPY ./build/libs/farming_springboot-0.0.1.jar application.jar

EXPOSE 8080

CMD ["java", "-jar", "application.jar"]