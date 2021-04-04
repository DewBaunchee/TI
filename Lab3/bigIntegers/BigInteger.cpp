#include "BigInteger.h"

#include "cstring"

BigInteger::BigInteger(unsigned int len) {
    _length = len;
    _digits = new char[_length]{0};
    negative = false;
}

BigInteger::BigInteger(const char *initValue) {
    unsigned int len = strlen(initValue);
    if (len == 0) {
        _length = 1;
        _digits = new char[_length]{0};
        negative = false;
        shortenZeroes();
        return;
    }

    negative = initValue[0] == '-';

    _length = len - negative;
    int i = negative;
    while (i < len && initValue[i] == '0') {
        _length--;
        i++;
    }

    _digits = new char[_length]{0};

    bool isNull = true;
    for (; i < len; i++) {
        isNull = false;
        _digits[i - (len - _length)] = (char) (initValue[i] - '0');
    }

    if (isNull) {
        _length = 1;
        _digits = new char[1]{0};
        negative = false;
    }
    shortenZeroes();
}

void BigInteger::set(unsigned int index, char value) {
    if (index >= _length) return;
    _digits[_length - 1 - index] = value;
}

char BigInteger::get(unsigned int index) const {
    if (index >= _length) return -1;
    return _digits[_length - 1 - index];
}

BigInteger BigInteger::pow(unsigned int) {
    return {""};
}

BigInteger operator*(const BigInteger &operand1, const BigInteger &operand2) {
    const BigInteger *max;
    const BigInteger *min;
    if (operand1._length > operand2._length) {
        max = &operand1;
        min = &operand2;
    } else {
        max = &operand2;
        min = &operand1;
    }

    BigInteger answer = BigInteger(max->_length * 2);
    answer.negative = operand1.negative ^ operand2.negative;
    int remain = 0;
    int i = 0;
    while (i < min->_length) {
        int mul = min->get(i) * max->get(i) + remain;
        remain = mul / 10;
        answer.set(i, (char) (mul % 10));
        i++;
    }

    while (i < max->_length) {
        int mul = max->get(i) + remain;
        remain = mul / 10;
        answer.set(i, (char) (mul % 10));
        i++;
    }
    answer.shortenZeroes();

    return answer;
}

BigInteger operator%(const BigInteger &x, const BigInteger &y) {
    return {""};
}

BigInteger operator+(const BigInteger &operand1, const BigInteger &operand2) {
    if (operand1.negative ^ operand2.negative) {
        if (operand1.negative) {
            return operand2 - BigInteger(-1) * operand1;
        } else {
            return operand1 - BigInteger(-1) * operand2;
        }
    }

    const BigInteger *max;
    const BigInteger *min;
    if (operand1._length > operand2._length) {
        max = &operand1;
        min = &operand2;
    } else {
        max = &operand2;
        min = &operand1;
    }

    BigInteger answer = BigInteger(max->_length + 1);
    answer.negative = operand1.negative;
    int remain = 0;
    int i = 0;
    while (i < min->_length) {
        int sum = min->get(i) + max->get(i) + remain;
        remain = sum > 9;
        answer.set(i, (char) (sum % 10));
        i++;
    }

    while (i < max->_length) {
        int sum = max->get(i) + remain;
        remain = sum > 9;
        answer.set(i, (char) (sum % 10));
        i++;
    }
    answer.shortenZeroes();

    return answer;
}

BigInteger operator-(const BigInteger &operand1, const BigInteger &operand2) {
    if (operand1.negative ^ operand2.negative) {
        return operand1 + BigInteger(-1) * operand2;
    }

    const BigInteger *max;
    const BigInteger *min;
    if (operand1 < operand2) {
        max = &operand2;
        min = &operand1;
    } else {
        max = &operand1;
        min = &operand2;
    }

    BigInteger answer = BigInteger(max->_length);
    answer.negative = &operand2 == max;

    if (operand1.negative) {
        const BigInteger *temp = max;
        max = min;
        min = temp;
    }

    int i = 0;
    int remain = 0;
    while (i < min->_length) {
        int diff = max->get(i) - min->get(i) - remain;
        remain = diff < 0;
        answer.set(i, (char) ((diff + 10) % 10));
        i++;
    }
    while (i < max->_length) {
        int diff = max->get(i) - remain;
        remain = diff < 0;
        answer.set(i, (char) ((diff + 10) % 10));
        i++;
    }
    answer.shortenZeroes();

    return answer;
}

std::ostream &operator<<(std::ostream &out, const BigInteger &self) {
    if (self.negative) out << '-';
    for (int i = 0; i < self._length; i++) {
        out << (int) self._digits[i];
    }
    return out;
}

void BigInteger::shortenDigits(unsigned int count) {
    if (count > _length) count = _length;

    _digits += count;
    _length -= count;
}

void BigInteger::shortenZeroes() {
    while (_length > 1 && _digits[0] == 0) {
        _length--;
        _digits += 1;
    }
}

bool operator>(const BigInteger &operand1, const BigInteger &operand2) {
    if (operand1.negative && !operand2.negative) return false;
    if (!operand1.negative && operand2.negative) return true;

    if (operand1._length != operand2._length) {
        if (operand1._length < operand2._length) {
            return operand1.negative;
        } else {
            return !operand1.negative;
        }
    }

    int i = 0;
    while (i < operand1._length && operand1.get(i) == operand2.get(i)) i++;
    if (i == operand1._length) return false;

    return operand1.negative ? operand1.get(i) < operand2.get(i) : operand1.get(i) > operand2.get(i);
}

bool operator<(const BigInteger &operand1, const BigInteger &operand2) {
    if (operand1.negative && !operand2.negative) return true;
    if (!operand1.negative && operand2.negative) return false;

    if (operand1._length != operand2._length) {
        if (operand1._length < operand2._length) {
            return !operand1.negative;
        } else {
            return operand1.negative;
        }
    }

    int i = 0;
    while (i < operand1._length && operand1.get(i) == operand2.get(i)) i++;
    if (i == operand1._length) return false;

    return operand1.negative ? operand1.get(i) > operand2.get(i) : operand1.get(i) < operand2.get(i);
}

bool operator==(const BigInteger &operand1, const BigInteger &operand2) {
    if (operand1.negative ^ operand2.negative) return false;
    if (operand1._length != operand2._length) return false;

    int i = 0;
    while (i < operand1._length && operand1.get(i) == operand2.get(i)) i++;

    return i == operand1._length;
}

