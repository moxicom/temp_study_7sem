.PHONY: install run_all run_positive run_negative

install:
	python3 -m venv .venv
	.venv/bin/pip install -U pip
	.venv/bin/pip install selenium webdriver-manager pytest pytest-html

run_all:
	.venv/bin/pytest -v

run_positive:
	.venv/bin/pytest -v -k "positive"

run_negative:
	.venv/bin/pytest -v -k "negative"
