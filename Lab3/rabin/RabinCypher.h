#ifndef LAB3_RABINCYPHER_H
#define LAB3_RABINCYPHER_H

#include "../bigIntegers/BigInteger.h"

struct Key {
    BigInteger np;
    BigInteger bq;
};

struct Keys {
    struct Key publicKey;
    struct Key privateKey;
};

struct ByteArray {
    unsigned char *array;
    unsigned int length;
};

class RabinCypher {
public:
    static struct Keys generateKeys();

    static struct Keys generateKeys(unsigned);

    static struct ByteArray encrypt(const char *, unsigned int, struct Key);

    static struct ByteArray decrypt(const char *, unsigned int, struct Key);
};

#endif //LAB3_RABINCYPHER_H