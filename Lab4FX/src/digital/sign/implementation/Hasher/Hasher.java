package digital.sign.implementation.Hasher;

import java.math.BigInteger;

public interface Hasher {
    BigInteger hash(byte[] plain, BigInteger module);
}
