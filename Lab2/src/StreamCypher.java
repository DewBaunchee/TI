public class StreamCypher {
    public final LSRF lsrf;

    public StreamCypher(boolean[] startValue) {
        lsrf = new LSRF(startValue);
    }

    public byte encrypt(byte current) {
        byte keyByte = getKeyByte(lsrf);
        return (byte) (current ^ keyByte);
    }

    private static byte getKeyByte(LSRF lsrf) {
        byte answer = 0;
        for(int i = 0; i < 7; i++) {
            answer = (byte) (answer | (lsrf.getNext() ? 1 : 0));
            answer = (byte) (answer << 1);
        }
        return (byte) (answer | (lsrf.getNext() ? 1 : 0));
    }

    public static byte[] encryptByteArray(byte[] array, String startLSRF) {
        boolean[] startKey = stringToBoolArr(startLSRF);
        if(startKey == null) return null;
        LSRF lsrf = new LSRF(startKey);

        for(int i = 0; i < array.length; i++) {
            System.out.println("+--------------------------------+");
            System.out.print(toBit(array[i]));
            System.out.print(" ^ ");

            byte keyByte = getKeyByte(lsrf);
            System.out.print(toBit(keyByte));

            System.out.print(" = ");

            array[i] = (byte) (array[i] ^ keyByte);
            System.out.println(toBit(array[i]));
        }
        return array;
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

    public static boolean[] getWholeKey(int length, boolean[] startValue) {
        if(startValue == null) return null;

        LSRF lsrf = new LSRF(startValue);
        boolean[] key = new boolean[length];

        for(int i = 0; i < length; i++) {
            key[i] = lsrf.getNext();
        }

        return key;
    }

    public static String boolArrToString(boolean[] array) {
        if(array == null) return null;
        StringBuilder sb = new StringBuilder();
        for (boolean b : array) sb.append(b ? '1' : '0');
        return sb.toString();
    }

    public static boolean[] stringToBoolArr(String str) {
        if(str.matches("[01]+")) {
            boolean[] answer = new boolean[str.length()];
            for(int i = 0; i < str.length(); i++) {
                answer[i] = str.charAt(i) == '1';
            }

            return answer;
        } else {
            return null;
        }
    }
}

class LSRF {
    class Bit {
        boolean bit;
        Bit next;

        Bit(boolean in) {
            bit = in;
            next = null;
        }
    }

    Bit first, last;

    LSRF(boolean[] lsrf) {
        for (boolean b : lsrf) add(b);
    }

    private void add(boolean bit) {
        if(first == null) {
            first = last = new Bit(bit);
        } else {
            last.next = new Bit(bit);
            last = last.next;
        }
    }

    boolean getNext() {
        boolean returnable = first.bit;
        first = first.next;

        last.next = new Bit(returnable ^ last.bit);
        last = last.next;

        return returnable;
    }
}
