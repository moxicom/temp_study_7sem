# 📱 Тестирование мобильных приложений

Проект для изучения автоматизированного тестирования мобильных приложений с использованием Appium и Python.

## 🎯 Цели проекта

- Получить базовый опыт в тестировании мобильных приложений на эмуляторе
- Изучить ручное тестирование с использованием чек-листов
- Освоить автоматизированное тестирование с Appium
- Написать тесты для простых действий в мобильном приложении

## 📋 Содержание

1. **Ручное тестирование** - Чек-лист для тестирования мобильных приложений
2. **Автоматизированное тестирование** - Скрипты на Python с использованием Appium
3. **Настройка окружения** - Автоматизированные скрипты установки

## 🛠 Технологии

- **Python 3** - Основной язык программирования
- **Appium** - Фреймворк для автоматизации мобильных приложений
- **Selenium WebDriver** - Базовый драйвер для взаимодействия с элементами
- **Android SDK** - Для работы с Android эмулятором
- **UiAutomator2** - Драйвер для Android приложений

## 📁 Структура проекта

```
l7/
├── README.md                    # Документация проекта
├── Makefile                     # Автоматизация команд
├── requirements.txt             # Python зависимости
├── setup_environment.sh         # Скрипт настройки окружения
├── mobile_testing_checklist.md  # Чек-лист ручного тестирования
├── test_notes_app.py           # Основной скрипт автоматизированного тестирования
├── test_config.py              # Конфигурация тестов
├── .venv/                      # Виртуальное окружение Python (создается автоматически)
├── screenshots/                # Скриншоты тестов (создается автоматически)
├── logs/                       # Логи тестирования (создается автоматически)
└── reports/                    # Отчеты тестирования (создается автоматически)
```

## 🚀 Быстрый старт

### 1. Клонирование и переход в директорию

```bash
cd /home/ejs/programming/study/temp_study_7sem/qa/l7
```

### 2. Настройка окружения

```bash
# Автоматическая настройка (рекомендуется)
make setup

# Или ручная настройка
./setup_environment.sh
```

### 3. Запуск тестирования

```bash
# Полный цикл (запуск эмулятора + Appium + тесты)
make run-tests

# Или пошагово:
make run-emulator  # Запуск эмулятора
make run-appium    # Запуск Appium сервера
make test          # Запуск тестов
```

### 4. Остановка сервисов

```bash
make stop-all
```

## 📖 Подробные инструкции

### Настройка окружения

#### Системные требования

- **ОС**: Ubuntu/Debian (тестировано на Ubuntu 22.04+)
- **RAM**: Минимум 4GB (рекомендуется 8GB+)
- **Дисковое пространство**: Минимум 10GB свободного места
- **Процессор**: x86_64

#### Автоматическая установка

```bash
# Запуск автоматического скрипта установки
./setup_environment.sh
```

Этот скрипт установит:
- Java 17 (OpenJDK)
- Node.js (LTS версия)
- Python 3 и pip
- Android SDK
- Android эмулятор
- Appium и драйверы
- Python зависимости

#### Ручная установка

```bash
# 1. Установка системных зависимостей
sudo apt update
sudo apt install -y openjdk-17-jdk nodejs npm python3-venv python3-pip

# 2. Обновление Node.js
sudo npm install -g n
sudo n lts

# 3. Создание виртуального окружения Python
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt

# 4. Установка Appium
sudo npm install -g appium --unsafe-perm=true
appium driver install uiautomator2

# 5. Установка Android SDK (если не установлен)
# Следуйте инструкциям в setup_environment.sh
```

### Работа с эмулятором

#### Создание AVD (Android Virtual Device)

```bash
# Список доступных образов системы
sdkmanager --list | grep "system-images"

# Установка образа системы
sdkmanager "system-images;android-34;google_apis;x86_64"

# Создание AVD
avdmanager create avd -n "Medium_Phone_API_36.0" -k "system-images;android-34;google_apis;x86_64"

# Список созданных AVD
emulator -list-avds
```

#### Запуск эмулятора

```bash
# Запуск с GUI
emulator -avd Medium_Phone_API_36.0

# Запуск без GUI (для CI/CD)
emulator -avd Medium_Phone_API_36.0 -no-audio -no-window
```

#### Проверка подключения

```bash
# Проверка устройств
adb devices

# Проверка статуса загрузки
adb shell getprop sys.boot_completed
```

### Работа с Appium

#### Запуск Appium сервера

```bash
# Запуск на стандартном порту
appium

# Запуск на кастомном порту
appium --port 4724

# Запуск с логами
appium --log-level debug
```

#### Проверка драйверов

```bash
# Список установленных драйверов
appium driver list

# Установка дополнительных драйверов
appium driver install xcuitest  # Для iOS
```

## 🧪 Тестирование

### Ручное тестирование

Используйте чек-лист из файла `mobile_testing_checklist.md` для проведения ручного тестирования.

#### Основные категории тестирования:

1. **Функциональное тестирование**
   - Запуск приложения
   - Основные функции
   - Создание и редактирование заметок

2. **Тестирование жестов**
   - Тап, долгое нажатие
   - Свайпы, пинч
   - Мультитач

3. **Тестирование ориентации**
   - Поворот экрана
   - Адаптивность интерфейса

4. **Тестирование прерываний**
   - Входящие звонки
   - SMS и уведомления
   - Системные прерывания

5. **Тестирование производительности**
   - Скорость работы
   - Нагрузочное тестирование

### Автоматизированное тестирование

#### Структура тестов

Основной файл тестирования: `test_notes_app.py`

```python
class MobileAppTester:
    def test_app_launch(self)      # Тест запуска приложения
    def test_create_note(self)     # Тест создания заметки
    def test_gestures(self)        # Тест жестов
    def test_screen_rotation(self) # Тест поворота экрана
```

#### Запуск тестов

```bash
# Активация виртуального окружения
source .venv/bin/activate

# Запуск тестов
python test_notes_app.py
```

#### Результаты тестирования

- **Логи**: `test_results.log`
- **Скриншоты**: `screenshots/` (при ошибках)
- **Отчеты**: Вывод в консоль и файл

## 📊 Мониторинг и отладка

### Проверка статуса сервисов

```bash
make status
```

### Просмотр логов

```bash
# Последние 50 строк логов
make logs

# Просмотр логов в реальном времени
tail -f test_results.log
```

### Отладка проблем

#### Эмулятор не запускается

```bash
# Проверка доступных AVD
emulator -list-avds

# Запуск с отладкой
emulator -avd Medium_Phone_API_36.0 -verbose

# Очистка данных эмулятора
emulator -avd Medium_Phone_API_36.0 -wipe-data
```

#### Appium не подключается

```bash
# Проверка статуса сервера
curl http://127.0.0.1:4723/status

# Перезапуск сервера
pkill -f appium
appium
```

#### Тесты не находят элементы

```bash
# Просмотр иерархии UI
adb shell uiautomator dump
adb pull /sdcard/window_dump.xml

# Инспектор элементов
appium-inspector
```

## 🔧 Полезные команды

### Makefile команды

```bash
make help          # Показать справку
make setup         # Полная настройка
make deps          # Установка зависимостей
make python        # Настройка Python
make appium        # Установка Appium
make run-emulator  # Запуск эмулятора
make run-appium    # Запуск Appium
make test          # Запуск тестов
make run-tests     # Полный цикл
make stop-all      # Остановка всех сервисов
make clean         # Очистка
make status        # Статус сервисов
make logs          # Просмотр логов
```

### ADB команды

```bash
adb devices                    # Список устройств
adb shell                     # Подключение к shell
adb install app.apk          # Установка APK
adb uninstall com.package    # Удаление приложения
adb logcat                   # Просмотр логов
adb shell am start -n com.package/.Activity  # Запуск приложения
```

### Appium команды

```bash
appium --version              # Версия Appium
appium driver list           # Список драйверов
appium driver install uiautomator2  # Установка драйвера
appium-inspector             # Инспектор элементов
```

## 📚 Дополнительные ресурсы

### Документация

- [Appium Documentation](http://appium.io/docs/en/about-appium/intro/)
- [Selenium Python Documentation](https://selenium-python.readthedocs.io/)
- [Android Developer Guide](https://developer.android.com/guide)

### Полезные инструменты

- **Appium Inspector** - GUI для инспекции элементов
- **Android Studio** - IDE для разработки Android приложений
- **Genymotion** - Альтернативный эмулятор Android

### Примеры тестов

```python
# Простой тест
def test_button_click(self):
    button = self.driver.find_element(AppiumBy.ID, "button_id")
    button.click()
    assert "success" in self.driver.page_source

# Тест с ожиданием
def test_with_wait(self):
    WebDriverWait(self.driver, 10).until(
        EC.presence_of_element_located((AppiumBy.ID, "element_id"))
    )
```

## 🐛 Решение проблем

### Частые ошибки

1. **"No devices found"**
   - Проверьте, что эмулятор запущен: `adb devices`
   - Убедитесь, что USB debugging включен

2. **"Appium server not running"**
   - Запустите Appium: `appium`
   - Проверьте порт: `curl http://127.0.0.1:4723/status`

3. **"Element not found"**
   - Используйте Appium Inspector для поиска правильных селекторов
   - Добавьте ожидания: `WebDriverWait`

4. **"Permission denied"**
   - Запустите с sudo: `sudo make setup`
   - Проверьте права на файлы: `chmod +x setup_environment.sh`

### Получение помощи

1. Проверьте логи: `make logs`
2. Проверьте статус: `make status`
3. Перезапустите сервисы: `make stop-all && make run-tests`

## 📝 Заключение

Этот проект предоставляет полный набор инструментов для изучения тестирования мобильных приложений:

- ✅ Чек-лист для ручного тестирования
- ✅ Автоматизированные скрипты настройки
- ✅ Готовые тесты на Python + Appium
- ✅ Подробная документация
- ✅ Инструменты для отладки

Используйте `make help` для получения списка доступных команд и начинайте тестирование!

