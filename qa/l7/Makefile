# Конфигурация
VENV_DIR := .venv
PYTHON := python3
AVD_NAME := Medium_Phone_API_36.0
APPIUM_PORT := 4723

# Цвета для вывода
RED := \033[0;31m
GREEN := \033[0;32m
YELLOW := \033[1;33m
BLUE := \033[0;34m
NC := \033[0m # No Color

.PHONY: all help setup deps python appium test clean run-emulator run-appium run-tests stop-all

# Основные цели
all: setup

help: ## Показать справку
	@echo "$(BLUE)🚀 Мобильное тестирование - Доступные команды$(NC)"
	@echo "=================================================="
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "$(GREEN)%-20s$(NC) %s\n", $$1, $$2}' $(MAKEFILE_LIST)

setup: deps python appium ## Полная настройка окружения
	@echo "$(GREEN)✅ Настройка завершена!$(NC)"
	@echo "$(YELLOW)📋 Следующие шаги:$(NC)"
	@echo "1. make run-emulator  # Запуск эмулятора"
	@echo "2. make run-appium    # Запуск Appium сервера"
	@echo "3. make run-tests     # Запуск тестов"

deps: ## Установка системных зависимостей
	@echo "$(BLUE)📦 Установка системных зависимостей...$(NC)"
	sudo apt update
	sudo apt install -y openjdk-17-jdk nodejs npm python3-venv python3-pip wget unzip
	sudo npm install -g n
	sudo n lts
	@echo "$(GREEN)✅ Системные зависимости установлены$(NC)"

python: ## Настройка Python окружения
	@echo "$(BLUE)🐍 Настройка Python окружения...$(NC)"
	$(PYTHON) -m venv $(VENV_DIR)
	$(VENV_DIR)/bin/pip install --upgrade pip
	$(VENV_DIR)/bin/pip install -r requirements.txt
	@echo "$(GREEN)✅ Python окружение настроено$(NC)"

appium: ## Установка Appium и драйверов
	@echo "$(BLUE)📱 Установка Appium...$(NC)"
	sudo npm install -g appium --unsafe-perm=true
	appium driver install uiautomator2
	appium driver install xcuitest
	@echo "$(GREEN)✅ Appium установлен$(NC)"

test: ## Запуск тестов часов (требует запущенный Appium и эмулятор)
	@echo "$(BLUE)🧪 Запуск тестов часов...$(NC)"
	@if ! pgrep -f "appium" > /dev/null; then \
		echo "$(RED)❌ Appium сервер не запущен! Запустите: make run-appium$(NC)"; \
		exit 1; \
	fi
	@if ! adb devices | grep -q "emulator"; then \
		echo "$(RED)❌ Эмулятор не запущен! Запустите: make run-emulator$(NC)"; \
		exit 1; \
	fi
	$(VENV_DIR)/bin/python test_clock.py
	@echo "$(GREEN)✅ Тесты часов завершены$(NC)"

run-emulator: ## Запуск Android эмулятора
	@echo "$(BLUE)📱 Запуск Android эмулятора...$(NC)"
	@if pgrep -f "emulator" > /dev/null; then \
		echo "$(YELLOW)⚠️  Эмулятор уже запущен$(NC)"; \
	else \
		emulator -avd $(AVD_NAME) -no-audio -no-window & \
		echo "$(GREEN)✅ Эмулятор запускается...$(NC)"; \
		echo "$(YELLOW)⏳ Ожидание загрузки эмулятора (это может занять несколько минут)...$(NC)"; \
		timeout 300 bash -c 'until adb shell getprop sys.boot_completed 2>/dev/null | grep -q "1"; do sleep 5; done' || echo "$(RED)❌ Таймаут загрузки эмулятора$(NC)"; \
		echo "$(GREEN)✅ Эмулятор готов к работе$(NC)"; \
	fi

run-appium: ## Запуск Appium сервера
	@echo "$(BLUE)🚀 Запуск Appium сервера...$(NC)"
	@if pgrep -f "appium" > /dev/null; then \
		echo "$(YELLOW)⚠️  Appium сервер уже запущен$(NC)"; \
	else \
		appium --port $(APPIUM_PORT) & \
		echo "$(GREEN)✅ Appium сервер запущен на порту $(APPIUM_PORT)$(NC)"; \
		sleep 3; \
	fi

run-tests: run-emulator run-appium ## Запуск полного цикла тестирования часов
	@echo "$(BLUE)🧪 Запуск полного цикла тестирования часов...$(NC)"
	sleep 10  # Даем время на запуск сервисов
	$(VENV_DIR)/bin/python test_clock.py
	@echo "$(GREEN)✅ Полный цикл тестирования часов завершен$(NC)"

stop-all: ## Остановка всех сервисов
	@echo "$(BLUE)🛑 Остановка всех сервисов...$(NC)"
	@pkill -f "appium" && echo "$(GREEN)✅ Appium сервер остановлен$(NC)" || echo "$(YELLOW)⚠️  Appium сервер не был запущен$(NC)"
	@pkill -f "emulator" && echo "$(GREEN)✅ Эмулятор остановлен$(NC)" || echo "$(YELLOW)⚠️  Эмулятор не был запущен$(NC)"
	@adb kill-server && echo "$(GREEN)✅ ADB сервер остановлен$(NC)" || echo "$(YELLOW)⚠️  ADB сервер не был запущен$(NC)"

clean: stop-all ## Очистка и остановка всех сервисов
	@echo "$(BLUE)🧹 Очистка временных файлов...$(NC)"
	rm -rf __pycache__ *.pyc .pytest_cache
	rm -f test_results.log
	@echo "$(GREEN)✅ Очистка завершена$(NC)"

status: ## Показать статус сервисов
	@echo "$(BLUE)📊 Статус сервисов$(NC)"
	@echo "==================="
	@if pgrep -f "appium" > /dev/null; then \
		echo "$(GREEN)✅ Appium сервер: ЗАПУЩЕН$(NC)"; \
	else \
		echo "$(RED)❌ Appium сервер: ОСТАНОВЛЕН$(NC)"; \
	fi
	@if pgrep -f "emulator" > /dev/null; then \
		echo "$(GREEN)✅ Эмулятор: ЗАПУЩЕН$(NC)"; \
		adb devices | grep emulator || echo "$(YELLOW)⚠️  Эмулятор запускается...$(NC)"; \
	else \
		echo "$(RED)❌ Эмулятор: ОСТАНОВЛЕН$(NC)"; \
	fi
	@if adb devices | grep -q "device"; then \
		echo "$(GREEN)✅ ADB: УСТРОЙСТВА ОБНАРУЖЕНЫ$(NC)"; \
		adb devices; \
	else \
		echo "$(YELLOW)⚠️  ADB: Устройства не обнаружены$(NC)"; \
	fi

install-apk: ## Установка APK на эмулятор (требует путь к APK)
	@if [ -z "$(APK_PATH)" ]; then \
		echo "$(RED)❌ Укажите путь к APK: make install-apk APK_PATH=/path/to/app.apk$(NC)"; \
		exit 1; \
	fi
	@echo "$(BLUE)📱 Установка APK: $(APK_PATH)$(NC)"
	adb install "$(APK_PATH)"
	@echo "$(GREEN)✅ APK установлен$(NC)"

logs: ## Показать логи тестирования
	@if [ -f "test_results.log" ]; then \
		echo "$(BLUE)📋 Логи тестирования:$(NC)"; \
		tail -50 test_results.log; \
	else \
		echo "$(YELLOW)⚠️  Файл логов не найден$(NC)"; \
	fi

# Автоматическая помощь
.DEFAULT_GOAL := help
