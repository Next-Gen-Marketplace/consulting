FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# Копируем Gradle wrapper и файлы сборки
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./
RUN chmod +x ./gradlew

# Копируем исходники и собираем jar
COPY src src
RUN ./gradlew bootJar --no-daemon

# ===== STAGE 2: Запуск готового приложения =====
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]