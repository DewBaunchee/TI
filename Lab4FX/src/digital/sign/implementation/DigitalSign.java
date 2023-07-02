package digital.sign.implementation;

import java.math.BigInteger;

public class DigitalSign {
    private final BigInteger _r;
    private final BigInteger _s;

    public DigitalSign(BigInteger r, BigInteger s) {
        _r = r;
        _s = s;
    }

    public DigitalSign(String string) {
        string = string.trim();
        _r = new BigInteger(string.substring(0, string.indexOf(" ")));
        _s = new BigInteger(string.substring(string.indexOf(" ") + 1));
    }

    public BigInteger getR() {
        return _r;
    }

    public BigInteger getS() {
        return _s;
    }

    @Override
    public String toString() {
        return _r.toString() + " " + _s.toString();
    }
}
