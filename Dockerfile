FROM openjdk:13-jdk-alpine

WORKDIR /home/dns

COPY build/libs/DNS-*-all.jar DNS.jar

ENTRYPOINT ["java"]
CMD ["-jar", "DNS.jar"]
