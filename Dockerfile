# Étape 1 : Utilise une image Java 17 officielle
FROM eclipse-temurin:17-jdk-alpine

# Étape 2 : Définit le répertoire de travail dans le conteneur
WORKDIR /app

# Étape 3 : Copie les fichiers nécessaires
COPY . .

# Étape 4 : Donne les permissions d'exécution au Maven wrapper
RUN chmod +x ./mvnw

# Étape 5 : Construit l'application
RUN ./mvnw clean package -DskipTests

# Étape 6 : Expose le port 8080
EXPOSE 8080

# Étape 7 : Commande pour lancer l'application
ENTRYPOINT ["java", "-jar", "target/flash-mind-backend-0.0.1-SNAPSHOT.jar"]