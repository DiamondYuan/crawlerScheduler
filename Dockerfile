FROM gradle as build

WORKDIR /srv

USER root

add . /srv

RUN  gradle build

FROM java:8-jre

COPY --from=build /srv/build/libs/*.jar /srv/

ENTRYPOINT ["java", "-server", "-jar", "/srv/crawlerScheduler-0.1.jar"]

