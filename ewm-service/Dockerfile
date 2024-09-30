FROM eclipse-temurin:21-jre-jammy
ENV SPRING_PROFILES_ACTIVE=production
COPY target/*.jar ewm-service.jar
ENTRYPOINT ["java","-jar", "/ewm-service.jar"]