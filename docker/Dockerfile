FROM java:8-jre
ADD target/digitalsky-api-1.0.0.jar /app.jar
ADD  docker/entrypoint.sh /entrypoint.sh
ADD chennaiFir.json delhiFir.json mumbaiFir.json kolkataFir.json /
RUN chmod +x /entrypoint.sh
EXPOSE 9000
ENTRYPOINT ["/entrypoint.sh"]
