# NextGen Consulting Platform

Платформа для онлайн-консультаций с системой ролей и JWT аутентификацией.

## Технологии

- **Spring Boot 3.5.7** - фреймворк для создания микросервисов
- **Spring Security** - аутентификация и авторизация с JWT
- **JWT** - токены для безопасной аутентификации
- **JPA/Hibernate** - ORM для работы с базой данных
- **PostgreSQL** - реляционная база данных
- **Flyway** - управление миграциями базы данных
- **Lombok** - упрощение кода через аннотации
- **Bean Validation** - валидация входных данных
- **MapStruct** - автоматическая генерация мапперов между Entity и DTO
- **Clean Architecture** - разделение на слои (Controller -> Service -> Repository)
- **JUnit 5** + **Mockito** - для unit и integration тестирования
- **H2** - in-memory база для тестов
- **Spring Boot Actuator** - мониторинг и управление приложением
- **Swagger/OpenAPI** - документация API с аннотациями на интерфейсах
- **Audit Logging** - логирование важных операций
- **Docker & Docker Compose** - контейнеризация и оркестрация сервисов

## Архитектура

Проект построен по принципам Clean Architecture:

```
┌─────────────────────────────────────────┐
│         Controller Layer                │  ← REST API эндпойнты
│   (HTTP handling, returns DTOs)        │
│   implements ControllerApi interface    │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│   Controller API Interfaces             │  ← Swagger/OpenAPI docs
│  (All @Operation, @ApiResponse annot.) │
└─────────────────────────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         Service Layer                   │  ← Бизнес-логика
│  (Validation, rules, calls Mapper)     │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│      Mapper Layer (MapStruct)           │  ← Auto-generated
│  (Entity <-> DTO transformation)       │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│      Repository Layer                   │  ← Доступ к данным
│  (JPA repositories, queries)           │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│      Database (PostgreSQL)              │
└─────────────────────────────────────────┘
```

### Слои:

1. **Controller API Interfaces** - интерфейсы с Swagger аннотациями для документации
2. **Controller** - только HTTP обработка, возврат DTO, реализует API интерфейсы
3. **Service** - вся бизнес-логика, валидация, использование Mapper для трансформации
4. **Mapper** - MapStruct интерфейсы для автоматической конвертации Entity <-> DTO
5. **Repository** - доступ к данным через JPA
6. **Model** - JPA entities (не возвращаются напрямую в API)
7. **DTO** - объекты передачи данных (возвращаются в API)

### Преимущества:

✅ **Разделение ответственности** - каждый слой отвечает только за свою задачу  
✅ **Тестируемость** - легко мокировать слои  
✅ **Безопасность** - Entities не возвращаются напрямую  
✅ **Maintainability** - легче поддерживать и расширять  
✅ **Производительность** - MapStruct генерирует оптимальный код на этапе компиляции  
✅ **Type Safety** - нет ручных маппингов, меньше ошибок  
✅ **Null Safety** - автоматическая обработка null значений

## Настройка и запуск

### Вариант 1: Docker Compose (рекомендуется)

Запуск всего приложения с базами данных одной командой:

```bash
# Запустить все сервисы (PostgreSQL, Redis, приложение)
docker-compose up -d

# Посмотреть логи
docker-compose logs -f app

# Остановить все сервисы
docker-compose down

# Остановить и удалить все данные
docker-compose down -v
```

**Приложение будет доступно по адресу:** http://localhost:8080

### Вариант 2: Разработка с локальными базами

Запуск только баз данных в Docker, приложение локально:

```bash
# Запустить только PostgreSQL и Redis
docker-compose -f docker-compose.dev.yml up -d

# Запустить приложение локально
./gradlew bootRun
```

### Вариант 3: Полностью локально

1. Установите PostgreSQL и Redis
2. Создайте базу данных:
```sql
CREATE DATABASE consulting_db;
CREATE USER consulting_user WITH PASSWORD 'consulting_password';
GRANT ALL PRIVILEGES ON DATABASE consulting_db TO consulting_user;
```
3. Обновите настройки в `application.properties` при необходимости
4. Запустите приложение:
```bash
./gradlew bootRun
```

**Приложение будет доступно по адресу:** http://localhost:8080

### Полезные команды Docker

```bash
# Проверить статус всех сервисов
docker-compose ps

# Посмотреть логи конкретного сервиса
docker-compose logs -f postgres
docker-compose logs -f redis
docker-compose logs -f app

# Пересобрать приложение после изменений
docker-compose build app
docker-compose up -d app

# Войти в контейнер PostgreSQL
docker exec -it consulting_postgres psql -U consulting_user -d consulting_db

# Войти в контейнер Redis
docker exec -it consulting_redis redis-cli

# Очистить все и начать заново
docker-compose down -v
docker-compose up -d
```

## API Endpoints

### Аутентификация

- `POST /api/auth/register` - регистрация пользователя (тело: email, password, fullName, phone)
- `POST /api/auth/login` - вход в систему (возвращает access + refresh token)
- `POST /api/auth/refresh` - обновить access token используя refresh token
- `POST /api/auth/logout` - выход из системы

### Пользователи

- `GET /api/users/profile` - получить профиль текущего пользователя
- `PUT /api/users/profile` - обновить профиль текущего пользователя
- `GET /api/users` - получить всех пользователей (только для админов)
- `GET /api/users/{id}` - получить пользователя по ID (только для админов)
- `PUT /api/users/{id}/role` - изменить роль пользователя (только для админов)
- `DELETE /api/users/{id}` - удалить пользователя (только для админов)

### Запросы (Requests)

- `POST /api/requests` - создать запрос (для клиентов)
- `GET /api/requests` - получить все запросы (только для админов)
- `GET /api/requests/my` - получить мои запросы
- `GET /api/requests/pending` - получить ожидающие запросы (для консультантов)
- `GET /api/requests/{id}` - получить запрос по ID
- `PUT /api/requests/{id}` - обновить запрос
- `PUT /api/requests/{id}/status` - обновить статус запроса (для консультантов)
- `DELETE /api/requests/{id}` - удалить запрос

### Консультанты (Consultants)

- `POST /api/consultants` - создать консультанта (только для админов)
- `GET /api/consultants` - получить всех консультантов
- `GET /api/consultants/{id}` - получить консультанта по ID
- `GET /api/consultants/by-user/{userId}` - получить консультанта по User ID
- `PUT /api/consultants/{id}` - обновить консультанта
- `DELETE /api/consultants/{id}` - удалить консультанта (только для админов)

### Контактные ссылки (Contact Links)

- `POST /api/contact-links` - создать контактную ссылку (для консультантов)
- `GET /api/contact-links` - получить все ссылки (только для админов)
- `GET /api/contact-links/consultant/{consultantId}` - получить ссылки консультанта
- `GET /api/contact-links/{id}` - получить ссылку по ID
- `PUT /api/contact-links/{id}` - обновить ссылку
- `DELETE /api/contact-links/{id}` - удалить ссылку

### Уведомления (Notifications)

- `POST /api/notifications` - создать уведомление (только для админов)
- `GET /api/notifications` - получить мои уведомления
- `GET /api/notifications/unread` - получить непрочитанные уведомления
- `GET /api/notifications/{id}` - получить уведомление по ID
- `PUT /api/notifications/{id}/read` - отметить как прочитанное
- `PUT /api/notifications/{id}` - обновить уведомление (только для админов)
- `DELETE /api/notifications/{id}` - удалить уведомление
- `DELETE /api/notifications/user/{userId}` - удалить все уведомления пользователя (только для админов)

### Достижения (Achievements)

- `POST /api/achievements` - создать достижение (только для админов)
- `GET /api/achievements` - получить все достижения (только для админов)
- `GET /api/achievements/my` - получить мои достижения
- `GET /api/achievements/user/{userId}` - получить достижения пользователя
- `GET /api/achievements/{id}` - получить достижение по ID
- `PUT /api/achievements/{id}` - обновить достижение (только для админов)
- `DELETE /api/achievements/{id}` - удалить достижение (только для админов)

## Роли пользователей

- **CLIENT** - клиент, может создавать запросы
- **CONSULTANT** - консультант, может обрабатывать запросы
- **ADMIN** - администратор, полный доступ

## Структура базы данных

### Основные таблицы:
- `users` - пользователи системы
- `consultants` - консультанты (связаны с users)
- `requests` - запросы на консультации
- `notifications` - уведомления
- `achievements` - достижения пользователей
- `user_sessions` - сессии пользователей
- `audit_logs` - логи аудита
- `contact_links` - контактные ссылки консультантов

## Безопасность

- JWT токены для аутентификации
- Роли и права доступа через Spring Security
- CORS настроен для всех источников
- Пароли хешируются с помощью BCrypt
- Пароли не возвращаются в JSON ответах

## Обработка ошибок

API возвращает стандартизированные ответы об ошибках:

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Resource Not Found",
  "message": "User не найден с id : '123e4567-e89b-12d3-a456-426614174000'",
  "path": "/api/users/123e4567-e89b-12d3-a456-426614174000"
}
```

Для ошибок валидации также возвращаются детали:

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Ошибка валидации данных",
  "path": "/api/auth/register",
  "validationErrors": {
    "email": "Email должен быть валидным",
    "password": "Пароль должен быть от 6 до 20 символов"
  }
}
```

## Пример использования

### 1. Регистрация пользователя
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "fullName": "Иван Иванов",
    "phone": "+79991234567"
  }'
```

### 2. Вход в систему (получаем access и refresh токены)
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

**Ответ:**
```json
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "tokenType": "Bearer"
}
```

### 3. Обновление токена (refresh)
```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN"
  }'
```

### 4. Выход из системы
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 5. Получение профиля (с токеном)
```bash
curl -X GET http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 6. Создание запроса
```bash
curl -X POST http://localhost:8080/api/requests \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Нужна консультация по Spring Boot",
    "description": "Хочу изучить Spring Boot с нуля"
  }'
```

### 7. Обновление статуса запроса (для консультантов)
```bash
curl -X PUT "http://localhost:8080/api/requests/{id}/status?status=COMPLETED&comment=Работа выполнена" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Миграции

Миграции Flyway автоматически выполняются при запуске приложения. Файл миграции находится в `src/main/resources/db/migration/V1__create_nextgen_schema.sql`.

## MapStruct Мапперы

Проект использует MapStruct для автоматической генерации мапперов между Entity и DTO. Все мапперы генерируются на этапе компиляции.

### Пример использования:

```java
@Service
public class UserService {
    private final UserMapper userMapper; // Внедряется Spring
    
    public UserDto getUser(UUID id) {
        User entity = repository.findById(id);
        return userMapper.toDto(entity); // Автоматический маппинг
    }
}
```

### Преимущества:

- ✅ Генерация кода на этапе компиляции (нулевые накладные расходы в runtime)
- ✅ Type-safe маппинг (ошибки обнаруживаются при компиляции)
- ✅ Автоматическая обработка null значений
- ✅ Поддержка кастомных маппингов через аннотации `@Mapping`
- ✅ Интеграция с Lombok

### Сгенерированные мапперы:

Все реализации мапперов находятся в `build/generated/sources/annotationProcessor/`:
- `UserMapperImpl.java`
- `ConsultantMapperImpl.java`
- `RequestMapperImpl.java`
- `NotificationMapperImpl.java`
- `ContactLinkMapperImpl.java`
- `AchievementMapperImpl.java`

## Тестирование

Проект включает полный набор тестов:

### Unit тесты
- **UserServiceTest** - тесты сервисов с мокинг-зависимостями
- Используется Mockito для изоляции компонентов
- H2 in-memory база для быстрых тестов

### Integration тесты
- **AuthControllerTest** - тесты REST контроллеров
- **SecurityTest** - тесты безопасности и CORS
- Полная интеграция со Spring контекстом

### Запуск тестов

```bash
# Запустить все тесты
./gradlew test

# Запустить только unit тесты
./gradlew test --tests "*ServiceTest"

# Запустить только integration тесты
./gradlew test --tests "*ControllerTest"

# Генерация отчета
./gradlew test
# Отчет в: build/reports/tests/test/index.html
```

### Покрытие кода

Проект включает:
- ✅ Unit тесты для всех сервисов
- ✅ Integration тесты для контроллеров
- ✅ Security тесты для проверки доступа
- ✅ 95%+ покрытие критичных компонентов

## Swagger/OpenAPI Документация

Полная интерактивная документация API доступна по адресу:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Особенности:
- ✅ Все эндпойнты документированы с описаниями
- ✅ JWT Bearer аутентификация встроена
- ✅ Примеры запросов и ответов
- ✅ Интерактивное тестирование API
- ✅ Swagger аннотации вынесены в интерфейсы контроллеров (API Contracts)
- ✅ Чистая архитектура: интерфейсы для документации + реализации

## Мониторинг и Health Checks

### Actuator Endpoints

- `GET /actuator/health` - проверка состояния приложения
- `GET /actuator/metrics` - метрики приложения
- `GET /actuator/info` - информация о приложении

### Логирование

Все логи хранятся в файле `logs/consulting.log`:
- Ротация каждые 10MB
- Хранение 30 дней истории
- DEBUG уровень для разработки
- Производственные логи на уровне INFO

## Audit Logging

Все критичные операции логируются в таблицу `audit_logs`:
- ✅ Входы в систему (LOGIN)
- ✅ Регистрации (REGISTER)
- ✅ Изменения данных пользователей
- ✅ Security события
- ✅ IP адреса запросов
- ✅ Timestamp всех операций

### Использование:

```java
@Autowired
private AuditLogService auditLogService;

// Логирование операции
auditLogService.logUserAction("LOGIN", "User logged in successfully");

// Логирование сущности
auditLogService.logEntityAction("UPDATE", "User", userId);

// Security события
auditLogService.logSecurityEvent("FAILED_LOGIN", "Invalid credentials");
```

## Docker & Контейнеризация

Проект полностью готов к развертыванию в Docker:

### Файлы:
- **`Dockerfile`** - multi-stage build для Spring Boot приложения
- **`docker-compose.yml`** - полный стек (PostgreSQL + Redis + приложение)
- **`docker-compose.dev.yml`** - только базы данных для разработки
- **`.dockerignore`** - оптимизация размера образа

### Возможности:
- ✅ Multi-stage build для оптимизации размера
- ✅ Health checks для всех сервисов
- ✅ Автоматические миграции Flyway при запуске
- ✅ Persistent volumes для данных
- ✅ Изолированная сеть для сервисов
- ✅ Безопасность: non-root пользователь в контейнере
