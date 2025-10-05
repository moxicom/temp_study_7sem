# 🚀 Быстрый старт - Мобильное тестирование

## ⚡ За 5 минут

### 1. Настройка окружения (один раз)

```bash
# Переход в директорию проекта
cd /home/ejs/programming/study/temp_study_7sem/qa/l7

# Автоматическая настройка всего окружения
make setup
```

### 2. Запуск тестирования

```bash
# Запуск всех тестов (эмулятор + Appium + тесты)
make run-tests
```

### 3. Остановка сервисов

```bash
# Остановка всех сервисов
make stop-all
```

## 🎯 Что тестируется

### Ручное тестирование
- **Чек-лист**: `mobile_testing_checklist.md`
- **Приложения**: Любое мобильное приложение (Notes, Calculator, etc.)

### Автоматизированное тестирование
- **Notes/Keep App**: Создание заметок, жесты, поворот экрана
- **Calculator App**: Базовые вычисления, функции очистки

## 📋 Доступные команды

```bash
make help                    # Показать все команды
make setup                   # Настройка окружения
make run-tests              # Полный цикл тестирования
make test                   # Только тесты заметок
make test-calculator        # Только тесты калькулятора
make test-all               # Все тесты
make status                 # Статус сервисов
make stop-all               # Остановка сервисов
make logs                   # Просмотр логов
```

## 🔧 Решение проблем

### Эмулятор не запускается
```bash
make stop-all
make run-emulator
```

### Appium не работает
```bash
make stop-all
make run-appium
```

### Ошибки в тестах
```bash
make logs
make status
```

## 📊 Результаты

- **Логи**: `test_results.log`, `calculator_test_results.log`
- **Скриншоты**: `screenshots/` (при ошибках)
- **Отчеты**: Вывод в консоль

## 🎓 Обучение

1. **Изучите чек-лист** - `mobile_testing_checklist.md`
2. **Запустите тесты** - `make run-tests`
3. **Изучите код** - `test_notes_app.py`, `test_calculator_app.py`
4. **Модифицируйте тесты** - Добавьте свои тесты

## 📚 Дополнительно

- **Подробная документация**: `README.md`
- **Настройка окружения**: `setup_environment.sh`
- **Конфигурация**: `test_config.py`

---

**Готово! Теперь вы можете тестировать мобильные приложения! 🎉**

