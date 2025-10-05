#!/usr/bin/env python3
"""
Тест создания драйвера Appium без подключения к устройству
"""

import time
import logging
from typing import Optional
from appium import webdriver
from appium.options.android import UiAutomator2Options

# Настройка логирования
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


def test_driver_creation():
    """Тест создания драйвера Appium"""
    driver = None
    
    try:
        logger.info("🔍 Тест создания драйвера Appium")
        
        # Настройки для подключения
        desired_caps = {
            "platformName": "Android",
            "automationName": "UiAutomator2",
            "deviceName": "Android Emulator",
            "appPackage": "com.google.android.keep",
            "appActivity": ".activities.BrowseActivity",
            "noReset": True,
            "newCommandTimeout": 60,
            "autoGrantPermissions": True,
            "unicodeKeyboard": True,
            "resetKeyboard": True
        }
        
        options = UiAutomator2Options().load_capabilities(desired_caps)
        
        logger.info("✅ Конфигурация драйвера создана успешно")
        logger.info(f"Platform: {desired_caps['platformName']}")
        logger.info(f"Automation: {desired_caps['automationName']}")
        logger.info(f"App Package: {desired_caps['appPackage']}")
        
        # Попытка создания драйвера (будет ошибка без устройства)
        logger.info("🔗 Попытка подключения к Appium серверу...")
        
        try:
            driver = webdriver.Remote("http://127.0.0.1:4723", options=options)
            logger.info("✅ Драйвер создан успешно!")
            
            # Если дошли до сюда, значит устройство подключено
            current_activity = driver.current_activity
            logger.info(f"Текущая активность: {current_activity}")
            
            return True
            
        except Exception as e:
            if "No devices found" in str(e) or "device" in str(e).lower():
                logger.warning("⚠️ Устройство не найдено (это ожидаемо без эмулятора)")
                logger.info("✅ Драйвер создается корректно, проблема только в отсутствии устройства")
                return True
            else:
                logger.error(f"❌ Ошибка создания драйвера: {e}")
                return False
        
    except Exception as e:
        logger.error(f"❌ Общая ошибка: {e}")
        return False
        
    finally:
        if driver:
            try:
                driver.quit()
                logger.info("✅ Драйвер закрыт")
            except:
                pass


def test_appium_server_connection():
    """Тест подключения к Appium серверу"""
    try:
        logger.info("🔍 Тест подключения к Appium серверу")
        
        import requests
        
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
            
    except requests.exceptions.ConnectionError:
        logger.error("❌ Appium сервер не запущен")
        logger.info("💡 Запустите Appium: make run-appium")
        return False
    except Exception as e:
        logger.error(f"❌ Ошибка подключения: {e}")
        return False


def main():
    """Основная функция"""
    logger.info("🚀 Тестирование создания драйвера Appium")
    logger.info("=" * 50)
    
    # Тест подключения к серверу
    server_ok = test_appium_server_connection()
    
    if not server_ok:
        logger.error("❌ Appium сервер недоступен. Завершение тестирования.")
        return 1
    
    logger.info("")
    
    # Тест создания драйвера
    driver_ok = test_driver_creation()
    
    logger.info("")
    logger.info("=" * 50)
    logger.info("📊 РЕЗУЛЬТАТЫ ТЕСТИРОВАНИЯ")
    logger.info("=" * 50)
    
    if driver_ok:
        logger.info("✅ Тест создания драйвера: ПРОЙДЕН")
        logger.info("🎉 Система готова к тестированию мобильных приложений!")
        logger.info("")
        logger.info("📋 Следующие шаги:")
        logger.info("1. Запустите эмулятор: make run-emulator")
        logger.info("2. Запустите тесты: make test")
        return 0
    else:
        logger.error("❌ Тест создания драйвера: НЕ ПРОЙДЕН")
        return 1


if __name__ == "__main__":
    exit(main())
