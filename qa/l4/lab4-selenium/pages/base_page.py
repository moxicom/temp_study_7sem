from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.action_chains import ActionChains

class BasePage:
    def __init__(self, driver):
        self.driver = driver
        self.timeout = 15

    def open(self, url):
        self.driver.get(url)

    def find(self, locator):
        return WebDriverWait(self.driver, self.timeout).until(
            EC.presence_of_element_located(locator)
        )

    def click(self, locator):
        element = self.find(locator)
        # Прокручиваем к элементу, чтобы он был видимым
        self.driver.execute_script("arguments[0].scrollIntoView(true);", element)
        # Ждем немного, чтобы прокрутка завершилась
        import time
        time.sleep(0.5)
        # Используем JavaScript клик для надежности
        self.driver.execute_script("arguments[0].click();", element)

    def type(self, locator, text):
        field = self.find(locator)
        # Прокручиваем к полю, чтобы оно было видимым
        self.driver.execute_script("arguments[0].scrollIntoView(true);", field)
        import time
        time.sleep(0.3)
        field.clear()
        field.send_keys(text)

    def get_text(self, locator):
        return self.find(locator).text
