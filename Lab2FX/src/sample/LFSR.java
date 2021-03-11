package sample;

public class LFSR {
    class Bit {
        boolean bit;
        Bit next;

        Bit(boolean in) {
            bit = in;
            next = null;
        }
    }

    Bit first, last;

    public LFSR(boolean[] lfsr) {
        if(lfsr == null) return;
        for (boolean b : lfsr) add(b);
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

    @Override
    public String toString() {
        if(first == null) return "Empty";
        Bit current = first;
        StringBuilder answer = new StringBuilder();
        while(current != null) {
            answer.append(current.bit ? "1" : "0");
            current = current.next;
        }
        return answer.toString();
    }
}
