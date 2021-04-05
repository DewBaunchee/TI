#include <iostream>
#include <random>

#include "bigIntegers/BigInteger.h"

int main() {
    std::cout << (BigInteger("6958") * BigInteger("1643")) << std::endl;
    return 0;
}























/*std::random_device randomDevice;
    std::mt19937 gen(randomDevice());

    unsigned int x = gen();
    unsigned int y = gen();
    unsigned int o = gen();
    std::cout << "X:" << x << std::endl;
    std::cout << "Y:" << y << std::endl;
    std::cout << "O:" << o << std::endl;
    std::cout << (x + y) % 5 << std::endl;
    std::cout << x % 5 + y % 5 << std::endl;
    std::cout << 30309 % 209 << std::endl;*/