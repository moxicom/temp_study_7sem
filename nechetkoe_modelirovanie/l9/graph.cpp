
// void printMembershipGraph(int x1, int x2) {
//     const int numSegments = GRAPH_WIDTH;
//     const int height = GRAPH_HEIGHT;
//     const double yStep = 1.0 / height;
//     const double xStep = static_cast<double>(x2 - x1) / numSegments;
//     const double tolerance = yStep / 2.0;
    
//     double yValues[numSegments + 1];
    
//     for (int i = 0; i <= numSegments; ++i) {
//         double x = x1 + i * xStep;
//         yValues[i] = getMembershipFunction(x);
//     }
    
//     std::cout << std::fixed;
//     std::cout.precision(1);
    
//     for (int row = height; row >= 0; --row) {
//         double yLevel = static_cast<double>(row) / height;
        
//         std::cout << yLevel << " |";
        
//         for (int i = 0; i <= numSegments; ++i) {
//             double y = yValues[i];
            
//             if (y >= yLevel - tolerance && y <= yLevel + tolerance) {
//                 std::cout << '*';
//             } else {
//                 std::cout << ' ';
//             }
//         }
//         std::cout << std::endl;
//     }

//     std::cout << 0.0f << " ";
//     int labelStep = numSegments / (x2 - x1);
//     for (int i = 0; i <= numSegments; ++i) {
//         if (i % labelStep == 0) {
//             std::cout << '+';
//         } else {
//             std::cout << '-';
//         }
//     }
//     std::cout << std::endl;
    
//     std::cout << "    ";
//     for (int x = x1; x <= x2; ++x) {
//         std::cout << x;
//         if (x < x2) {
//             for (int j = 1; j < labelStep; ++j) {
//                 std::cout << ' ';
//             }
//         }
//     }
//     std::cout << std::endl;
// }