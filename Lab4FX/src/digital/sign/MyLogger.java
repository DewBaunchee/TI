package digital.sign;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class MyLogger extends Thread {

    private static MyLogger logger;

    public static MyLogger getLogger() {
        if (logger == null) {
            logger = new MyLogger();
        }
        return logger;
    }

    public static void setLogger(MyLogger myLogger) {
        logger = myLogger;
    }

    private BufferedWriter bw;
    private Path logFile;
    private Event onMessageAction;

    public MyLogger() {
        this.bw = null;
        logFile = null;
        onMessageAction = null;
    }

    public MyLogger setOnMessageAction(Event event) {
        onMessageAction = event;
        return this;
    }

    public MyLogger setLogFile(Path path) {
        logFile = path;
        return this;
    }

    public MyLogger setLogStream(OutputStream logStream) {
        if (bw != null) {
            try {
                bw.close();
            } catch (IOException ignored) {
            }
        }
        bw = new BufferedWriter(new OutputStreamWriter(logStream));
        return this;
    }

    public void close() throws IOException {
        if (bw != null)
            bw.close();
    }

    public void log(String message) {
        System.out.println(message);

        if (bw != null) {
            try {
                bw.write(message + "\n");
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(getClass() + ".log: " + e.getMessage());
                try {
                    bw.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                bw = null;
            }
        }

        if (logFile != null && Files.exists(logFile)) {
            try {
                Files.write(logFile, (message + "\n").getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(getClass() + ".log: " + e.getMessage());
            }
        }

        if (onMessageAction != null) onMessageAction.handle(message);
    }

    public interface Event {
        void handle(String message);
    }
}
