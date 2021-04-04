package sample.Cypher;

import java.io.*;
import java.text.DecimalFormat;

public class CypherSocket extends Thread {
    public final PipedInputStream pis;
    public final PipedOutputStream pos;
    public final BufferedReader src;
    public final BufferedWriter dest;
    public final StreamCypher sc;

    public CypherSocket(StreamCypher inSC) throws IOException {
        pis = new PipedInputStream();
        pos = new PipedOutputStream();
        pos.connect(pis);

        src = new BufferedReader(new InputStreamReader(pis));
        dest = new BufferedWriter(new OutputStreamWriter(pos));
        sc = inSC;
        start();
    }

    @Override
    public void run() {
        long byteIndex = 1;
        DecimalFormat df = new DecimalFormat("###,###,###");
        while (!isInterrupted()) {
            try {
                if (src.ready()) {
                    int part = src.read();
                    dest.write(sc.transformByte((byte) part));
                    System.out.println("Byte #" + df.format(byteIndex++));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        interrupt();
    }

    public InputStream getInput() {
        return pis;
    }

    public OutputStream getOutput() {
        return pos;
    }
}
