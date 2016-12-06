FROM java:8-alpine
COPY target/*-fat.jar /app/
WORKDIR /app
EXPOSE 8080
CMD java -jar *.jar -Dvertx.cacheDirBase=/tmp
