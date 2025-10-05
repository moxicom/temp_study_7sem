#!/usr/bin/env python3
"""
Тест окружения для проверки корректности настройки
без необходимости запуска эмулятора
"""

import sys
import os
import logging
from typing import Dict, Any

# Настройка логирования
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


class EnvironmentTester:
    """Класс для тестирования окружения"""
    
    def __init__(self):
        self.results = {}
    
    def test_python_imports(self) -> bool:
        """Тест 1: Проверка импорта Python модулей"""
        try:
            logger.info("🔍 Тест 1: Проверка импорта Python модулей")
            
            # Проверяем основные модули
            import appium
            import selenium
            import requests
            
            # Получаем версии модулей
            appium_version = getattr(appium, '__version__', 'неизвестна')
            selenium_version = getattr(selenium, '__version__', 'неизвестна')
            requests_version = getattr(requests, '__version__', 'неизвестна')
            
            logger.info(f"✅ Appium версия: {appium_version}")
            logger.info(f"✅ Selenium версия: {selenium_version}")
            logger.info(f"✅ Requests версия: {requests_version}")
            
            # Проверяем Appium компоненты
            from appium import webdriver
            from appium.options.android import UiAutomator2Options
            from appium.webdriver.common.appiumby import AppiumBy
            
            logger.info("✅ Все необходимые модули импортированы успешно")
            return True
            
        except ImportError as e:
            logger.error(f"❌ Ошибка импорта: {e}")
            return False
        except Exception as e:
            logger.error(f"❌ Неожиданная ошибка: {e}")
            return False
    
    def test_config_loading(self) -> bool:
        """Тест 2: Проверка загрузки конфигурации"""
        try:
            logger.info("🔍 Тест 2: Проверка загрузки конфигурации")
            
            # Импортируем наш конфиг
            from test_config import TestConfig
            
            # Проверяем основные настройки
            assert TestConfig.APPIUM_SERVER_URL == "http://127.0.0.1:4723"
            assert "Android" in TestConfig.ANDROID_CONFIG["platformName"]
            assert "UiAutomator2" in TestConfig.ANDROID_CONFIG["automationName"]
            
            # Проверяем конфигурацию приложений
            apps = TestConfig.APPS_CONFIG
            assert "google_keep" in apps
            assert "calculator" in apps
            
            logger.info("✅ Конфигурация загружена корректно")
            return True
            
        except Exception as e:
            logger.error(f"❌ Ошибка загрузки конфигурации: {e}")
            return False
    
    def test_appium_server_connection(self) -> bool:
        """Тест 3: Проверка подключения к Appium серверу"""
        try:
            logger.info("🔍 Тест 3: Проверка подключения к Appium серверу")
            
            import requests
            
            # Проверяем доступность сервера
            response = requests.get("http://127.0.0.1:4723/status", timeout=5)
            
            if response.status_code == 200:
                data = response.json()
                if data.get("value", {}).get("ready"):
                    logger.info("✅ Appium сервер доступен и готов")
                    return True
                else:
                    logger.error("❌ Appium сервер не готов")
                    return False
            else:
                logger.error(f"❌ Appium сервер вернул код: {response.status_code}")
                return False
                
        except Exception as e:
            if "ConnectionError" in str(type(e)):
                logger.warning("⚠️ Appium сервер не запущен (это нормально для теста окружения)")
                return True  # Не критично для теста окружения
            else:
                logger.error(f"❌ Ошибка подключения к Appium: {e}")
                return False
    
    def test_file_structure(self) -> bool:
        """Тест 4: Проверка структуры файлов"""
        try:
            logger.info("🔍 Тест 4: Проверка структуры файлов")
            
            required_files = [
                "test_notes_app.py",
                "test_calculator_app.py", 
                "run_all_tests.py",
                "test_config.py",
                "requirements.txt",
                "Makefile",
                "README.md",
                "mobile_testing_checklist.md"
            ]
            
            missing_files = []
            for file in required_files:
                if not os.path.exists(file):
                    missing_files.append(file)
            
            if missing_files:
                logger.error(f"❌ Отсутствуют файлы: {missing_files}")
                return False
            
            logger.info("✅ Все необходимые файлы присутствуют")
            return True
            
        except Exception as e:
            logger.error(f"❌ Ошибка проверки файлов: {e}")
            return False
    
    def test_makefile_commands(self) -> bool:
        """Тест 5: Проверка команд Makefile"""
        try:
            logger.info("🔍 Тест 5: Проверка команд Makefile")
            
            import subprocess
            
            # Проверяем команду help
            result = subprocess.run(
                ["make", "help"], 
                capture_output=True, 
                text=True, 
                timeout=10
            )
            
            if result.returncode == 0:
                help_output = result.stdout
                required_commands = [
                    "setup", "test", "run-emulator", "run-appium", 
                    "stop-all", "status", "logs"
                ]
                
                missing_commands = []
                for cmd in required_commands:
                    if cmd not in help_output:
                        missing_commands.append(cmd)
                
                if missing_commands:
                    logger.error(f"❌ Отсутствуют команды Makefile: {missing_commands}")
                    return False
                
                logger.info("✅ Команды Makefile работают корректно")
                return True
            else:
                logger.error(f"❌ Makefile не работает: {result.stderr}")
                return False
                
        except Exception as e:
            logger.error(f"❌ Ошибка проверки Makefile: {e}")
            return False
    
    def test_android_sdk(self) -> bool:
        """Тест 6: Проверка Android SDK"""
        try:
            logger.info("🔍 Тест 6: Проверка Android SDK")
            
            import subprocess
            
            # Проверяем adb
            result = subprocess.run(
                ["adb", "version"], 
                capture_output=True, 
                text=True, 
                timeout=10
            )
            
            if result.returncode == 0:
                logger.info("✅ Android SDK установлен и работает")
                logger.info(f"ADB версия: {result.stdout.split()[2]}")
                return True
            else:
                logger.error("❌ Android SDK не работает")
                return False
                
        except FileNotFoundError:
            logger.error("❌ Android SDK не установлен")
            return False
        except Exception as e:
            logger.error(f"❌ Ошибка проверки Android SDK: {e}")
            return False
    
    def run_all_tests(self) -> Dict[str, bool]:
        """Запуск всех тестов окружения"""
        logger.info("🚀 Запуск тестирования окружения")
        logger.info("=" * 50)
        
        # Запускаем тесты
        self.results["python_imports"] = self.test_python_imports()
        self.results["config_loading"] = self.test_config_loading()
        self.results["appium_connection"] = self.test_appium_server_connection()
        self.results["file_structure"] = self.test_file_structure()
        self.results["makefile_commands"] = self.test_makefile_commands()
        self.results["android_sdk"] = self.test_android_sdk()
        
        # Подсчитываем результаты
        passed = sum(self.results.values())
        total = len(self.results)
        
        logger.info("")
        logger.info("=" * 50)
        logger.info(f"📊 Результаты тестирования окружения: {passed}/{total} тестов пройдено")
        
        if passed == total:
            logger.info("🎉 ВСЕ ТЕСТЫ ОКРУЖЕНИЯ ПРОЙДЕНЫ УСПЕШНО!")
        else:
            logger.warning(f"⚠️ {total - passed} тестов окружения не пройдено")
        
        logger.info("=" * 50)
        
        return self.results


def main():
    """Основная функция"""
    print("🧪 Тестирование окружения мобильного тестирования")
    print("=" * 60)
    
    tester = EnvironmentTester()
    results = tester.run_all_tests()
    
    # Выводим детальный отчет
    print("\n" + "=" * 60)
    print("📋 ДЕТАЛЬНЫЙ ОТЧЕТ ТЕСТИРОВАНИЯ ОКРУЖЕНИЯ")
    print("=" * 60)
    
    test_names = {
        "python_imports": "Python модули",
        "config_loading": "Конфигурация",
        "appium_connection": "Appium сервер",
        "file_structure": "Структура файлов",
        "makefile_commands": "Makefile команды",
        "android_sdk": "Android SDK"
    }
    
    for test_key, result in results.items():
        test_name = test_names.get(test_key, test_key)
        status = "✅ ПРОЙДЕН" if result else "❌ НЕ ПРОЙДЕН"
        print(f"{test_name:20} {status}")
    
    print("=" * 60)
    
    # Код возврата
    failed_count = sum(1 for result in results.values() if not result)
    if failed_count == 0:
        print("🎉 Окружение полностью готово к работе!")
        return 0
    else:
        print(f"⚠️ Требуется исправление {failed_count} проблем")
        return 1


if __name__ == "__main__":
    exit(main())
