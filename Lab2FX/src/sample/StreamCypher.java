package sample;

public class StreamCypher {
    public final boolean[] startValue;
    public LFSR lfsr;

    public StreamCypher(boolean[] inStartValue) {
        startValue = inStartValue;
        lfsr = new LFSR(startValue);
    }

    public byte transformByte(byte current) {
        byte keyByte = getKeyByte(lfsr);
        return (byte) (current ^ keyByte);
    }

    private static byte getKeyByte(LFSR lfsr) {
        byte answer = 0;
        for(int i = 0; i < 7; i++) {
            answer = (byte) (answer | (lfsr.getNext() ? 1 : 0));
            answer = (byte) (answer << 1);
        }
        return (byte) (answer | (lfsr.getNext() ? 1 : 0));
    }

    public static byte[] transformByteArray(byte[] array, String startLSRF) {
        boolean[] startKey = stringToBoolArr(startLSRF);
        if(startKey == null) return null;
        LFSR lfsr = new LFSR(startKey);

        for(int i = 0; i < array.length; i++) {
            byte keyByte = getKeyByte(lfsr);
            array[i] = (byte) (array[i] ^ keyByte);
        }
        return array;
    }

    public static String transformForLog(byte[] array, String startLSRF) {
        boolean[] startKey = stringToBoolArr(startLSRF);
        if(startKey == null) return null;
        LFSR lfsr = new LFSR(startKey);
        StringBuilder answer = new StringBuilder();

        for(int i = 0; i < array.length; i++) {
            byte keyByte = getKeyByte(lfsr);
            answer.append(toBit(array[i])).append("(")
                    .append((char) array[i]).append(") ^ ")
                    .append(toBit(keyByte)).append(" = ");

            array[i] = (byte) (array[i] ^ keyByte);
            answer.append(toBit(array[i])).append("(")
                    .append((char) array[i]).append(")\n");
        }
        return answer.toString();
    }

    public static String toBit(byte b) {
        StringBuilder answer = new StringBuilder(Integer.toBinaryString(b));
        if(answer.length() > 8) {
            answer = new StringBuilder(answer.substring(24));
        } else {
            while (answer.length() < 8) answer.insert(0, "0");
        }
        return answer.toString();
    }

    public static boolean[] getWholeKey(int length, boolean[] startValue) {
        if(startValue == null) return null;

        LFSR lfsr = new LFSR(startValue);
        boolean[] key = new boolean[length];

        for(int i = 0; i < length; i++) {
            key[i] = lfsr.getNext();
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