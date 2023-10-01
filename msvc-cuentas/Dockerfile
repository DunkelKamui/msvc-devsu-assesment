FROM openjdk:17-jdk-alpine as builder
LABEL authors="David"

WORKDIR /app/msvc-cuentas

COPY ./pom.xml /app
COPY ./msvc-cuentas/.mvn ./.mvn
COPY ./msvc-cuentas/mvnw .
COPY ./msvc-cuentas/pom.xml .

RUN ./mvnw clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target/

COPY ./msvc-cuentas/src ./src
ARG LOCAL_HOSTNAME=host.docker.internal
ARG CLIENTES_HOSTNAME=host.docker.internal
RUN ./mvnw clean package

FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY --from=builder /app/msvc-cuentas/target/msvc-cuentas-0.0.1-SNAPSHOT.jar .
EXPOSE 8002

ENTRYPOINT ["java", "-jar", "msvc-cuentas-0.0.1-SNAPSHOT.jar"]