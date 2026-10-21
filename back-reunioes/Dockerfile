# Etapa 1: build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# Etapa 2: runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Criar grupo e utilizador não-root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copiar o JAR garantindo que o novo utilizador é o proprietário
COPY --from=build --chown=appuser:appgroup /app/target/*.jar app.jar

# Alternar para o utilizador não-privilegiado
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]