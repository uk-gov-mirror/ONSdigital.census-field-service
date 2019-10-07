FROM openjdk:11-jre-slim

ARG JAR_FILE=censusfieldsvc*.jar
RUN apt-get update
RUN apt-get -yq clean
RUN groupadd -g 983 fieldsvc && \
    useradd -r -u 983 -g fieldsvc fieldsvc
USER fieldsvc
COPY target/$JAR_FILE /opt/censusfieldsvc.jar

ENTRYPOINT [ "java", "-jar", "/opt/censusfieldsvc.jar" ]

