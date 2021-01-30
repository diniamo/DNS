FROM openjdk:11-jdk-slim-buster

WORKDIR /home/dns

COPY build/libs/DNS-*-all.jar DNS.jar

ENTRYPOINT ["java"]
CMD ["-jar", "DNS.jar"]
