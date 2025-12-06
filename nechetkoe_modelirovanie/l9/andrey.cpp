#include <iostream>
#include <cmath>

using namespace std;

int main() {
    double squareTotal = 0.0;    // общая площадь
    double squareRect = 0.0;     // площадь одного прямоугольника
    double xPos = 0.0;           // текущая координата x
    double stepX = 0.01;         // шаг Δx

    // === 1. Вычисление полной площади левым методом прямоугольников ===
    for (int i = 0; i < 10 / stepX; i++) {

        if (xPos >= 0 && xPos <= 1) {
            squareRect = stepX * (0.5 * xPos * xPos * xPos);
        }
        else if (xPos > 1 && xPos <= 8) {
            squareRect = stepX * 0.5;
        }
        else {
            squareRect = stepX * (1.0 - 0.125 * pow(xPos - 10, 2));
        }

        squareTotal += squareRect;
        xPos += stepX;
    }

    // половина площади
    double squareHalf = squareTotal / 2.0;

    // === 2. Поиск медианной точки x* ===
    xPos = 0.0;
    double squareAccum = 0.0;   // накопленная площадь

    for (int i = 0; i < 10 / stepX; i++) {

        if (xPos >= 0 && xPos <= 1) {
            squareRect = stepX * (0.5 * xPos * xPos * xPos);
        }
        else if (xPos > 1 && xPos <= 8) {
            squareRect = stepX * 0.5;
        }
        else {
            squareRect = stepX * (1.0 - 0.125 * pow(xPos - 10, 2));
        }

        squareAccum += squareRect;

        if (squareAccum >= squareHalf) {
            cout << "x* = " << xPos << endl;
            break;
        }

        xPos += stepX;
    }

    return 0;
}
