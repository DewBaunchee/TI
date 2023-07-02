package digital.sign.implementation;

import java.math.BigInteger;

public class SignCheckResult {
    private final BigInteger _r;
    private final BigInteger _v;
    private final boolean result;

    public SignCheckResult(BigInteger r, BigInteger v) {
        _r = r;
        _v = v;
        result = _r.compareTo(_v) == 0;
    }

    public boolean get() {
        return result;
    }

    public BigInteger getR() {
        return _r;
    }

    public BigInteger getV() {
        return _v;
    }
}
