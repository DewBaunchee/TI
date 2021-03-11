package sample;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CypherSocket extends Thread {
    public final BlockingQueue<Byte> src;
    public final BlockingQueue<Byte> dest;
    public final StreamCypher sc;

    public CypherSocket(StreamCypher inSC) {
        src = new LinkedBlockingQueue<>();
        dest = new LinkedBlockingQueue<>();
        sc = inSC;
        start();
    }

    @Override
    public void run() {
        while(!isInterrupted() || src.size() > 0) {
            try {
                int part = src.take();
                dest.put(sc.transformByte((byte) part));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        interrupt();
    }

    public BlockingQueue<Byte> getOutput() {
        return src;
    }

    public BlockingQueue<Byte> getInput() {
        return dest;
    }
}
