#include "BigInteger.h"

#include <cstring>
#include <random>
#include <ctime>
#include <cstdlib>

#define BASE 16

static char alphabet[16] = {'0', '1', '2', '3',
                            '4', '5', '6', '7',
                            '8', '9', 'A', 'B',
                            'C', 'D', 'E', 'F'};

const static BigInteger BIZERO = BigInteger("0");
const static BigInteger BIONE = BigInteger("1");
const static BigInteger BITWO = BigInteger("2");
const static BigInteger BITHREE = BigInteger("3");

struct divisionResult {
    const BigInteger div;
    const BigInteger mod;
};

BigInteger::BigInteger() {
    _length = 1;
    _digits = new char[_length]{0};
    _negative = false;
}

BigInteger::BigInteger(int len) {
    if (len < 1) len = 1;
    _length = len;
    _digits = new char[_length]{0};
    _negative = false;
}

BigInteger::BigInteger(const char *initValue) {
    int len = strlen(initValue);
    if (len == 0) {
        _length = 1;
        _digits = new char[_length]{0};
        _negative = false;
        _shortenZeroes();
        return;
    }

    _negative = initValue[0] == '-';

    _length = len - _negative;
    int i = _negative;
    while (i < len - 1 && initValue[i] == '0') {
        _length--;
        i++;
    }

    _digits = new char[_length]{0};
    for (; i < len; i++) {
        if (initValue[i] >= 'A') {
            _digits[i - (len - _length)] = (char) (initValue[i] - 'A' + 10);
        } else {
            _digits[i - (len - _length)] = (char) (initValue[i] - '0');
        }
    }
    _shortenZeroes();
}

void BigInteger::set(int index, char value) {
    if (index >= _length) return;
    if (index < 0) {
        _digits[-index - 1] = value;
    } else {
        _digits[_length - 1 - index] = value;
    }
}

char BigInteger::get(int index) const {
    if (index >= _length) return -1;
    if (index < 0) {
        return _digits[-index - 1];
    } else {
        return _digits[_length - 1 - index];
    }
}

BigInteger BigInteger::subInt(int startIndex) const {
    return subInt(startIndex, _length);
}

BigInteger BigInteger::subInt(int startIndex, int endIndex) const {
    if (endIndex < 0) endIndex = (_length + (endIndex % _length)) % _length;
    if (endIndex > _length) endIndex = _length;
    if (startIndex < 0) startIndex = (_length + (startIndex % _length)) % _length;
    if (startIndex > _length) startIndex = _length;
    if (endIndex < startIndex) return BIZERO;

    BigInteger answer = BigInteger(endIndex - startIndex);
    int j = 0;
    for (int i = startIndex; i < endIndex; i++) answer.set(j++, this->get(i));

    return answer;
}

BigInteger BigInteger::_concat(const BigInteger &addable) const {
    BigInteger answer = BigInteger(_length + addable._length);
    for (int i = 0; i < addable._length; i++) {
        answer.set(i, addable.get(i));
    }
    for (int i = 0; i < _length; i++) {
        answer.set(i + addable._length, get(i));
    }
    return answer;
}

struct divisionResult BigInteger::_divMod(const BigInteger &right) const {
    if (right == BIZERO) throw std::runtime_error("Dividing by zero");
    const BigInteger left = *this;
    if (left < right) return {BIZERO, BIZERO + left};

    BigInteger answer = BigInteger(left._length - right._length + 1);
    answer._negative = left._negative ^ right._negative;

    int i = -1;
    int end = left._length;
    int start = -right._length;
    BigInteger remain = BIZERO;
    BigInteger sub;
    while (-start < left._length + 1) {
        sub = left.subInt(start, end);

        if (remain != BIZERO) {
            sub = remain._concat(sub);
        }

        while (sub < right) {
            answer.set(i--, 0);
            start--;
            if (-start == left._length + 1) {
                remain = sub;
                break;
            }
            BigInteger additionalDigit = left.subInt(start, start + 1);
            sub = sub._concat(additionalDigit);
        }
        if (-start == left._length + 1) break;

        BigInteger sum = BIZERO;
        int resultDigit = 0;
        do {
            sum = sum + right;
            resultDigit++;
        } while (sum < sub);
        sum = sum - right;
        resultDigit--;

        answer.set(i--, (char) (resultDigit));
        remain = sub - sum;

        end = start;
        start--;
    }

    answer._shortenZeroes();
    answer._shortenDigits(-i - 1);
    return {answer, remain};
}


BigInteger BigInteger::pow(int power) {
    if (power < 1) return BIZERO;
    BigInteger answer = BigInteger("1");
    while (power > 0) {
        answer = answer * (*this);
        power--;
    }
    return answer;
}

BigInteger operator*(const BigInteger &left, const BigInteger &right) {
    const BigInteger *max;
    const BigInteger *min;
    if (left._length > right._length) {
        max = &left;
        min = &right;
    } else {
        max = &right;
        min = &left;
    }

    BigInteger answer = BigInteger(max->_length + min->_length);
    answer._negative = left._negative ^ right._negative;

    int i = 0;
    int j;
    int sumRemain;
    while (i < min->_length) {
        sumRemain = 0;
        int remain = 0;

        for (j = 0; j < max->_length; j++) {
            int mul = min->get(i) * max->get(j) + remain;
            int sum = mul % BASE + answer.get(j + i) + sumRemain;
            remain = mul / BASE;
            sumRemain = sum / BASE;
            answer.set(j + i, (char) (sum % BASE));
        }

        answer.set(j + i, (char) (answer.get(j + i) + remain + sumRemain));
        i++;
    }
    answer._shortenZeroes();

    return answer;
}

BigInteger operator/(const BigInteger &left, const BigInteger &right) {
    return left._divMod(right).div;
}

BigInteger operator%(const BigInteger &left, const BigInteger &right) {
    return left._divMod(right).mod;
}

BigInteger operator+(const BigInteger &left, const BigInteger &right) {
    if (left._negative ^ right._negative) {
        if (left._negative) {
            return right - BigInteger(-1) * left;
        } else {
            return left - BigInteger(-1) * right;
        }
    }

    const BigInteger *max;
    const BigInteger *min;
    if (left._length > right._length) {
        max = &left;
        min = &right;
    } else {
        max = &right;
        min = &left;
    }

    BigInteger answer = BigInteger(max->_length + 1);
    answer._negative = left._negative;
    int remain = 0;
    int i = 0;
    while (i < min->_length) {
        int sum = min->get(i) + max->get(i) + remain;
        remain = sum / BASE;
        answer.set(i, (char) (sum % BASE));
        i++;
    }

    while (i < max->_length) {
        int sum = max->get(i) + remain;
        remain = sum > 9;
        answer.set(i, (char) (sum % BASE));
        i++;
    }
    answer.set(i, (char) remain);
    answer._shortenZeroes();

    return answer;
}

BigInteger operator-(const BigInteger &left, const BigInteger &right) {
    if (left._negative ^ right._negative) {
        return left + BigInteger(-1) * right;
    }

    const BigInteger *max;
    const BigInteger *min;
    if (left < right) {
        max = &right;
        min = &left;
    } else {
        max = &left;
        min = &right;
    }

    BigInteger answer = BigInteger(max->_length);
    answer._negative = &right == max;

    if (left._negative) {
        const BigInteger *temp = max;
        max = min;
        min = temp;
    }

    int i = 0;
    int remain = 0;
    while (i < min->_length) {
        int diff = max->get(i) - min->get(i) - remain;
        remain = diff < 0;
        answer.set(i, (char) ((diff + BASE) % BASE));
        i++;
    }
    while (i < max->_length) {
        int diff = max->get(i) - remain;
        remain = diff < 0;
        answer.set(i, (char) ((diff + BASE) % BASE));
        i++;
    }
    answer._shortenZeroes();

    return answer;
}

std::ostream &operator<<(std::ostream &out, const BigInteger &self) {
    if (self._negative) out << '-';
    for (int i = 0; i < self._length; i++) {
        out << alphabet[self._digits[i]];
    }
    return out;
}

void BigInteger::_shortenDigits(int newLength) {
    if (newLength < 1) return;
    if (newLength >= _length) return;

    _length = newLength;
    char *newDigits = new char[newLength];
    for (int i = 0; i < newLength; i++) newDigits[i] = _digits[i];
    _digits = newDigits;
}

void BigInteger::_shortenZeroes() {
    while (_length > 1 && _digits[0] == 0) {
        _length--;
        _digits += 1;
    }
}

bool operator>(const BigInteger &left, const BigInteger &right) {
    if (left._negative && !right._negative) return false;
    if (!left._negative && right._negative) return true;

    if (left._length != right._length) {
        if (left._length < right._length) {
            return left._negative;
        } else {
            return !left._negative;
        }
    }

    int i = 0;
    while (i < left._length && left._digits[i] == right._digits[i]) i++;
    if (i == left._length) return false;

    return left._negative ? left._digits[i] < right._digits[i] : left._digits[i] > right._digits[i];
}

bool operator<(const BigInteger &left, const BigInteger &right) {
    if (left._negative && !right._negative) return true;
    if (!left._negative && right._negative) return false;

    if (left._length != right._length) {
        if (left._length < right._length) {
            return !left._negative;
        } else {
            return left._negative;
        }
    }

    int i = 0;
    while (i < left._length && left._digits[i] == right._digits[i]) i++;
    if (i == left._length) return false;

    return left._negative ? left._digits[i] > right._digits[i] : left._digits[i] < right._digits[i];
}

bool operator==(const BigInteger &left, const BigInteger &right) {
    if (left._negative ^ right._negative) return false;
    if (left._length != right._length) return false;

    int i = 0;
    while (i < left._length && left.get(i) == right.get(i)) i++;

    return i == left._length;
}

bool operator!=(const BigInteger &left, const BigInteger &right) {
    return !(left == right);
}

BigInteger::operator std::string() const {
    std::string answer;
    answer.reserve(_length + _negative);
    if (_negative) {
        answer[0] = '-';
    }
    for (int i = _negative; i < _length + _negative; i++) {
        answer[i] = get(i - _negative);
    }
    return answer;
}

BigInteger BigInteger::random(unsigned int length) {
    BigInteger answer = BigInteger((int) length);
    std::random_device rd;
    std::mt19937 mersenne(rd() + rand());
    struct timespec ts{};

    int i = 0;
    while (i < length) {
        clock_gettime(CLOCK_REALTIME, &ts);
        unsigned int nextRand = mersenne() + ts.tv_nsec;
        while (nextRand > 0 && i < length) {
            answer.set(i++, (char) (nextRand % BASE));
            nextRand /= BASE;
        }
    }

    answer._shortenZeroes();
    return answer;
}

int BigInteger::getLength() const {
    return _length;
}

BigInteger BigInteger::toBigInteger(long long convertable) {
    if (convertable == 0) return BIZERO;

    char num[32];
    sprintf(num, "%X", convertable < 0 ? -1 * convertable : convertable);

    BigInteger answer = BigInteger(num);
    answer._negative = convertable < 0;

    return answer;
}

bool BigInteger::isPrime(int accuracy) const {
    return _miller(*this, accuracy);
}

BigInteger BigInteger::_mulmod(BigInteger &a, BigInteger &b, const BigInteger &mod) const {
    BigInteger x = BIZERO;
    BigInteger y = a % mod;
    while (b > BIZERO) {
        if (b % BITWO == BIONE) {
            x = (x + y) % mod;
        }
        y = (y * BITWO) % mod;
        b = b / BITWO;
    }
    return x % mod;
}

BigInteger BigInteger::_modulo(BigInteger base, BigInteger exponent, BigInteger mod) const {
    BigInteger x = BIONE;
    BigInteger y = base;
    while (exponent > BIZERO) {
        if (exponent % BITWO == BIONE)
            x = (x * y) % mod;
        y = (y * y) % mod;
        exponent = exponent / BITWO;
    }
    return x % mod;
}

bool BigInteger::_miller(BigInteger p, int iteration) const {
    if (p < BITWO) {
        return false;
    }
    if (p != BITWO && p % BITWO == BIZERO) {
        return false;
    }
    BigInteger s = p - BIONE;
    while (s % BITWO == BIZERO) {
        s = s / BITWO;
    }
    for (int i = 0; i < iteration; i++) {
        BigInteger a = BigInteger::toBigInteger(rand()) % (p - BIONE) + BIONE, temp = s;
        BigInteger mod = _modulo(a, temp, p);
        while (temp != p - BIONE && mod != BIONE && mod != p - BIONE) {
            mod = _mulmod(mod, mod, p);
            temp = temp * BITWO;
        }
        if (mod != p - BIONE && temp % BITWO == BIZERO) {
            return false;
        }
    }
    return true;
}