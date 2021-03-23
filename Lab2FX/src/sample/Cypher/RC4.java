package sample.Cypher;

public class RC4 {
    private final int[] S;
    private int first, second;

    public RC4(byte[] key) {
        S = new int[256];

        for(int i = 0; i < S.length; i++) S[i] = i;
        int j = 0;
        for(int i = 0; i < S.length; i++) {
            j = (j + S[i] + key[i % key.length]) % S.length;
            swap(S, i, j);
        }
        first = second = 0;
    }

    private void swap(int[] s, int i, int j) {
        int temp = s[i];
        s[i] = s[j];
        s[j] = temp;
    }

    private int getNextKey() {
        first = (first + 1) % S.length;
        second = (second + S[first]) % S.length;
        swap(S, first, second);
        return S[(S[first] + S[second]) % S.length];
    }

    public byte[] transform(byte[] plain) {
        byte[] cyphered = new byte[plain.length];
        for(int i = 0; i < cyphered.length; i++) {
            int keyByte = getNextKey();
            cyphered[i] = (byte) (plain[i] ^ keyByte);
            System.out.println("+------------------------------------+");
            System.out.println(toBit(plain[i]) + " ^ "
                    + toBit((byte) keyByte) + " = " + toBit(cyphered[i]));
            System.out.println((int) plain[i] + " ^ " + keyByte + " = " + (int) cyphered[i]);
        }
        return cyphered;
    }

    public static String toBit(byte b) {
        String answer = Integer.toBinaryString(b);
        if(answer.length() > 8) {
            answer = answer.substring(24);
        } else {
            while (answer.length() < 8) answer = "0" + answer;
        }
        return answer;
    }
}