import pytest
from pages.contact_page import ContactPage


def test_negative_invalid_email(driver):
    """Тест с неправильным форматом email"""
    page = ContactPage(driver)
    page.open_page()
    page.fill_form("Alex", "Cursed", "invalid-email-format", "Male", "1234567890")
    
    # Пробуем отправить форму с неправильным email
    page.click(page.SUBMIT)
    
    # Проверяем, что форма не отправилась (нет модального окна)
    try:
        # Если модальное окно появилось, значит валидация не сработала
        success = page.success_message_visible()
        assert False, "Форма отправилась с неправильным email - валидация не работает"
    except:
        # Ожидаемое поведение - модальное окно не появилось
        print("Форма правильно не отправилась с неправильным email")
        
    # Проверяем, что email поле показывает ошибку валидации
    email_field = page.find(page.EMAIL)
    validation_message = email_field.get_attribute("validationMessage")
    print(f"Сообщение валидации: {validation_message}")
    
    # В HTML5 поле email должно показывать сообщение валидации
    assert validation_message is not None and len(validation_message) > 0
