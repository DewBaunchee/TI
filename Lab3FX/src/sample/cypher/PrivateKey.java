package sample.cypher;

import java.math.BigInteger;

public class PrivateKey {
    private final BigInteger _p;
    private final BigInteger _q;

    public PrivateKey(BigInteger p, BigInteger q) {
        _p = p;
        _q = q;
    }

    public BigInteger getP() {
        return _p;
    }

    public BigInteger getQ() {
        return _q;
    }
}
