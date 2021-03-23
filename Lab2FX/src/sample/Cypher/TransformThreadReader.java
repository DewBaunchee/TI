package sample.Cypher;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import sample.Controller;

import java.io.*;

public class TransformThreadReader extends Thread {
    private final File cyphered;
    private final InputStream inputStream;

    public TransformThreadReader(File inCyphered, CypherSocket inCS) {
        cyphered = inCyphered;
        inputStream = inCS.getInput();
    }

    @Override
    public void run() {
        try (BufferedWriter toFile = new BufferedWriter(new FileWriter(cyphered)); BufferedReader fromCypher = new BufferedReader(new InputStreamReader(inputStream))) {
            while (!isInterrupted()) {
                if (fromCypher.ready()) {
                    int part = fromCypher.read();
                    toFile.write(part);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("Cypher writer is closed.");
        Platform.runLater(() -> Controller.showMsg("Encrypting", "Done!", Alert.AlertType.INFORMATION));
    }
}