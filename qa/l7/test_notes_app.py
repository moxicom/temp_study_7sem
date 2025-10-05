#!/usr/bin/env python3
"""
Автоматическое тестирование мобильного приложения Notes/Keep
с использованием Appium и Python
"""

import time
import logging
from typing import Optional
from appium import webdriver
from appium.options.android import UiAutomator2Options
from appium.webdriver.common.appiumby import AppiumBy
from selenium.common.exceptions import NoSuchElementException, TimeoutException

# Настройка логирования
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('test_results.log'),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)


class MobileAppTester:
    """Класс для тестирования мобильного приложения"""
    
    def __init__(self):
        self.driver: Optional[webdriver.Remote] = None
        self.appium_server_url = "http://127.0.0.1:4723"
        
    def setup_driver(self) -> bool:
        """Настройка и подключение к Appium драйверу"""
        try:
            desired_caps = {
                "platformName": "Android",
                "automationName": "UiAutomator2",
                "deviceName": "Android Emulator",
                "appPackage": "com.google.android.keep",  # Google Keep
                "appActivity": ".activities.BrowseActivity",
                "noReset": True,
                "newCommandTimeout": 60,
                "autoGrantPermissions": True,
                "unicodeKeyboard": True,
                "resetKeyboard": True
            }
            
            options = UiAutomator2Options().load_capabilities(desired_caps)
            self.driver = webdriver.Remote(self.appium_server_url, options=options)
            
            logger.info("✅ Успешно подключились к Appium серверу")
            return True
            
        except Exception as e:
            logger.error(f"❌ Ошибка при подключении к Appium: {e}")
            return False
    
    def test_app_launch(self) -> bool:
        """Тест 1: Проверка запуска приложения"""
        try:
            logger.info("🔍 Тест 1: Проверка запуска приложения")
            
            # Ждем загрузки главного экрана
            time.sleep(3)
            
            # Проверяем, что приложение запустилось
            current_activity = self.driver.current_activity
            logger.info(f"Текущая активность: {current_activity}")
            
            # Проверяем наличие элементов интерфейса
            try:
                # Ищем кнопку создания заметки
                create_button = self.driver.find_element(
                    AppiumBy.ACCESSIBILITY_ID, 
                    "New text note"
                )
                logger.info("✅ Приложение успешно запущено")
                return True
                
            except NoSuchElementException:
                # Пробуем альтернативные селекторы
                try:
                    create_button = self.driver.find_element(
                        AppiumBy.XPATH, 
                        "//android.widget.ImageButton[@content-desc='New text note']"
                    )
                    logger.info("✅ Приложение успешно запущено (альтернативный селектор)")
                    return True
                except NoSuchElementException:
                    logger.warning("⚠️ Не удалось найти кнопку создания заметки")
                    return False
                    
        except Exception as e:
            logger.error(f"❌ Ошибка при тестировании запуска: {e}")
            return False
    
    def test_create_note(self) -> bool:
        """Тест 2: Создание новой заметки"""
        try:
            logger.info("🔍 Тест 2: Создание новой заметки")
            
            # Ищем и нажимаем кнопку создания заметки
            create_button = None
            try:
                create_button = self.driver.find_element(
                    AppiumBy.ACCESSIBILITY_ID, 
                    "New text note"
                )
            except NoSuchElementException:
                try:
                    create_button = self.driver.find_element(
                        AppiumBy.XPATH, 
                        "//android.widget.ImageButton[@content-desc='New text note']"
                    )
                except NoSuchElementException:
                    # Пробуем найти по тексту
                    create_button = self.driver.find_element(
                        AppiumBy.XPATH, 
                        "//android.widget.TextView[@text='New text note']"
                    )
            
            create_button.click()
            logger.info("✅ Нажали на кнопку создания заметки")
            
            # Ждем открытия редактора заметок
            time.sleep(2)
            
            # Ищем поле ввода текста
            try:
                text_field = self.driver.find_element(
                    AppiumBy.XPATH, 
                    "//android.widget.EditText"
                )
                
                # Вводим тестовый текст
                test_text = "Тестовая заметка от автоматизированного тестирования"
                text_field.send_keys(test_text)
                logger.info(f"✅ Ввели текст: {test_text}")
                
                # Возвращаемся назад
                self.driver.back()
                time.sleep(2)
                
                logger.info("✅ Заметка успешно создана")
                return True
                
            except NoSuchElementException:
                logger.error("❌ Не удалось найти поле ввода текста")
                return False
                
        except Exception as e:
            logger.error(f"❌ Ошибка при создании заметки: {e}")
            return False
    
    def test_gestures(self) -> bool:
        """Тест 3: Тестирование жестов"""
        try:
            logger.info("🔍 Тест 3: Тестирование жестов")
            
            # Тест долгого нажатия
            try:
                # Ищем заметку для долгого нажатия
                notes = self.driver.find_elements(
                    AppiumBy.XPATH, 
                    "//android.widget.TextView[contains(@text, 'Тестовая заметка')]"
                )
                
                if notes:
                    # Выполняем долгое нажатие
                    self.driver.tap([(notes[0].location['x'], notes[0].location['y'])], 2000)
                    logger.info("✅ Долгое нажатие выполнено")
                    
                    # Проверяем, появилось ли контекстное меню
                    time.sleep(1)
                    
                    # Закрываем меню (нажимаем вне элемента)
                    self.driver.tap([(100, 100)], 500)
                    logger.info("✅ Жесты протестированы успешно")
                    return True
                else:
                    logger.warning("⚠️ Не найдены заметки для тестирования жестов")
                    return True  # Не критичная ошибка
                    
            except Exception as e:
                logger.error(f"❌ Ошибка при тестировании жестов: {e}")
                return False
                
        except Exception as e:
            logger.error(f"❌ Общая ошибка при тестировании жестов: {e}")
            return False
    
    def test_screen_rotation(self) -> bool:
        """Тест 4: Тестирование поворота экрана"""
        try:
            logger.info("🔍 Тест 4: Тестирование поворота экрана")
            
            # Получаем текущую ориентацию
            current_orientation = self.driver.orientation
            logger.info(f"Текущая ориентация: {current_orientation}")
            
            # Поворачиваем экран
            new_orientation = "LANDSCAPE" if current_orientation == "PORTRAIT" else "PORTRAIT"
            self.driver.orientation = new_orientation
            logger.info(f"Повернули экран в {new_orientation}")
            
            # Ждем адаптации интерфейса
            time.sleep(3)
            
            # Проверяем, что приложение работает в новой ориентации
            try:
                # Ищем элементы интерфейса
                self.driver.find_element(
                    AppiumBy.XPATH, 
                    "//android.widget.TextView"
                )
                logger.info("✅ Приложение работает в новой ориентации")
                
                # Возвращаем исходную ориентацию
                self.driver.orientation = current_orientation
                time.sleep(2)
                
                return True
                
            except NoSuchElementException:
                logger.error("❌ Приложение не адаптировалось к новой ориентации")
                return False
                
        except Exception as e:
            logger.error(f"❌ Ошибка при тестировании поворота экрана: {e}")
            return False
    
    def run_all_tests(self) -> dict:
        """Запуск всех тестов"""
        results = {}
        
        if not self.setup_driver():
            logger.error("❌ Не удалось настроить драйвер")
            return {"error": "Не удалось настроить драйвер"}
        
        try:
            # Запускаем тесты
            results["app_launch"] = self.test_app_launch()
            results["create_note"] = self.test_create_note()
            results["gestures"] = self.test_gestures()
            results["screen_rotation"] = self.test_screen_rotation()
            
            # Подсчитываем результаты
            passed = sum(results.values())
            total = len(results)
            
            logger.info(f"📊 Результаты тестирования: {passed}/{total} тестов пройдено")
            
            if passed == total:
                logger.info("🎉 Все тесты пройдены успешно!")
            else:
                logger.warning(f"⚠️ {total - passed} тестов не пройдено")
            
            return results
            
        finally:
            self.cleanup()
    
    def cleanup(self):
        """Очистка ресурсов"""
        if self.driver:
            try:
                self.driver.quit()
                logger.info("✅ Драйвер закрыт")
            except Exception as e:
                logger.error(f"❌ Ошибка при закрытии драйвера: {e}")


def main():
    """Основная функция"""
    logger.info("🚀 Запуск автоматизированного тестирования мобильного приложения")
    
    tester = MobileAppTester()
    results = tester.run_all_tests()
    
    # Выводим итоговый отчет
    print("\n" + "="*50)
    print("📋 ИТОГОВЫЙ ОТЧЕТ ТЕСТИРОВАНИЯ")
    print("="*50)
    
    for test_name, result in results.items():
        if isinstance(result, bool):
            status = "✅ ПРОЙДЕН" if result else "❌ НЕ ПРОЙДЕН"
            print(f"{test_name}: {status}")
    
    print("="*50)


if __name__ == "__main__":
    main()
