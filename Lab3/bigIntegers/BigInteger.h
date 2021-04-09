#ifndef LAB3_BIGINTEGER_H
#define LAB3_BIGINTEGER_H

#include <iostream>
#include <vector>

class BigInteger {
private:
    char * _digits;
    int _length;
    bool _negative;

    void _shortenDigits(int count);
    void _shortenZeroes();
    BigInteger _concat(const BigInteger&) const;
    struct divisionResult _divMod(const BigInteger&) const;
    BigInteger _mulmod(BigInteger &, BigInteger &, const BigInteger &) const;
    BigInteger _modulo(BigInteger, BigInteger, BigInteger) const;
    bool _miller(BigInteger, int) const;
public:
    explicit BigInteger(int);
    explicit BigInteger(const char *);
    BigInteger pow(int);
    friend BigInteger operator*(const BigInteger&, const BigInteger&);
    friend BigInteger operator/(const BigInteger&, const BigInteger&);
    friend BigInteger operator%(const BigInteger&, const BigInteger&);
    friend BigInteger operator+(const BigInteger&, const BigInteger&);
    friend BigInteger operator-(const BigInteger&, const BigInteger&);
    friend bool operator>(const BigInteger&, const BigInteger&);
    friend bool operator<(const BigInteger&, const BigInteger&);
    friend bool operator==(const BigInteger&, const BigInteger&);
    friend bool operator!=(const BigInteger&, const BigInteger&);
    friend std::ostream& operator<< (std::ostream&, const BigInteger&);
    explicit operator std::string() const;

    void set(int index, char value);
    char get(int index) const;
    int getLength() const;
    BigInteger subInt(int) const;
    BigInteger subInt(int, int) const;
    bool isPrime(int) const;

    static BigInteger random(unsigned int);
    static BigInteger toBigInteger(long long);

    BigInteger();
};

#endif //LAB3_BIGINTEGER_H
