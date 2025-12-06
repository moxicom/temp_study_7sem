#include <iostream>
#include <cmath>

const int MIN_X = 0;
const int MAX_X = 10;
const double squareMethodTolerance = 0.01;
const int GRAPH_HEIGHT = 20;
const int GRAPH_WIDTH = 100;

double getMembershipFunction(double x);
void printMembershipGraph(int x1, int x2);

double calculateSegmentArea(double xLeft, double xRight);
double calculateAreaUnderGraph();
double findMedian();

int main() {
    printMembershipGraph(MIN_X, MAX_X);
    
    std::cout.precision(5);
    double area = calculateAreaUnderGraph();
    std::cout << std::endl;
    std::cout << "Площадь под графиком функции принадлежности: " << area << std::endl;
    
    double median = findMedian();
    std::cout << "Медиана (точка x, где площадь делится пополам): " << median << std::endl;
    
    return 0;
}


// variant 10
double getMembershipFunction(double x) {
    if (x >= 0 && x <= 2) {
        return 1.0 - 0.25 * pow(x - 2, 2);
    } else if (x > 2 && x <= 8) {
        return 1.0;
    } else if (x > 8 && x <= 10) {
        return 1.0 - 0.125 * pow(x - 8, 2);
    } else {
        return 0.0;
    }
}


double calculateSegmentArea(double xLeft, double xRight) {
    double width = xRight - xLeft;
    double height = getMembershipFunction(xLeft);
    return width * height;
}

double calculateAreaUnderGraph() {
    int numSegments = static_cast<int>((MAX_X - MIN_X) / squareMethodTolerance);
    
    double segmentWidth = static_cast<double>(MAX_X - MIN_X) / numSegments;
    double totalArea = 0.0;
    
    for (int i = 0; i < numSegments; ++i) {
        double xLeft = MIN_X + i * segmentWidth; 
        double xRight = MIN_X + (i + 1) * segmentWidth;
        
        totalArea += calculateSegmentArea(xLeft, xRight);
    }
    
    return totalArea;
}

double findMedian() {
    double totalArea = calculateAreaUnderGraph();
    double targetArea = totalArea / 2.0;
    
    int numSegments = static_cast<int>((MAX_X - MIN_X) / squareMethodTolerance);
    double segmentWidth = static_cast<double>(MAX_X - MIN_X) / numSegments;
    
    double accumulatedArea = 0.0;
    
    for (int i = 0; i < numSegments; ++i) {
        double xLeft = MIN_X + i * segmentWidth;
        double xRight = MIN_X + (i + 1) * segmentWidth;
        
        accumulatedArea += calculateSegmentArea(xLeft, xRight);
        
        if (std::abs(targetArea - accumulatedArea) <= squareMethodTolerance) {
            return xRight;
        }
        
        if (accumulatedArea > targetArea) {
            return xRight;
        }
    }
    
    return MAX_X;
}



void printMembershipGraph(int x1, int x2) {
    const int numSegments = GRAPH_WIDTH;
    const int height = GRAPH_HEIGHT;
    const double yStep = 1.0 / height;
    const double xStep = static_cast<double>(x2 - x1) / numSegments;
    const double tolerance = yStep / 2.0;
    
    double yValues[numSegments + 1];
    
    for (int i = 0; i <= numSegments; ++i) {
        double x = x1 + i * xStep;
        yValues[i] = getMembershipFunction(x);
    }
    
    std::cout << std::fixed;
    std::cout.precision(1);
    
    for (int row = height; row >= 0; --row) {
        double yLevel = static_cast<double>(row) / height;
        
        std::cout << yLevel << " |";
        
        for (int i = 0; i <= numSegments; ++i) {
            double y = yValues[i];
            
            if (y >= yLevel - tolerance && y <= yLevel + tolerance) {
                std::cout << '*';
            } else {
                std::cout << ' ';
            }
        }
        std::cout << std::endl;
    }

    std::cout << 0.0f << " ";
    int labelStep = numSegments / (x2 - x1);
    for (int i = 0; i <= numSegments; ++i) {
        if (i % labelStep == 0) {
            std::cout << '+';
        } else {
            std::cout << '-';
        }
    }
    std::cout << std::endl;
    
    std::cout << "    ";
    for (int x = x1; x <= x2; ++x) {
        std::cout << x;
        if (x < x2) {
            for (int j = 1; j < labelStep; ++j) {
                std::cout << ' ';
            }
        }
    }
    std::cout << std::endl;
}