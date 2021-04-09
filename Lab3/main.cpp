#include <iostream>

#include "bigIntegers/BigInteger.h"
#include "rabin/RabinCypher.h"

int main() {
    for(int i = 0; i < 256; i++) {
        std::cout << i << (BigInteger::toBigInteger(i).isPrime(100) ? " is prime" : " is not prime") << std::endl;
    }
    struct Key key{BigInteger("123"), BigInteger("321")};
    printf("Cypher: %s\n", RabinCypher::encrypt("12345", 5, key).array);
    return 0;
}