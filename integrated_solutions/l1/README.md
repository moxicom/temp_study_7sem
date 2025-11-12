https://gitverse.ru/max_verstappen/uni_integrated_solutions_lab_1
# Task Management API

Веб-сервис для управления проектами и задачами с поддержкой двух версий API (v1 и v2).

## Архитектура

Приложение реализует REST API с двумя версиями:
- **API v1**: Базовая функциональность управления проектами и задачами
- **API v2**: Расширенная функциональность с поддержкой приоритетов и фильтрации

### Основные компоненты:

- **Middleware**: Логирование, Rate Limiting, JWT аутентификация
- **Handlers**: Обработчики HTTP запросов для v1 и v2 API
- **In-memory Storage**: Хранение данных в памяти (проекты, задачи, идемпотентность)

## Запуск приложения

### Локальный запуск

#### Требования
- Go 1.24 или выше
- Git

#### Инструкция

1. Клонируйте репозиторий:
```bash
git clone <repository-url>
cd l1
```

2. Установите зависимости:
```bash
go mod download
```

3. Запустите приложение:
```bash
go run cmd/l1/*.go
```

Приложение будет доступно по адресу: `http://localhost:8080`

### Запуск с Docker

#### Требования
- Docker
- Docker Compose (опционально)

#### Dockerfile

```dockerfile
FROM golang:1.24-alpine AS builder

WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download

COPY . .
RUN go build -o main cmd/l1/*.go

FROM alpine:latest
RUN apk --no-cache add ca-certificates
WORKDIR /root/

COPY --from=builder /app/main .

EXPOSE 8080
CMD ["./main"] 
```

#### Инструкция запуска

1. Соберите Docker образ:
```bash
docker build -t task-api .
```

2. Запустите контейнер:
```bash
docker run -p 8080:8080 task-api
```

#### Docker Compose

```yaml
version: '3.8'
services:
  task-api:
    build: .
    ports:
      - "8080:8080"
    environment:
      - PORT=8080
    restart: unless-stopped
```

Запуск с docker-compose:
```bash
docker-compose up -d
```

## API Документация

### Endpoints

#### API v1
- `GET /api/v1/projects` - Список проектов
- `POST /api/v1/projects` - Создание проекта (требует аутентификацию)
- `GET /api/v1/projects/{projectId}` - Получение проекта
- `GET /api/v1/projects/{projectId}/tasks` - Список задач проекта
- `POST /api/v1/projects/{projectId}/tasks` - Создание задачи (требует аутентификацию)
- `GET /api/v1/tasks/{taskId}` - Получение задачи
- `PUT /api/v1/tasks/{taskId}` - Обновление задачи

#### API v2
- Все endpoints v1 плюс:
- `GET /api/v2/projects/{projectId}/tasks?status=todo&priority=1` - Фильтрация задач
- `POST /api/v2/projects/{projectId}/tasks` - Создание задачи с приоритетом
- `PUT /api/v2/tasks/{taskId}` - Обновление задачи с приоритетом

### Аутентификация

Для операций записи (POST, PUT) требуется JWT токен в заголовке:
```
Authorization: Bearer demo-token
```

### Rate Limiting

- 60 запросов в минуту на IP адрес
- Заголовки ответа: `X-Limit-Remaining`, `X-RateLimit-Reset`

### Идемпотентность

Для POST запросов поддерживается идемпотентность через заголовок:
```
Idempotency-Key: unique-key-123
```

## Обоснование выбора аутентификации

### Выбранный подход: JWT Bearer Token

В данном проекте реализована упрощенная JWT аутентификация с использованием Bearer токенов по следующим причинам:

#### Преимущества JWT:

1. **Stateless архитектура**: JWT токены содержат всю необходимую информацию, что позволяет масштабировать приложение горизонтально без необходимости синхронизации состояния сессий между серверами.

2. **Простота интеграции**: JWT является стандартом RFC 7519 и поддерживается большинством клиентских библиотек и фреймворков.

3. **Безопасность**: Токены подписываются криптографически, что обеспечивает целостность данных и предотвращает подделку.

4. **Гибкость**: JWT может содержать дополнительные claims (роли, права доступа, время истечения), что упрощает реализацию авторизации.

#### Особенности реализации:

1. **Дифференцированная защита**: 
   - GET запросы доступны без аутентификации (публичное чтение)
   - POST/PUT запросы требуют валидный токен (защищенная запись)

2. **Демо-режим**: Для простоты демонстрации используется фиксированный токен `demo-token`. В production среде следует:
   - Использовать надежную библиотеку для генерации и верификации JWT
   - Реализовать proper key management (RSA/ECDSA ключи)
   - Добавить время истечения токенов
   - Реализовать refresh token механизм

#### Альтернативные подходы:

1. **Session-based аутентификация**: Требует server-side storage, усложняет масштабирование
2. **API Keys**: Менее безопасно, сложнее ротация ключей
3. **OAuth 2.0**: Избыточно для простого API, требует дополнительной инфраструктуры

### Рекомендации для production:

```go
// Пример улучшенной JWT реализации
func jwtAuthMiddleware(secretKey []byte) func(http.Handler) http.Handler {
    return func(next http.Handler) http.Handler {
        return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
            token := extractBearerToken(r)
            if token == "" {
                // Allow read-only access
                next.ServeHTTP(w, r)
                return
            }
            
            claims, err := validateJWT(token, secretKey)
            if err != nil {
                http.Error(w, "Invalid token", http.StatusUnauthorized)
                return
            }
            
            ctx := context.WithValue(r.Context(), "user", claims)
            next.ServeHTTP(w, r.WithContext(ctx))
        })
    }
}
```

## Тестирование

### Примеры запросов

1. Создание проекта:
```bash
curl -X POST http://localhost:8080/api/v1/projects \
  -H "Authorization: Bearer demo-token" \
  -H "Content-Type: application/json" \
  -d '{"name": "Test Project", "description": "Test description"}'
```

2. Получение списка проектов:
```bash
curl http://localhost:8080/api/v1/projects
```

3. Создание задачи с приоритетом (v2):
```bash
curl -X POST http://localhost:8080/api/v2/projects/{projectId}/tasks \
  -H "Authorization: Bearer demo-token" \
  -H "Content-Type: application/json" \
  -d '{"title": "Important task", "description": "Description", "priority": 1}'
```

## Мониторинг

Приложение выводит логи запросов в stdout:
```
2025/09/14 15:05:09 127.0.0.1:54321 GET /api/v1/projects
```

Rate limiting информация доступна в HTTP заголовках ответа. 
