FROM openjdk:17-jdk-alpine as builder
LABEL authors="David"

WORKDIR /app/msvc-clientes

COPY ./pom.xml /app
COPY ./msvc-clientes/.mvn ./.mvn
COPY ./msvc-clientes/mvnw .
COPY ./msvc-clientes/pom.xml .

RUN ./mvnw clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target/

COPY ./msvc-clientes/src ./src
ARG LOCAL_HOSTNAME=host.docker.internal
RUN ./mvnw clean package

FROM openjdk:17-jdk-alpine

WORKDIR /app

ARG MYSQL_IMAGE_HOSTNAME=mysql8
COPY --from=builder /app/msvc-clientes/target/msvc-clientes-0.0.1-SNAPSHOT.jar .
EXPOSE 8001

ENTRYPOINT ["java", "-jar", "msvc-clientes-0.0.1-SNAPSHOT.jar"]