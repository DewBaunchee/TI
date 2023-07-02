package digital.sign.implementation.keys;

import java.math.BigInteger;

public class PublicKey {
    private final BigInteger _p;
    private final BigInteger _q;
    private final BigInteger _g;
    private final BigInteger _y;

    public PublicKey(BigInteger p, BigInteger q, BigInteger g, BigInteger y) {
        _p = p;
        _q = q;
        _g = g;
        _y = y;
    }

    public BigInteger getP() {
        return _p;
    }

    public BigInteger getQ() {
        return _q;
    }

    public BigInteger getG() {
        return _g;
    }

    public BigInteger getY() {
        return _y;
    }
}
