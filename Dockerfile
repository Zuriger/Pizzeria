# ── Etapa 1: Compilar el proyecto ──────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copia primero el pom.xml para aprovechar caché de dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia el código fuente y compila
COPY src ./src
RUN mvn clean package -DskipTests -B

# ── Etapa 2: Imagen final ligera ────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copia el JAR generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Puerto que expone la aplicación (por defecto Spring Boot = 8080)
EXPOSE 8080

# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]