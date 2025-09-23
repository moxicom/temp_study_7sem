# tests/test_login.py
import os
import pytest
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service as ChromeService
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

LOGIN_URL = "https://the-internet.herokuapp.com/login"
VALID_USER = os.getenv("TEST_USERNAME", "tomsmith")
VALID_PASS = os.getenv("TEST_PASSWORD", "SuperSecretPassword!")

@pytest.fixture(scope="function")
def driver():
    options = Options()
    # Для CI/виртуального сервера можно включить headless:
    # options.add_argument("--headless=new")  # или "--headless"
    options.add_argument("--window-size=1920,1080")
    # инициализация драйвера (Chrome)
    service = ChromeService(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service, options=options)
    yield driver
    driver.quit()

def test_login_valid_shows_logout(driver):
    driver.get(LOGIN_URL)

    # Вводим логин/пароль
    driver.find_element(By.ID, "username").send_keys(VALID_USER)
    driver.find_element(By.ID, "password").send_keys(VALID_PASS)

    # Нажимаем Submit
    driver.find_element(By.CSS_SELECTOR, "button[type='submit']").click()

    # Явное ожидание появления ссылки "Logout"
    wait = WebDriverWait(driver, 10)
    logout_elem = wait.until(EC.presence_of_element_located((By.XPATH, "//a[contains(., 'Logout')]")))

    assert logout_elem is not None
    assert "Logout" in logout_elem.text
