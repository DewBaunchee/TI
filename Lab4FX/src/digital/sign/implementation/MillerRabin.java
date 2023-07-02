package digital.sign.implementation;

import java.math.BigInteger;
import java.util.Random;

public class MillerRabin {

    public static boolean isProbablePrime(BigInteger n, int iteration) {
        if(n.bitLength() > 16) {
            return n.isProbablePrime(iteration);
        }

        if (n.compareTo(BigInteger.ZERO) == 0 || n.compareTo(BigInteger.ONE) == 0)
            return false;
        if (n.compareTo(BigInteger.TWO) == 0)
            return true;
        if (n.mod(BigInteger.TWO).compareTo(BigInteger.ZERO) == 0)
            return false;

        BigInteger s = n.subtract(BigInteger.ONE);
        while (s.mod(BigInteger.TWO).compareTo(BigInteger.ZERO) == 0)
            s = s.divide(BigInteger.TWO);

        Random rand = new Random();
        for (int i = 0; i < iteration; i++) {
            BigInteger r = new BigInteger(n.bitLength(), new Random());
            BigInteger a = r.mod(n.subtract(BigInteger.ONE)).add(BigInteger.ONE);
            BigInteger temp = s;
            BigInteger mod = modPow(a, temp, n);
            while (temp.compareTo(n.subtract(BigInteger.ONE)) != 0
                    && mod.compareTo(BigInteger.ONE) != 0
                    && mod.compareTo(n.subtract(BigInteger.ONE)) != 0) {
                mod = mulMod(mod, mod, n);
                temp = temp.multiply(BigInteger.TWO);
            }
            if (mod.compareTo(n.subtract(BigInteger.ONE)) != 0
                    && temp.mod(BigInteger.TWO).compareTo(BigInteger.ZERO) == 0)
                return false;
        }
        return true;
    }

    public static BigInteger modPow(BigInteger a, BigInteger b, BigInteger c) {
        BigInteger res = BigInteger.ONE;
        for (BigInteger i = BigInteger.ZERO; i.compareTo(b) < 0; i = i.add(BigInteger.ONE)) {
            res = res.multiply(a);
            res = res.mod(c);
        }

        return res.mod(c);
    }

    public static BigInteger mulMod(BigInteger a, BigInteger b, BigInteger mod) {
        return a.multiply(b).mod(mod);
    }
}
