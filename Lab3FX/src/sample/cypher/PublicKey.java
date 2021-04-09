package sample.cypher;

import java.math.BigInteger;

public class PublicKey {
    private final BigInteger _n;
    private final BigInteger _b;

    public PublicKey(BigInteger n, BigInteger b) {
        _n = n;
        _b = b;
    }

    public BigInteger getN() {
        return _n;
    }

    public BigInteger getB() {
        return _b;
    }
}
