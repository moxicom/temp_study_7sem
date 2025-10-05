#!/bin/bash

# Скрипт для настройки окружения для тестирования мобильных приложений
# Использование: ./setup_environment.sh

set -e  # Остановка при ошибке

echo "🚀 Настройка окружения для тестирования мобильных приложений"
echo "============================================================"

# Проверка прав администратора
if [ "$EUID" -eq 0 ]; then
    echo "⚠️  Запуск с правами root. Продолжаем..."
fi

# Обновление пакетов
echo "📦 Обновление списка пакетов..."
sudo apt update

# Установка Java (необходима для Android SDK)
echo "☕ Установка Java..."
sudo apt install -y openjdk-17-jdk
java -version

# Установка Node.js и npm
echo "📦 Установка Node.js и npm..."
sudo apt install -y nodejs npm
node --version
npm --version

# Обновление Node.js до последней LTS версии
echo "🔄 Обновление Node.js до LTS версии..."
sudo npm install -g n
sudo n lts

# Установка Python и venv
echo "🐍 Установка Python и venv..."
sudo apt install -y python3 python3-venv python3-pip

# Создание виртуального окружения Python
echo "🔧 Создание виртуального окружения Python..."
python3 -m venv .venv
source .venv/bin/activate

# Установка Python пакетов
echo "📚 Установка Python пакетов..."
pip install --upgrade pip
pip install -r requirements.txt

# Установка Appium
echo "📱 Установка Appium..."
sudo npm install -g appium --unsafe-perm=true

# Установка Appium драйверов
echo "🔌 Установка Appium драйверов..."
appium driver install uiautomator2
appium driver install xcuitest

# Установка Android SDK (если не установлен)
echo "🤖 Проверка Android SDK..."
if ! command -v adb &> /dev/null; then
    echo "⚠️  Android SDK не найден. Установка..."
    
    # Создание директории для Android SDK
    ANDROID_HOME="$HOME/Android/Sdk"
    mkdir -p "$ANDROID_HOME"
    
    # Загрузка Android SDK command line tools
    echo "📥 Загрузка Android SDK command line tools..."
    cd /tmp
    wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
    unzip commandlinetools-linux-11076708_latest.zip -d "$ANDROID_HOME"
    
    # Настройка переменных окружения
    echo "🔧 Настройка переменных окружения..."
    echo "export ANDROID_HOME=$ANDROID_HOME" >> ~/.bashrc
    echo "export PATH=\$PATH:\$ANDROID_HOME/cmdline-tools/latest/bin:\$ANDROID_HOME/platform-tools" >> ~/.bashrc
    source ~/.bashrc
    
    # Принятие лицензий Android SDK
    echo "📋 Принятие лицензий Android SDK..."
    yes | "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" --licenses
    
    # Установка необходимых компонентов
    echo "📦 Установка компонентов Android SDK..."
    "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" "platform-tools" "platforms;android-34" "build-tools;34.0.0"
    
    cd -  # Возврат в исходную директорию
else
    echo "✅ Android SDK уже установлен"
fi

# Проверка установки эмулятора
echo "📱 Проверка Android эмулятора..."
if ! command -v emulator &> /dev/null; then
    echo "⚠️  Android эмулятор не найден. Установка..."
    "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" "emulator"
    "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" "system-images;android-34;google_apis;x86_64"
    "$ANDROID_HOME/cmdline-tools/latest/bin/avdmanager" create avd -n "Medium_Phone_API_36.0" -k "system-images;android-34;google_apis;x86_64"
else
    echo "✅ Android эмулятор уже установлен"
fi

# Создание необходимых директорий
echo "📁 Создание директорий..."
mkdir -p screenshots logs reports

# Проверка установки
echo ""
echo "🔍 Проверка установки..."
echo "=========================="

echo "Java:"
java -version

echo ""
echo "Node.js:"
node --version

echo ""
echo "npm:"
npm --version

echo ""
echo "Python:"
python3 --version

echo ""
echo "Appium:"
appium --version

echo ""
echo "Android SDK:"
if command -v adb &> /dev/null; then
    adb version
else
    echo "❌ Android SDK не найден"
fi

echo ""
echo "✅ Настройка окружения завершена!"
echo ""
echo "📋 Следующие шаги:"
echo "1. Перезагрузите терминал или выполните: source ~/.bashrc"
echo "2. Запустите эмулятор: emulator -avd Medium_Phone_API_36.0 &"
echo "3. Запустите Appium сервер: appium"
echo "4. В новом терминале запустите тесты: python3 test_notes_app.py"
echo ""
echo "🔧 Полезные команды:"
echo "- Список эмуляторов: emulator -list-avds"
echo "- Список устройств: adb devices"
echo "- Остановка эмулятора: adb emu kill"
echo "- Остановка Appium: Ctrl+C"

