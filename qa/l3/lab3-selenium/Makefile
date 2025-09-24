init:
	python3 -m venv .venv

install:
	.venv/bin/pip install -r requirements.txt

run_open_close:
	.venv/bin/python scripts/open_and_close.py

run_login:
	.venv/bin/pytest tests/test_login.py

run_all:
	.venv/bin/pytest -v
