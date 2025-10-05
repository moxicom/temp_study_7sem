#!/usr/bin/env python3
"""
Конфигурация для тестирования мобильных приложений
"""

import os
import time
from typing import Dict, Any

class TestConfig:
    """Конфигурация тестов"""
    
    # Appium настройки
    APPIUM_SERVER_URL = "http://127.0.0.1:4723"
    APPIUM_SERVER_TIMEOUT = 60
    
    # Android настройки
    ANDROID_CONFIG = {
        "platformName": "Android",
        "automationName": "UiAutomator2",
        "deviceName": "Android Emulator",
        "newCommandTimeout": 60,
        "autoGrantPermissions": True,
        "unicodeKeyboard": True,
        "resetKeyboard": True,
        "noReset": True
    }
    
    # Настройки для разных приложений
    APPS_CONFIG = {
        "google_keep": {
            "appPackage": "com.google.android.keep",
            "appActivity": ".activities.BrowseActivity",
            "description": "Google Keep - приложение для заметок"
        },
        "notes": {
            "appPackage": "com.google.android.keep",  # Альтернатива
            "appActivity": ".activities.BrowseActivity",
            "description": "Стандартное приложение заметок"
        },
        "calculator": {
            "appPackage": "com.google.android.calculator",
            "appActivity": "com.android.calculator2.Calculator",
            "description": "Калькулятор"
        }
    }
    
    # Настройки эмулятора
    EMULATOR_CONFIG = {
        "avd_name": "Medium_Phone_API_36.0",
        "boot_timeout": 300,  # 5 минут
        "startup_timeout": 60
    }
    
    # Настройки логирования
    LOGGING_CONFIG = {
        "level": "INFO",
        "format": "%(asctime)s - %(levelname)s - %(message)s",
        "file": "test_results.log"
    }
    
    # Настройки тестирования
    TEST_CONFIG = {
        "wait_timeout": 10,
        "implicit_wait": 5,
        "screenshot_on_failure": True,
        "screenshot_dir": "screenshots"
    }
    
    @classmethod
    def get_app_config(cls, app_name: str = "google_keep") -> Dict[str, Any]:
        """Получить конфигурацию для конкретного приложения"""
        if app_name not in cls.APPS_CONFIG:
            raise ValueError(f"Неизвестное приложение: {app_name}")
        
        config = cls.ANDROID_CONFIG.copy()
        config.update(cls.APPS_CONFIG[app_name])
        return config
    
    @classmethod
    def setup_directories(cls):
        """Создать необходимые директории"""
        directories = [
            cls.TEST_CONFIG["screenshot_dir"],
            "logs",
            "reports"
        ]
        
        for directory in directories:
            os.makedirs(directory, exist_ok=True)
    
    @classmethod
    def get_screenshot_path(cls, test_name: str) -> str:
        """Получить путь для скриншота"""
        timestamp = int(time.time())
        filename = f"{test_name}_{timestamp}.png"
        return os.path.join(cls.TEST_CONFIG["screenshot_dir"], filename)
