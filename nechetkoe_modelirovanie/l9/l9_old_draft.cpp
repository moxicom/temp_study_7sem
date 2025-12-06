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

        if (xPos >= 0 && xPos <= 2) {
            squareRect = stepX * (1.0 - 0.25 * pow(xPos - 2, 2));
        }
        else if (xPos >= 2 && xPos <= 8) {
            squareRect = stepX * 1.0;
        }
        else {
            squareRect = stepX * (1.0 - 0.125 * pow(xPos - 8, 2));
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

        if (xPos >= 0 && xPos <= 2) {
            squareRect = stepX * (1.0 - 0.25 * pow(xPos - 2, 2));
        }
        else if (xPos >= 2 && xPos <= 8) {
            squareRect = stepX * 1.0;
        }
        else {
            squareRect = stepX * (1.0 - 0.125 * pow(xPos - 8, 2));
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


// variant 13
// double getMembershipFunction(double x) {
//     if (x < MIN_X) {
//         throw std::out_of_range("x is less than MIN_X: " + std::to_string(x));
//     }
//     if (x > MAX_X) {
//         throw std::out_of_range("x is greater than MAX_X: " + std::to_string(x));
//     }

//     if (x >= 0 && x <= 1) {
//         return 0.5 * x * x * x;
//     } else if (x > 1 && x <= 8) {
//         return 0.5;
//     } else {
//         return 1.0 - 0.125 * pow(x - 10, 2);
//     }
// }