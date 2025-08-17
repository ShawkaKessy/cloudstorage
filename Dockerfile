FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Копируем только wrapper и pom для прогрева зависимостей
COPY .mvn/ .mvn/
COPY mvnw mvnw
COPY pom.xml pom.xml
RUN chmod +x mvnw && ./mvnw -q -DskipTests dependency:go-offline

# Копируем исходники и собираем
COPY src src
RUN ./mvnw -q -DskipTests package

EXPOSE 8080
CMD ["java","-jar","target/cloudstorage-1.0.0.jar"]
