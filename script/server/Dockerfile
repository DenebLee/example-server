FROM amazoncorretto:8u362-alpine3.17-jre

RUN mkdir /app
WORKDIR /app

COPY ./app.jar /app/app.jar

CMD ["/usr/lib/jvm/default-jvm/jre/bin/java", "-jar", "/app/app.jar"]