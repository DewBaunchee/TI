package digital.sign.implementation;

import digital.sign.MyLogger;
import digital.sign.implementation.Hasher.Hasher;
import digital.sign.implementation.keys.Keys;
import digital.sign.implementation.keys.PrivateKey;
import digital.sign.implementation.keys.PublicKey;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

public class DigitalSigner {

    public static final int CERTAINTY = 100;

    public static DigitalSign getSign(byte[] bytes, PublicKey publicKey, PrivateKey privateKey, BigInteger k, Hasher hasher) throws IOException {
        BigInteger hash = hasher.hash(bytes, publicKey.getQ());

        log("Hash generated: " + hash + " for text: \n\""
                + (bytes.length > 256
                ? new String(bytes).substring(0, 256) + "..."
                : new String(bytes)) + "\"");
        BigInteger reverseK = gcdEx(k, publicKey.getQ())[1];
        BigInteger r = publicKey.getG().modPow(k, publicKey.getP()).mod(publicKey.getQ());
        BigInteger s = reverseK.multiply(hash.add(privateKey.getX().multiply(r))).mod(publicKey.getQ());

        if (r.compareTo(BigInteger.ZERO) == 0) throw new IOException("R is zero, choose another K");
        if (s.compareTo(BigInteger.ZERO) == 0) throw new IOException("S is zero, choose another K");

        log("Digital sign:\n\tr: " + r + "\n\ts: " + s);
        return new DigitalSign(r, s);
    }

    public static SignCheckResult checkSign(byte[] bytes, DigitalSign sign, PublicKey key, Hasher hasher) {
        BigInteger hash = hasher.hash(bytes, key.getQ());

        log("Hash generated: " + hash + " for text: \n\""
                + (bytes.length > 256
                ? new String(bytes).substring(0, 256) + "..."
                : new String(bytes)) + "\"");
        BigInteger w = gcdEx(sign.getS(), key.getQ())[1];
        BigInteger u1 = hash.multiply(w).mod(key.getQ());
        BigInteger u2 = sign.getR().multiply(w).mod(key.getQ());
        BigInteger v = key.getG().modPow(u1, key.getP())
                .multiply(key.getY().modPow(u2, key.getP())).mod(key.getP())
                .mod(key.getQ());

        log("Checking sign:\n\tw: " + w +
                "\n\tu1: " + u1 +
                "\n\tu2: " + u2 +
                "\n\tv: " + v);
        return new SignCheckResult(sign.getR(), v);
    }

    public static Keys generateKeys(int keyLength) {
        BigInteger p;
        BigInteger q;
        BigInteger g;
        BigInteger y;
        BigInteger x;

        do {
            q = new BigInteger(keyLength * 8, new Random());
        } while (!MillerRabin.isProbablePrime(q, CERTAINTY));

        p = q.add(BigInteger.ONE);
        while (!MillerRabin.isProbablePrime(p, CERTAINTY)) {
            p = p.add(q);
        }

        do {
            BigInteger h = randomBetween(keyLength, BigInteger.TWO, p.subtract(BigInteger.TWO));
            g = modPow(h, p.subtract(BigInteger.ONE).divide(q), p);
        } while (g.compareTo(BigInteger.ONE) < 1);

        x = randomBetween(keyLength, BigInteger.ONE, q.subtract(BigInteger.ONE));
        y = g.modPow(x, p);

        log("Generated keys:\n" +
                "\tPublic key:\n" +
                "\t\tP: " + p + "\n" +
                "\t\tQ: " + q + "\n" +
                "\t\tG: " + g + "\n" +
                "\t\tY: " + y + "\n" +
                "\tPrivateKey:\n" +
                "\t\tX: " + x);
        return new Keys(new PublicKey(p, q, g, y), new PrivateKey(x));
    }

    public static BigInteger randomBetween(int length, BigInteger lowest, BigInteger biggest) {
        return new BigInteger(length * 8, new Random()).mod(biggest.subtract(lowest).add(BigInteger.ONE)).add(lowest);
    }

    public static BigInteger[] gcdEx(BigInteger a, BigInteger b) {
        if (b.compareTo(new BigInteger("0")) == 0) {
            return new BigInteger[]{a, new BigInteger("1"), new BigInteger("0")};
        }

        BigInteger[] result = gcdEx(b, a.mod(b));
        return new BigInteger[]{result[0], result[2], result[1].subtract(result[2].multiply(a.divide(b)))};
    }

    public static BigInteger modPow(BigInteger base, BigInteger exponent, BigInteger module) {
        BigInteger result = new BigInteger("1");
        while (exponent.compareTo(BigInteger.ZERO) > 0) {
            result = result.multiply(base).mod(module);
            exponent = exponent.subtract(BigInteger.ONE);
        }
        return result;
    }

    public static void log(String message) {
        MyLogger.getLogger().log(message);
    }
}
