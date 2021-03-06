FROM openjdk:11.0.9

WORKDIR /home/dns

COPY build/libs/DNS-*-all.jar DNS.jar

ENTRYPOINT ["java"]
CMD ["-jar", "DNS.jar"]
