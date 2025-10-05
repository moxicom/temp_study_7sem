#!/usr/bin/env python3
"""
Автоматическое тестирование приложения Калькулятор
с использованием Appium и Python
"""

import time
import logging
from typing import Optional
from appium import webdriver
from appium.options.android import UiAutomator2Options
from appium.webdriver.common.appiumby import AppiumBy
from selenium.common.exceptions import NoSuchElementException

# Настройка логирования
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('calculator_test_results.log'),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)


class CalculatorTester:
    """Класс для тестирования приложения Калькулятор"""
    
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
                "appPackage": "com.google.android.calculator",
                "appActivity": "com.android.calculator2.Calculator",
                "noReset": True,
                "newCommandTimeout": 60,
                "autoGrantPermissions": True,
                "unicodeKeyboard": True,
                "resetKeyboard": True
            }
            
            options = UiAutomator2Options().load_capabilities(desired_caps)
            self.driver = webdriver.Remote(self.appium_server_url, options=options)
            
            logger.info("✅ Успешно подключились к Appium серверу для тестирования калькулятора")
            return True
            
        except Exception as e:
            logger.error(f"❌ Ошибка при подключении к Appium: {e}")
            return False
    
    def test_app_launch(self) -> bool:
        """Тест 1: Проверка запуска приложения Калькулятор"""
        try:
            logger.info("🔍 Тест 1: Проверка запуска приложения Калькулятор")
            
            # Ждем загрузки главного экрана
            time.sleep(3)
            
            # Проверяем, что приложение запустилось
            current_activity = self.driver.current_activity
            logger.info(f"Текущая активность: {current_activity}")
            
            # Проверяем наличие элементов интерфейса калькулятора
            try:
                # Ищем кнопку цифры (обычно "0")
                digit_button = self.driver.find_element(
                    AppiumBy.XPATH, 
                    "//android.widget.Button[@text='0']"
                )
                logger.info("✅ Приложение Калькулятор успешно запущено")
                return True
                
            except NoSuchElementException:
                # Пробуем альтернативные селекторы
                try:
                    digit_button = self.driver.find_element(
                        AppiumBy.XPATH, 
                        "//android.widget.Button[contains(@text, '0')]"
                    )
                    logger.info("✅ Приложение Калькулятор запущено (альтернативный селектор)")
                    return True
                except NoSuchElementException:
                    logger.warning("⚠️ Не удалось найти элементы калькулятора")
                    return False
                    
        except Exception as e:
            logger.error(f"❌ Ошибка при тестировании запуска калькулятора: {e}")
            return False
    
    def test_basic_calculation(self) -> bool:
        """Тест 2: Базовое вычисление (2 + 3 = 5)"""
        try:
            logger.info("🔍 Тест 2: Базовое вычисление (2 + 3 = 5)")
            
            # Нажимаем кнопку "2"
            button_2 = self.driver.find_element(
                AppiumBy.XPATH, 
                "//android.widget.Button[@text='2']"
            )
            button_2.click()
            logger.info("✅ Нажали на кнопку '2'")
            
            # Нажимаем кнопку "+"
            button_plus = self.driver.find_element(
                AppiumBy.XPATH, 
                "//android.widget.Button[@text='+']"
            )
            button_plus.click()
            logger.info("✅ Нажали на кнопку '+'")
            
            # Нажимаем кнопку "3"
            button_3 = self.driver.find_element(
                AppiumBy.XPATH, 
                "//android.widget.Button[@text='3']"
            )
            button_3.click()
            logger.info("✅ Нажали на кнопку '3'")
            
            # Нажимаем кнопку "="
            button_equals = self.driver.find_element(
                AppiumBy.XPATH, 
                "//android.widget.Button[@text='=']"
            )
            button_equals.click()
            logger.info("✅ Нажали на кнопку '='")
            
            # Проверяем результат
            time.sleep(1)
            try:
                # Ищем результат в поле отображения
                result_field = self.driver.find_element(
                    AppiumBy.XPATH, 
                    "//android.widget.TextView[contains(@resource-id, 'result')]"
                )
                result_text = result_field.text
                logger.info(f"✅ Результат вычисления: {result_text}")
                
                # Проверяем, что результат содержит "5"
                if "5" in result_text:
                    logger.info("✅ Базовое вычисление выполнено корректно")
                    return True
                else:
                    logger.error(f"❌ Неожиданный результат: {result_text}")
                    return False
                    
            except NoSuchElementException:
                logger.error("❌ Не удалось найти поле результата")
                return False
                
        except Exception as e:
            logger.error(f"❌ Ошибка при выполнении базового вычисления: {e}")
            return False
    
    def test_clear_function(self) -> bool:
        """Тест 3: Проверка функции очистки"""
        try:
            logger.info("🔍 Тест 3: Проверка функции очистки")
            
            # Сначала введем какое-то число
            button_5 = self.driver.find_element(
                AppiumBy.XPATH, 
                "//android.widget.Button[@text='5']"
            )
            button_5.click()
            logger.info("✅ Ввели число '5'")
            
            # Нажимаем кнопку очистки (обычно "C" или "Clear")
            try:
                clear_button = self.driver.find_element(
                    AppiumBy.XPATH, 
                    "//android.widget.Button[@text='C']"
                )
            except NoSuchElementException:
                clear_button = self.driver.find_element(
                    AppiumBy.XPATH, 
                    "//android.widget.Button[@text='Clear']"
                )
            
            clear_button.click()
            logger.info("✅ Нажали на кнопку очистки")
            
            # Проверяем, что дисплей очищен
            time.sleep(1)
            try:
                result_field = self.driver.find_element(
                    AppiumBy.XPATH, 
                    "//android.widget.TextView[contains(@resource-id, 'result')]"
                )
                result_text = result_field.text
                logger.info(f"✅ Результат после очистки: {result_text}")
                
                # Проверяем, что результат пустой или равен "0"
                if result_text == "" or result_text == "0":
                    logger.info("✅ Функция очистки работает корректно")
                    return True
                else:
                    logger.error(f"❌ Очистка не сработала: {result_text}")
                    return False
                    
            except NoSuchElementException:
                logger.error("❌ Не удалось найти поле результата после очистки")
                return False
                
        except Exception as e:
            logger.error(f"❌ Ошибка при тестировании функции очистки: {e}")
            return False
    
    def test_multiplication(self) -> bool:
        """Тест 4: Проверка умножения (4 * 3 = 12)"""
        try:
            logger.info("🔍 Тест 4: Проверка умножения (4 * 3 = 12)")
            
            # Нажимаем кнопку "4"
            button_4 = self.driver.find_element(
                AppiumBy.XPATH, 
                "//android.widget.Button[@text='4']"
            )
            button_4.click()
            logger.info("✅ Нажали на кнопку '4'")
            
            # Нажимаем кнопку "*"
            button_multiply = self.driver.find_element(
                AppiumBy.XPATH, 
                "//android.widget.Button[@text='×']"
            )
            button_multiply.click()
            logger.info("✅ Нажали на кнопку '×'")
            
            # Нажимаем кнопку "3"
            button_3 = self.driver.find_element(
                AppiumBy.XPATH, 
                "//android.widget.Button[@text='3']"
            )
            button_3.click()
            logger.info("✅ Нажали на кнопку '3'")
            
            # Нажимаем кнопку "="
            button_equals = self.driver.find_element(
                AppiumBy.XPATH, 
                "//android.widget.Button[@text='=']"
            )
            button_equals.click()
            logger.info("✅ Нажали на кнопку '='")
            
            # Проверяем результат
            time.sleep(1)
            try:
                result_field = self.driver.find_element(
                    AppiumBy.XPATH, 
                    "//android.widget.TextView[contains(@resource-id, 'result')]"
                )
                result_text = result_field.text
                logger.info(f"✅ Результат умножения: {result_text}")
                
                # Проверяем, что результат содержит "12"
                if "12" in result_text:
                    logger.info("✅ Умножение выполнено корректно")
                    return True
                else:
                    logger.error(f"❌ Неожиданный результат умножения: {result_text}")
                    return False
                    
            except NoSuchElementException:
                logger.error("❌ Не удалось найти поле результата")
                return False
                
        except Exception as e:
            logger.error(f"❌ Ошибка при выполнении умножения: {e}")
            return False
    
    def run_all_tests(self) -> dict:
        """Запуск всех тестов калькулятора"""
        results = {}
        
        if not self.setup_driver():
            logger.error("❌ Не удалось настроить драйвер для калькулятора")
            return {"error": "Не удалось настроить драйвер"}
        
        try:
            # Запускаем тесты
            results["app_launch"] = self.test_app_launch()
            results["basic_calculation"] = self.test_basic_calculation()
            results["clear_function"] = self.test_clear_function()
            results["multiplication"] = self.test_multiplication()
            
            # Подсчитываем результаты
            passed = sum(results.values())
            total = len(results)
            
            logger.info(f"📊 Результаты тестирования калькулятора: {passed}/{total} тестов пройдено")
            
            if passed == total:
                logger.info("🎉 Все тесты калькулятора пройдены успешно!")
            else:
                logger.warning(f"⚠️ {total - passed} тестов калькулятора не пройдено")
            
            return results
            
        finally:
            self.cleanup()
    
    def cleanup(self):
        """Очистка ресурсов"""
        if self.driver:
            try:
                self.driver.quit()
                logger.info("✅ Драйвер калькулятора закрыт")
            except Exception as e:
                logger.error(f"❌ Ошибка при закрытии драйвера калькулятора: {e}")


def main():
    """Основная функция для тестирования калькулятора"""
    logger.info("🚀 Запуск автоматизированного тестирования приложения Калькулятор")
    
    tester = CalculatorTester()
    results = tester.run_all_tests()
    
    # Выводим итоговый отчет
    print("\n" + "="*60)
    print("📋 ИТОГОВЫЙ ОТЧЕТ ТЕСТИРОВАНИЯ КАЛЬКУЛЯТОРА")
    print("="*60)
    
    for test_name, result in results.items():
        if isinstance(result, bool):
            status = "✅ ПРОЙДЕН" if result else "❌ НЕ ПРОЙДЕН"
            print(f"{test_name}: {status}")
    
    print("="*60)


if __name__ == "__main__":
    main()

