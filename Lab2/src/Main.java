import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        byte[] array = new byte[]{
                -1, 2, 12, -100, 23
        };
        System.out.println(Arrays.toString(StreamCypher.encryptByteArray(array, "1111")));

        CypherWriter cw = new CypherWriter(new StreamCypher(StreamCypher.stringToBoolArr("1111")));
        BlockingQueue<Byte> toCW = cw.getInput();
        BlockingQueue<Byte> fromCW = cw.getOutput();

        for (byte b : array) {
            toCW.put(b);
            System.out.println(fromCW.take());
        }
    }
}
