from selenium.webdriver.common.by import By
from .base_page import BasePage
import time

class ContactPage(BasePage):
    URL = "https://demoqa.com/automation-practice-form"

    FIRST_NAME = (By.ID, "firstName")
    LAST_NAME = (By.ID, "lastName")
    EMAIL = (By.ID, "userEmail")
    MOBILE = (By.ID, "userNumber")
    SUBMIT = (By.ID, "submit")
    SUCCESS_MODAL = (By.CSS_SELECTOR, ".modal-content")
    EMAIL_ERROR = (By.CSS_SELECTOR, "input#userEmail[aria-invalid='true']")  # правильный селектор ошибки

    def open_page(self):
        self.open(self.URL)

    def fill_form(self, firstname, lastname, email, gender="Male", mobile="1234567890"):
        self.type(self.FIRST_NAME, firstname)
        self.type(self.LAST_NAME, lastname)
        if email:  # заполняем email только если он не пустой
            self.type(self.EMAIL, email)

        # Выбор пола
        if gender:
            gender_locator = (By.XPATH, f"//label[text()='{gender}']")
            self.click(gender_locator)

        # Мобильный телефон
        if mobile:
            self.type(self.MOBILE, mobile)


    def submit_form(self):
        self.click(self.SUBMIT)
        time.sleep(2)  # ждем появления модального окна

    def success_message_visible(self):
        try:
            return self.find(self.SUCCESS_MODAL)
        except:
            return None

    def email_error_visible(self):
        return self.find(self.EMAIL_ERROR)
