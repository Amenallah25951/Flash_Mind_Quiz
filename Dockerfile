# Dockerfile corrigé avec encodage UTF-8
FROM eclipse-temurin:17-jdk-alpine

# Définit l'encodage UTF-8
ENV LANG C.UTF-8
ENV LC_ALL C.UTF-8

# Installation des dépendances nécessaires
RUN apk add --no-cache bash

WORKDIR /app

# Copie des fichiers de construction Maven d'abord (optimisation cache)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x ./mvnw

# Télécharge les dépendances Maven (cache séparé)
RUN ./mvnw dependency:go-offline -B

# Copie du code source
COPY src ./src

# Construction avec encodage explicite
RUN ./mvnw clean package -DskipTests -Dfile.encoding=UTF-8

EXPOSE 8080

CMD ["java", "-Dserver.port=${PORT:-8080}", "-Dfile.encoding=UTF-8", "-jar", "target/flash-mind-backend-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]