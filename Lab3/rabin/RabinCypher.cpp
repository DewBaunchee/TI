#include "RabinCypher.h"

#define DEFAULT_KEY_LENGTH 128

struct Keys RabinCypher::generateKeys() {
    return generateKeys(DEFAULT_KEY_LENGTH);
}

struct Keys RabinCypher::generateKeys(unsigned int keyLength) {
    BigInteger p;
    do {
        p = BigInteger::random(keyLength);
    } while (!p.isPrime(1) && p % BigInteger(4) != BigInteger(3));

    BigInteger q;
    do {
        q = BigInteger::random(keyLength);
    } while (!q.isPrime(1) && q % BigInteger(4) != BigInteger(3));

    BigInteger n = p * q;
    BigInteger b;
    do {
        b = BigInteger::random(n.getLength());
    } while (n < b);

    struct Key publicKey{n, b};

    struct Key privateKey{p, q};

    struct Keys keys{publicKey, privateKey};

    return keys;
}

struct ByteArray RabinCypher::encrypt(const char *plain, unsigned int length, struct Key publicKey) {
    struct ByteArray cypher{new unsigned char[length]{0}, length};

    for (unsigned int i = 0; i < length; i++) {
        BigInteger plainNum = BigInteger::toBigInteger(plain[i]);
        BigInteger cypherNum = plainNum * (plainNum + publicKey.bq) % publicKey.np;
        cypher.array[i] = (cypherNum.get(1) << 4) | cypherNum.get(0);
    }

    return cypher;
}

struct ByteArray RabinCypher::decrypt(const char *cypher, unsigned int length, struct Key privateKey) {
    return ByteArray();
}