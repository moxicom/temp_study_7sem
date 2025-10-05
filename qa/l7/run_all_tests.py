#!/usr/bin/env python3
"""
Скрипт для запуска всех тестов мобильных приложений
"""

import sys
import subprocess
import time
import logging
from typing import List, Dict, Any

# Настройка логирования
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


class TestRunner:
    """Класс для запуска всех тестов"""
    
    def __init__(self):
        self.test_modules = [
            {
                "name": "Notes/Keep App Tests",
                "module": "test_notes_app",
                "description": "Тестирование приложения заметок"
            },
            {
                "name": "Calculator App Tests", 
                "module": "test_calculator_app",
                "description": "Тестирование приложения калькулятор"
            }
        ]
        
        self.results = {}
    
    def check_environment(self) -> bool:
        """Проверка готовности окружения"""
        logger.info("🔍 Проверка готовности окружения...")
        
        # Проверка Appium сервера
        try:
            import requests
            response = requests.get("http://127.0.0.1:4723/status", timeout=5)
            if response.status_code == 200:
                logger.info("✅ Appium сервер запущен")
            else:
                logger.error("❌ Appium сервер не отвечает")
                return False
        except Exception as e:
            logger.error(f"❌ Appium сервер не запущен: {e}")
            logger.error("💡 Запустите Appium сервер: make run-appium")
            return False
        
        # Проверка эмулятора
        try:
            result = subprocess.run(
                ["adb", "devices"], 
                capture_output=True, 
                text=True, 
                timeout=10
            )
            if "emulator" in result.stdout:
                logger.info("✅ Эмулятор подключен")
            else:
                logger.error("❌ Эмулятор не подключен")
                logger.error("💡 Запустите эмулятор: make run-emulator")
                return False
        except Exception as e:
            logger.error(f"❌ Ошибка проверки эмулятора: {e}")
            return False
        
        # Проверка виртуального окружения
        try:
            import appium
            import selenium
            logger.info("✅ Python зависимости установлены")
        except ImportError as e:
            logger.error(f"❌ Отсутствуют Python зависимости: {e}")
            logger.error("💡 Установите зависимости: make python")
            return False
        
        logger.info("✅ Окружение готово к тестированию")
        return True
    
    def run_test_module(self, module_info: Dict[str, str]) -> bool:
        """Запуск отдельного модуля тестов"""
        module_name = module_info["name"]
        module_file = module_info["module"]
        
        logger.info(f"🧪 Запуск {module_name}...")
        logger.info(f"📝 {module_info['description']}")
        
        try:
            # Запуск тестов через subprocess
            result = subprocess.run(
                [sys.executable, f"{module_file}.py"],
                capture_output=True,
                text=True,
                timeout=300  # 5 минут на тест
            )
            
            if result.returncode == 0:
                logger.info(f"✅ {module_name} завершены успешно")
                self.results[module_name] = True
                return True
            else:
                logger.error(f"❌ {module_name} завершены с ошибками")
                logger.error(f"Ошибка: {result.stderr}")
                self.results[module_name] = False
                return False
                
        except subprocess.TimeoutExpired:
            logger.error(f"⏰ Таймаут при выполнении {module_name}")
            self.results[module_name] = False
            return False
        except Exception as e:
            logger.error(f"❌ Ошибка при запуске {module_name}: {e}")
            self.results[module_name] = False
            return False
    
    def run_all_tests(self) -> Dict[str, bool]:
        """Запуск всех тестов"""
        logger.info("🚀 Запуск полного набора тестов мобильных приложений")
        logger.info("=" * 60)
        
        # Проверка окружения
        if not self.check_environment():
            logger.error("❌ Окружение не готово. Завершение тестирования.")
            return {"error": "Окружение не готово"}
        
        logger.info("")
        
        # Запуск каждого модуля тестов
        for module_info in self.test_modules:
            logger.info("-" * 40)
            success = self.run_test_module(module_info)
            
            if success:
                logger.info(f"✅ {module_info['name']} - ПРОЙДЕНЫ")
            else:
                logger.error(f"❌ {module_info['name']} - НЕ ПРОЙДЕНЫ")
            
            # Небольшая пауза между тестами
            time.sleep(2)
        
        # Подсчет результатов
        total_tests = len(self.test_modules)
        passed_tests = sum(self.results.values())
        failed_tests = total_tests - passed_tests
        
        logger.info("")
        logger.info("=" * 60)
        logger.info("📊 ИТОГОВЫЙ ОТЧЕТ")
        logger.info("=" * 60)
        logger.info(f"Всего модулей тестов: {total_tests}")
        logger.info(f"Пройдено: {passed_tests}")
        logger.info(f"Не пройдено: {failed_tests}")
        
        if failed_tests == 0:
            logger.info("🎉 ВСЕ ТЕСТЫ ПРОЙДЕНЫ УСПЕШНО!")
        else:
            logger.warning(f"⚠️ {failed_tests} модулей тестов не пройдено")
        
        logger.info("=" * 60)
        
        return self.results
    
    def print_detailed_results(self):
        """Вывод детальных результатов"""
        print("\n" + "=" * 60)
        print("📋 ДЕТАЛЬНЫЕ РЕЗУЛЬТАТЫ ТЕСТИРОВАНИЯ")
        print("=" * 60)
        
        for test_name, result in self.results.items():
            if isinstance(result, bool):
                status = "✅ ПРОЙДЕНЫ" if result else "❌ НЕ ПРОЙДЕНЫ"
                print(f"{test_name:30} {status}")
            else:
                print(f"{test_name:30} {result}")
        
        print("=" * 60)


def main():
    """Основная функция"""
    print("🚀 Автоматизированное тестирование мобильных приложений")
    print("=" * 60)
    
    runner = TestRunner()
    results = runner.run_all_tests()
    
    # Вывод детальных результатов
    runner.print_detailed_results()
    
    # Код возврата
    if "error" in results:
        sys.exit(1)
    
    failed_count = sum(1 for result in results.values() if result is False)
    if failed_count > 0:
        sys.exit(1)
    else:
        sys.exit(0)


if __name__ == "__main__":
    main()

