FROM gradle:jdk10 as build

WORKDIR /srv

USER root

add . /srv

RUN  gradle build

FROM openjdk:10-jre-slim

COPY --from=build /srv/build/libs/crawlerScheduler-0.1.jar /srv/

ENTRYPOINT ["java", "-server", "-jar", "/srv/crawlerScheduler-0.1.jar"]