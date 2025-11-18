# Expense Tracker API

Back-end API для мобильного приложения учета расходов на Go с PostgreSQL.

## Возможности

- ✅ CRUD операции для расходов
- ✅ Фильтрация расходов по категории и дате
- ✅ Статистика расходов по категориям и периодам
- ✅ Управление категориями расходов
- ✅ Docker Compose для легкого запуска

## Технологии

- **Go 1.21** - язык программирования
- **Gin** - HTTP web framework
- **GORM** - ORM для работы с базой данных
- **PostgreSQL** - база данных
- **Docker** - контейнеризация

## Структура проекта

```
.
├── api/
│   └── openapi.yaml          # OpenAPI спецификация
├── cmd/
│   └── server/
│       └── main.go           # Точка входа приложения
├── internal/
│   ├── database/
│   │   └── db.go             # Подключение к БД и миграции
│   ├── handlers/
│   │   ├── expense.go        # Обработчики для расходов
│   │   └── category.go       # Обработчики для категорий
│   ├── models/
│   │   ├── expense.go        # Модель расхода
│   │   ├── category.go       # Модель категории
│   │   └── statistics.go     # Модель статистики
│   └── repository/
│       ├── expense.go        # Репозиторий расходов
│       └── category.go       # Репозиторий категорий
├── pkg/
│   └── errors/
│       └── errors.go         # Обработка ошибок
├── docker-compose.yml        # Конфигурация Docker Compose
├── Dockerfile                # Образ для API
├── go.mod                    # Зависимости Go
└── README.md                 # Документация

```

## Быстрый старт

### С Docker Compose (рекомендуется)

1. Скопируйте файл `.env.example` в `.env` (опционально, если хотите изменить настройки):
   ```bash
   cp .env.example .env
   ```

2. Запустите приложение:
   ```bash
   docker-compose up --build
   ```

3. API будет доступен по адресу: `http://localhost:8080/api`

### Без Docker

1. Установите PostgreSQL и создайте базу данных:
   ```sql
   CREATE DATABASE expense_tracker;
   ```

2. Скопируйте `.env.example` в `.env` и настройте переменные окружения:
   ```bash
   cp .env.example .env
   ```

3. Установите зависимости:
   ```bash
   go mod download
   ```

4. Запустите приложение:
   ```bash
   go run cmd/server/main.go
   ```

## API Endpoints

### Расходы

- `GET /api/expenses` - Получить список расходов (с фильтрацией)
- `POST /api/expenses` - Создать новый расход
- `GET /api/expenses/{id}` - Получить расход по ID
- `PUT /api/expenses/{id}` - Обновить расход
- `DELETE /api/expenses/{id}` - Удалить расход
- `GET /api/expenses/statistics` - Получить статистику расходов

### Категории

- `GET /api/categories` - Получить список категорий

## Примеры запросов

### Создать расход

```bash
curl -X POST http://localhost:8080/api/expenses \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 1500.50,
    "category": "Продукты",
    "description": "Покупка продуктов на неделю",
    "date": "2024-01-15T10:30:00Z"
  }'
```

### Получить расходы с фильтрацией

```bash
curl "http://localhost:8080/api/expenses?category=Продукты&startDate=2024-01-01T00:00:00Z&endDate=2024-01-31T23:59:59Z"
```

### Получить статистику

```bash
curl "http://localhost:8080/api/expenses/statistics?period=month&startDate=2024-01-01T00:00:00Z&endDate=2024-01-31T23:59:59Z"
```

## Переменные окружения

- `DB_HOST` - хост базы данных (по умолчанию: `localhost`)
- `DB_USER` - пользователь базы данных (по умолчанию: `expense_user`)
- `DB_PASSWORD` - пароль базы данных (по умолчанию: `expense_password`)
- `DB_NAME` - имя базы данных (по умолчанию: `expense_tracker`)
- `DB_PORT` - порт базы данных (по умолчанию: `5432`)
- `PORT` - порт API сервера (по умолчанию: `8080`)

## OpenAPI спецификация

Полная спецификация API доступна в файле `api/openapi.yaml`.

