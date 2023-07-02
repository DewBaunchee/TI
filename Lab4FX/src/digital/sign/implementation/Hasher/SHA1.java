package digital.sign.implementation.Hasher;

import digital.sign.implementation.DigitalSigner;
import digital.sign.implementation.MillerRabin;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Scanner;

public class SHA1 implements Hasher {

    public static void main(String[] args) {
        while(true) {
            Scanner in = new Scanner(System.in);
            System.out.println(Arrays.toString(DigitalSigner.gcdEx(new BigInteger(in.nextLine()), new BigInteger(in.nextLine()))));
        }
    }

    public static final byte APPENDABLE_ONE_BIT = (byte) 0x80;
    public static final int BLOCK_NEEDED_BIT_LENGTH = 448;
    public static final int BLOCK_BIT_LENGTH = 512;
    public static final int BIT_COUNT_FOR_LENGTH = 64;
    public static final int BYTE_BIT_LENGTH = 8;
    public static final int BYTES_IN_INT = 4;

    public static final int START_H0 = 0x67452301;
    public static final int START_H1 = 0xEFCDAB89;
    public static final int START_H2 = 0x98BADCFE;
    public static final int START_H3 = 0x10325476;
    public static final int START_H4 = 0xC3D2E1F0;

    public static final int K1 = 0x5A827999;
    public static final int K2 = 0x6ED9EBA1;
    public static final int K3 = 0x8F1BBCDC;
    public static final int K4 = 0xCA62C1D6;

    public BigInteger hash(byte[] plain, BigInteger module) {
        int[] correctArray = prepareArray(plain);

        int h0, h1, h2, h3, h4;
        h0 = START_H0;
        h1 = START_H1;
        h2 = START_H2;
        h3 = START_H3;
        h4 = START_H4;
        for (int i = 0; i < correctArray.length; i += 16) {
            int[] w = new int[80];
            System.arraycopy(correctArray, i, w, 0, 16);
            for (int j = 16; j < w.length; j++) {
                w[j] = leftRotate((w[j - 3] ^ w[j - 8] ^ w[j - 14] ^ w[j - 16]), 1);
            }

            int a = h0, b = h1, c = h2, d = h3, e = h4;
            int f;
            int k;
            for (int j = 0; j < w.length; j++) {
                if (j < 20) {
                    f = (b & c) ^ (~b & d);
                    k = K1;
                } else if (j < 40) {
                    f = b ^ c ^ d;
                    k = K2;
                } else if (j < 60) {
                    f = (b & c) ^ (b & d) ^ (c & d);
                    k = K3;
                } else {
                    f = b ^ c ^ d;
                    k = K4;
                }

                int temp = leftRotate(a, 5) + f + e + k + w[j];
                e = d;
                d = c;
                c = leftRotate(b, 30);
                b = a;
                a = temp;
            }

            h0 = h0 + a;
            h1 = h1 + b;
            h2 = h2 + c;
            h3 = h3 + d;
            h4 = h4 + e;
        }

        BigInteger bh0 = BigInteger.valueOf(Integer.toUnsignedLong(h0)).shiftLeft(128);
        BigInteger bh1 = BigInteger.valueOf(Integer.toUnsignedLong(h1)).shiftLeft(96);
        BigInteger bh2 = BigInteger.valueOf(Integer.toUnsignedLong(h2)).shiftLeft(64);
        BigInteger bh3 = BigInteger.valueOf(Integer.toUnsignedLong(h3)).shiftLeft(32);
        BigInteger bh4 = BigInteger.valueOf(Integer.toUnsignedLong(h4));

        return bh0.or(bh1).or(bh2).or(bh3).or(bh4);
    }

    private static int leftRotate(int w, int times) {
        while (times > 0) {
            w = ((w & 0x80000000) == 0 ? 0 : 1) | (w << 1);
            times--;
        }
        return w;
    }

    private static int[] prepareArray(byte[] plain) {
        int bitLength = plain.length * BYTE_BIT_LENGTH;
        int bitNeeded = (BLOCK_NEEDED_BIT_LENGTH - bitLength % BLOCK_BIT_LENGTH + BLOCK_BIT_LENGTH)
                % BLOCK_BIT_LENGTH + BIT_COUNT_FOR_LENGTH;

        int[] correctArray = new int[(bitLength + bitNeeded) / (BYTE_BIT_LENGTH * BYTES_IN_INT)];
        int i;
        for (i = 0; plain.length - i > 3; i += 4) {
            correctArray[i / 4] = (plain[i] << 24)
                    | (plain[i + 1] << 16)
                    | (plain[i + 2] << 8)
                    | (plain[i + 3]);

        }
        for (; i < plain.length; i++) {
            correctArray[i / 4] = correctArray[i / 4] | (plain[i] << (8 * (4 - (i + 1) % 4)));
        }
        correctArray[i / 4] = correctArray[i / 4] | (Byte.toUnsignedInt(APPENDABLE_ONE_BIT) << (8 * (4 - (i + 1) % 4)));
        correctArray[correctArray.length - 2] = (int) (((long) plain.length * 8) & 0xFFFFFFFF00000000L);
        correctArray[correctArray.length - 1] = (int) (((long) plain.length * 8) & 0xFFFFFFFFL);

        return correctArray;
    }
}
