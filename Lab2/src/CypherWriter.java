import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CypherWriter extends Thread {
    public final BlockingQueue<Byte> src;
    public final BlockingQueue<Byte> dest;
    public final StreamCypher sc;

    public CypherWriter(StreamCypher inSC) {
        src = new LinkedBlockingQueue<>();
        dest = new LinkedBlockingQueue<>();
        sc = inSC;
        start();
    }

    @Override
    public void run() {
        while(!isInterrupted()) {
            try {
                dest.put(sc.encrypt(src.take()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public BlockingQueue<Byte> getInput() {
        return src;
    }

    public BlockingQueue<Byte> getOutput() {
        return dest;
    }
}
