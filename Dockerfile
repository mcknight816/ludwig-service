FROM maven:3-jdk-11
ADD . /build
WORKDIR /build
RUN mvn clean install -DskipTests

FROM openjdk:11-jdk
VOLUME /tmp
COPY --from=0 "/build/target/ludwig-service-*-SNAPSHOT.jar" app.jar
CMD ["java","-Dspring.profiles.active=prod","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
