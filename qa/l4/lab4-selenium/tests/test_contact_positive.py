import pytest
from pages.contact_page import ContactPage

def test_positive_contact_form(driver):
    page = ContactPage(driver)
    page.open_page()
    page.fill_form("Alex", "Markov", "alex@example.com", "Male", "1234567890")
    page.submit_form()

    success = page.success_message_visible()
    assert success is not None
    assert "Thanks for submitting the form" in success.text
