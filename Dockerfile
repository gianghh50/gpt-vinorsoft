FROM java:8

FROM maven:alpine

 

# image layer

WORKDIR /app/auth-service
ENV PORT 9100


# Image layer: with the application

COPY ./auth-service /app/auth-service

RUN mvn -v

#RUN mvn clean install -DskipTests

ENTRYPOINT ["java", "-Deureka.client.serviceUrl.defaultZone=http://eureka-0.eureka.default.svc.cluster.local:8761/eureka","-jar","/app/auth-service/target/auth-service-0.0.1-SNAPSHOT.jar"]

EXPOSE 9100

#, "-Deureka.client.serviceUrl.defaultZone=http://eureka:8761/eureka"