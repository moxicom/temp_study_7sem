# scripts/open_and_close.py
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service as ChromeService
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.chrome.options import Options
import time

def main():
    options = Options()
    # options.add_argument("--headless")  # для headless надо раскомментировать
    options.add_argument("--start-maximized")
    # Опции для исправления проблемы с data: URL на macOS
    options.add_argument("--disable-blink-features=AutomationControlled")
    options.add_experimental_option("excludeSwitches", ["enable-automation"])
    options.add_experimental_option('useAutomationExtension', False)
    options.add_argument("--disable-dev-shm-usage")
    options.add_argument("--no-sandbox")
    # Инициализация Chrome через webdriver-manager
    service = ChromeService(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service, options=options)

    try:
        driver.get("https://the-internet.herokuapp.com/login")
        print("Title:", driver.title)
        time.sleep(2)  # для демонстрации (в реальных тестах использовать явные ожидания)
    finally:
        driver.quit()

if __name__ == "__main__":
    main()
