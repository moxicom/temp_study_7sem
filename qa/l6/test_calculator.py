import pytest
from calculator import Calculator


calc = Calculator()

@pytest.mark.parametrize("a, b, expected", [
    (1, 2, 3),
    (-1, 5, 4),
    (0, 0, 0),
    (3.5, 2.5, 6.0),
])
def test_add(a, b, expected):
    assert calc.add(a, b) == expected


@pytest.mark.parametrize("a, b, expected", [
    (10, 2, 5),
    (9, 3, 3),
    (5, 2, 2.5),
    (-10, 2, -5),
])
def test_divide(a, b, expected):
    assert calc.divide(a, b) == expected


def test_divide_by_zero():
    """Проверяем, что при делении на ноль выбрасывается исключение."""
    with pytest.raises(ZeroDivisionError, match="Деление на ноль запрещено"):
        calc.divide(5, 0)


# --- ТЕСТЫ ДЛЯ is_prime_number ---
@pytest.mark.parametrize("n, expected", [
    (1, False),
    (2, True),
    (3, True),
    (4, False),
    (17, True),
    (18, False),
    (19, True),
    (20, False),
    (0, False),
    (-5, False),
])
def test_is_prime_number(n, expected):
    assert calc.is_prime_number(n) == expected
