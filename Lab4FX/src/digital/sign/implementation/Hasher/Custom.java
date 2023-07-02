package digital.sign.implementation.Hasher;

import digital.sign.MyLogger;
import digital.sign.implementation.DigitalSigner;

import java.math.BigInteger;

public class Custom implements Hasher {
    @Override
    public BigInteger hash(byte[] plain, BigInteger module) {
        BigInteger hash = new BigInteger(module.toString()).divide(new BigInteger("3")).multiply(BigInteger.TWO);

        for (byte aByte : plain) {
            BigInteger bigByte = BigInteger.valueOf(aByte).add(BigInteger.ONE);
            hash = DigitalSigner.modPow(hash, bigByte, module).add(bigByte).mod(module);
        }

        return hash;
    }
}
