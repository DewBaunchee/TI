package sample.Cypher;

import java.io.*;

/*
CypherSocket cs = new CypherSocket(new StreamCypher(StreamCypher.stringToBoolArr(startValue)));
new TransformThreadWriter(plain.toFile(), cs).start();
new TransformThreadReader(new File(outputPath.toString()), cs).start();
*/
public class TransformThreadWriter extends Thread {
    private final File plain;
    private final OutputStream outputStream;

    public TransformThreadWriter(File inPlain, CypherSocket inCS) {
        plain = inPlain;
        outputStream = inCS.getOutput();
    }

    @Override
    public void run() {
        try (BufferedReader fromFile = new BufferedReader(new FileReader(plain)); BufferedWriter toCypher = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            while (!isInterrupted()) {
                int part = fromFile.read();
                toCypher.write(part);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("Plain reader is closed.");
    }
}
