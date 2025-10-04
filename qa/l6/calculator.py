class Calculator:
    """Простой калькулятор с базовой бизнес-логикой."""

    def add(self, a, b):
        """Сложение двух чисел."""
        return a + b

    def divide(self, a, b):
        """Деление одного числа на другое.
        Вызывает исключение ZeroDivisionError при делении на ноль.
        """
        if b == 0:
            raise ZeroDivisionError("Деление на ноль запрещено.")
        return a / b

    def is_prime_number(self, n):
        """Проверка, является ли число простым."""
        if n <= 1:
            return False
        for i in range(2, int(n ** 0.5) + 1):
            if n % i == 0:
                return False
        return True
