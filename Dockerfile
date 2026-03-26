FROM eclipse-temurin:21

WORKDIR /app

COPY /target/app.jar /app/app.jar

EXPOSE 9097

ENTRYPOINT ["java","-jar","app.jar"]
