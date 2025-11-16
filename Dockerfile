FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copie des fichiers
COPY . .

# Rend Maven executable
RUN chmod +x ./mvnw

# Construction avec encodage forcé
RUN ./mvnw clean package -DskipTests -Dfile.encoding=UTF-8

EXPOSE 8080

CMD ["java", "-Dserver.port=${PORT:-8080}", "-jar", "target/flash-mind-backend-0.0.1-SNAPSHOT.jar"]