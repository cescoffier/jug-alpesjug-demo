FROM java:8-alpine
EXPOSE 8080
COPY target/*-fat.jar /vertx/
WORKDIR /vertx
CMD java -jar *.jar -Dvertx.cacheDirBase=/tmp