#ifndef LAB3_BIGINTEGER_H
#define LAB3_BIGINTEGER_H

#include <iostream>
#include <vector>

class BigInteger {
private:
    char * _digits;
    unsigned int _length;
    bool negative;

    void shortenDigits(unsigned int count);
    void shortenZeroes();
public:
    BigInteger(unsigned int);
    BigInteger(const char *);
    BigInteger pow(unsigned);
    friend BigInteger operator*(const BigInteger&, const BigInteger&);
    friend BigInteger operator%(const BigInteger&, const BigInteger&);
    friend BigInteger operator+(const BigInteger&, const BigInteger&);
    friend BigInteger operator-(const BigInteger&, const BigInteger&);
    friend bool operator>(const BigInteger&, const BigInteger&);
    friend bool operator<(const BigInteger&, const BigInteger&);
    friend bool operator==(const BigInteger&, const BigInteger&);
    friend std::ostream& operator<< (std::ostream&, const BigInteger&);

    void set(unsigned int index, char value);
    char get(unsigned int index) const;
};

#endif //LAB3_BIGINTEGER_H
