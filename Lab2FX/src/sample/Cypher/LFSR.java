package sample.Cypher;

//x34 + x15 + x14 + x + 1
public class LFSR {
    static class Bit {
        boolean bit;
        Bit prev;

        Bit(boolean in) {
            bit = in;
            prev = null;
        }
    }

    private Bit left, b15, b14, right;

    public LFSR(boolean[] lfsr) {
        if(lfsr == null) return;
        int count = 0;
        for (boolean bit : lfsr) {
            count++;
            if(right == null) {
                right = left = new Bit(bit);
            } else {
                Bit newBit = new Bit(bit);
                newBit.prev = left;
                left = newBit;
                if(count == 14) b14 = left;
                if(count == 15) b15 = left;
            }
        }
    }

    public boolean getNext() {
        boolean returnable = left.bit;
        left = left.prev;
        b15 = b15.prev;
        b14 = b14.prev;

        right.prev = new Bit(returnable ^ b15.bit ^ b14.bit ^ right.bit);
        right = right.prev;

        return returnable;
    }

    @Override
    public String toString() {
        if(left == null) return "Empty";
        Bit current = left;
        StringBuilder answer = new StringBuilder();
        while(current != null) {
            answer.append(current.bit ? "1" : "0");
            current = current.prev;
        }
        return answer.toString();
    }

    public String toString(String open, String close) {
        if(left == null) return "Empty";
        Bit current = left;
        StringBuilder answer = new StringBuilder();
        int count = 34;
        while(current != null) {
            if(count == 1 || count == 14 || count == 15 || count == 34) {
                answer.append(open).append(current.bit ? "1" : "0").append(close);
            } else {
                answer.append(current.bit ? "1" : "0");
            }
            current = current.prev;
            count--;
        }
        return answer.toString();
    }
}
