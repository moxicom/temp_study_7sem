# Makefile for Buggy TODO Flask App
VENV_NAME = venv
PYTHON = python3
PIP = $(VENV_NAME)/bin/pip
PYTHON_VENV = $(VENV_NAME)/bin/python
FLASK_APP = app.py
HOST = 127.0.0.1
PORT = 5000

# Default target
.PHONY: help
help:
	@echo "Available commands:"
	@echo "  make setup    - Create virtual environment and install dependencies"
	@echo "  make install  - Install dependencies in existing venv"
	@echo "  make run      - Run the Flask application"
	@echo "  make dev      - Run in development mode with debug"
	@echo "  make clean    - Remove virtual environment"
	@echo "  make test     - Test the application (basic curl tests)"
	@echo "  make freeze   - Generate requirements.txt from current environment"

# Create virtual environment and install dependencies
.PHONY: setup
setup: $(VENV_NAME)/bin/activate
	$(PIP) install --upgrade pip
	$(PIP) install -r requirements.txt
	@echo "Setup complete! Virtual environment created and dependencies installed."

# Create virtual environment
$(VENV_NAME)/bin/activate:
	$(PYTHON) -m venv $(VENV_NAME)

# Install dependencies only
.PHONY: install
install:
	@if [ ! -d "$(VENV_NAME)" ]; then \
		echo "Virtual environment not found. Run 'make setup' first."; \
		exit 1; \
	fi
	$(PIP) install -r requirements.txt

# Run the application
.PHONY: run
run:
	@if [ ! -d "$(VENV_NAME)" ]; then \
		echo "Virtual environment not found. Run 'make setup' first."; \
		exit 1; \
	fi
	@echo "Starting Flask app at http://$(HOST):$(PORT)"
	$(PYTHON_VENV) $(FLASK_APP)

# Run in development mode
.PHONY: dev
dev:
	@if [ ! -d "$(VENV_NAME)" ]; then \
		echo "Virtual environment not found. Run 'make setup' first."; \
		exit 1; \
	fi
	@echo "Starting Flask app in development mode at http://$(HOST):$(PORT)"
	FLASK_ENV=development $(PYTHON_VENV) $(FLASK_APP)

# Clean up virtual environment
.PHONY: clean
clean:
	rm -rf $(VENV_NAME)
	@echo "Virtual environment removed."

# Basic tests using curl
.PHONY: test
test:
	@echo "Running basic API tests..."
	@echo "Testing GET /api/tasks:"
	@curl -s http://$(HOST):$(PORT)/api/tasks || echo "App not running?"
	@echo "\nTesting GET /:"
	@curl -s -o /dev/null -w "Status: %{http_code}\n" http://$(HOST):$(PORT)/ || echo "App not running?"

# Generate requirements.txt from current environment
.PHONY: freeze
freeze:
	@if [ ! -d "$(VENV_NAME)" ]; then \
		echo "Virtual environment not found. Run 'make setup' first."; \
		exit 1; \
	fi
	$(PIP) freeze > requirements.txt
	@echo "requirements.txt updated."

# Show current environment info
.PHONY: info
info:
	@echo "Project: Buggy TODO Flask App"
	@echo "Python: $(PYTHON)"
	@echo "Virtual Environment: $(VENV_NAME)"
	@echo "Flask App: $(FLASK_APP)"
	@echo "Host: $(HOST)"
	@echo "Port: $(PORT)"
	@if [ -d "$(VENV_NAME)" ]; then \
		echo "Virtual environment: EXISTS"; \
		echo "Python version: $$($(PYTHON_VENV) --version)"; \
		echo "Pip version: $$($(PIP) --version)"; \
	else \
		echo "Virtual environment: NOT FOUND"; \
	fi 