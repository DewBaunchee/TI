package digital.sign.implementation.keys;

import java.math.BigInteger;

public class PrivateKey {
    private final BigInteger _x;

    public PrivateKey(BigInteger x) {
        _x = x;
    }

    public BigInteger getX() {
        return _x;
    }
}
