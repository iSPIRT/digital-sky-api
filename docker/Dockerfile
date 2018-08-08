FROM java:8-jre
ADD digitalsky-api-1.0.0.jar /app.jar
ADD  entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
EXPOSE 9000
ENTRYPOINT ["/entrypoint.sh"]
