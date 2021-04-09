package sample.cypher;

import java.math.BigInteger;
import java.util.Random;

public class RabinCypher {

    public final static int DEFAULT_KEY_LENGTH = 128;

    public static Keys generateKeys() {
        return generateKeys(DEFAULT_KEY_LENGTH);
    }

    public static Keys generateKeys(int keyLength) {
        BigInteger p;
        BigInteger q;
        BigInteger n;
        do {
            do {
                p = new BigInteger(keyLength * 8, new Random());
            } while (!p.isProbablePrime(1) || p.mod(new BigInteger("4")).compareTo(new BigInteger("3")) != 0);
            do {
                q = new BigInteger(keyLength * 8, new Random());
            } while (!q.isProbablePrime(1) || q.mod(new BigInteger("4")).compareTo(new BigInteger("3")) != 0);
            n = p.multiply(q);
        } while(n.compareTo(new BigInteger("256")) < 0);

        BigInteger b;
        do {
            b = new BigInteger(keyLength * 8, new Random());
        } while (n.compareTo(b) < 0);

        return new Keys(new PublicKey(n, b), new PrivateKey(p, q));
    }

    public static BigInteger[] encrypt(byte[] plain, PublicKey publicKey) {
        BigInteger[] cypher = new BigInteger[plain.length];

        int i = 0;
        while (i < plain.length) {
            BigInteger plainNum = new BigInteger("" + (0xFF & plain[i]));
            cypher[i] = plainNum.multiply(plainNum.add(publicKey.getB())).mod(publicKey.getN());
            i++;
        }

        return cypher;
    }

    public static byte[] decrypt(BigInteger[] cypher, Keys keys) {
        byte[] plain = new byte[cypher.length];

        BigInteger n = keys.getPublicKey().getN();
        BigInteger b = keys.getPublicKey().getB();
        BigInteger p = keys.getPrivateKey().getP();
        BigInteger q = keys.getPrivateKey().getQ();

        BigInteger[] euclidResult = gcdEx(p, q);
        BigInteger yp = euclidResult[1];
        BigInteger yq = euclidResult[2];
        int i = 0;
        while (i < cypher.length) {
            BigInteger D =  b.multiply(b).add(cypher[i].multiply(new BigInteger("4"))).mod(n);
            BigInteger mp = D.modPow(p.add(new BigInteger("1")).divide(new BigInteger("4")), p);
            BigInteger mq = D.modPow(q.add(new BigInteger("1")).divide(new BigInteger("4")), q);

            BigInteger ypMulPMulMq = yp.multiply(p).multiply(mq);
            BigInteger yqMulQMulMp = yq.multiply(q).multiply(mp);

            BigInteger[] d = new BigInteger[4];
            d[0] = ypMulPMulMq.add(yqMulQMulMp).mod(n);
            d[1] = n.subtract(d[0]);
            d[2] = ypMulPMulMq.subtract(yqMulQMulMp).mod(n);
            d[3] = n.subtract(d[2]);

            int di = 0;
            BigInteger m;
            do {
                if(d[di].subtract(b).mod(new BigInteger("2")).compareTo(new BigInteger("0")) == 0) {
                    m = d[di].subtract(b).divide(new BigInteger("2")).mod(n);
                } else {
                    m = d[di].subtract(b).add(n).divide(new BigInteger("2")).mod(n);
                }
                di++;
            } while (m.compareTo(new BigInteger("256")) >= 0);

            plain[i] = (byte) m.intValue();
            i++;
        }

        return plain;
    }

    public static byte[] decrypt(BigInteger[] cypher, Keys keys, StringBuilder sb) {
        byte[] plain = new byte[cypher.length];

        BigInteger n = keys.getPublicKey().getN();
        BigInteger b = keys.getPublicKey().getB();
        BigInteger p = keys.getPrivateKey().getP();
        BigInteger q = keys.getPrivateKey().getQ();

        BigInteger[] euclidResult = gcdEx(p, q);
        BigInteger yp = euclidResult[1];
        BigInteger yq = euclidResult[2];
        int i = 0;
        while (i < cypher.length) {
            BigInteger D =  b.multiply(b).add(cypher[i].multiply(new BigInteger("4"))).mod(n);
            BigInteger mp = D.modPow(p.add(new BigInteger("1")).divide(new BigInteger("4")), p);
            BigInteger mq = D.modPow(q.add(new BigInteger("1")).divide(new BigInteger("4")), q);

            BigInteger ypMulPMulMq = yp.multiply(p).multiply(mq);
            BigInteger yqMulQMulMp = yq.multiply(q).multiply(mp);

            BigInteger[] d = new BigInteger[4];
            d[0] = ypMulPMulMq.add(yqMulQMulMp).mod(n);
            d[1] = n.subtract(d[0]);
            d[2] = ypMulPMulMq.subtract(yqMulQMulMp).mod(n);
            d[3] = n.subtract(d[2]);

            BigInteger mCorrect = null;
            BigInteger[] m = new BigInteger[4];
            for(int mi = 0; mi < 4; mi++) {
                if(d[mi].subtract(b).mod(new BigInteger("2")).compareTo(new BigInteger("0")) == 0) {
                    m[mi] = d[mi].subtract(b).divide(new BigInteger("2")).mod(n);
                } else {
                    m[mi] = d[mi].subtract(b).add(n).divide(new BigInteger("2")).mod(n);
                }

                sb.append(m[mi]);

                if(m[mi].compareTo(new BigInteger("256")) < 0) {
                    mCorrect = m[mi];
                    sb.append("[").append((char) mCorrect.intValue()).append("]");
                }
                sb.append(" ");
            }
            sb.append("\n");

            if(mCorrect == null) {
                plain[i] = (byte) m[0].intValue();
            } else {
                plain[i] = (byte) mCorrect.intValue();
            }

            i++;
        }

        return plain;
    }

    public static BigInteger[] gcdEx(BigInteger a, BigInteger b) {
        if(b.compareTo(new BigInteger("0")) == 0) {
            return new BigInteger[]{a, new BigInteger("1"), new BigInteger("0")};
        }

        BigInteger[] result = gcdEx(b, a.mod(b));
        return new BigInteger[]{result[0], result[2], result[1].subtract(result[2].multiply(a.divide(b)))};
    }
}
