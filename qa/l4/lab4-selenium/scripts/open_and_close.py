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
