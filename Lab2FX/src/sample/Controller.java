package sample;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

public class Controller {

    @FXML
    private TextField fileField;

    @FXML
    private Button transformBtn;

    @FXML
    private Button browseBtn;

    @FXML
    private Button printBtn;

    @FXML
    private Button printKeyBtn;

    @FXML
    private Button RC4Btn;

    @FXML
    private TextField keyField;

    @FXML
    private Label lengthLabel;

    @FXML
    private TextArea logArea;

    public static File opFile = null;

    @FXML
    void initialize() {
        transformBtn.setOnAction(actionEvent -> {
            if ((opFile = new File(fileField.getText())).exists() && keyField.getText().length() > 1) {
                executeTransform(opFile, keyField.getText());
            } else {
                opFile = null;
                showMsg("Error", "Fill all fields.", Alert.AlertType.ERROR);
            }
        });

        browseBtn.setOnAction(actionEvent -> {
            File file = openFile("Choose plainfile");
            if (file != null) fileField.setText(file.getAbsolutePath());
        });

        printBtn.setOnAction(actionEvent -> {
            if ((opFile = new File(fileField.getText())).exists() && keyField.getText().length() > 1) {

                Platform.runLater(() -> {
                    try {
                        printLog(StreamCypher.transformForLog(Files
                                        .readAllBytes(Paths.get(fileField.getText())), keyField.getText()));
                        byte[] answer = StreamCypher.transformByteArray(Files
                                        .readAllBytes(Paths.get(fileField.getText())), keyField.getText());
                        if (answer != null)
                            Files.write(Paths.get("temp.txt"), answer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } else {
                opFile = null;
                showMsg("Error", "Fill all fields.", Alert.AlertType.ERROR);
            }
        });

        RC4Btn.setOnAction(actionEvent -> {
            if (fileField.getText().length() > 0) {
                String key = dialog("Key for RC4", "Enter key:");

                RC4 rc4 = new RC4(key.getBytes());
                String plain = fileField.getText();
                byte[] cyphered = rc4.transform(plain.getBytes());

                rc4 = new RC4(key.getBytes());
                String decyphered = new String(rc4.transform(cyphered));

                byte[] wrongKey = new StringBuilder(key).reverse().toString().getBytes();
                rc4 = new RC4(wrongKey);
                String wrongDecyphered = new String(rc4.transform(cyphered));

                logArea.appendText("Key: " + Arrays.toString(key.getBytes()) + "\n");
                logArea.appendText("Plain: " + plain + "\n");
                logArea.appendText("Cyphered: " + new String(cyphered) + "\n");
                logArea.appendText("Decyphered: " + decyphered + "\n");
                logArea.appendText("Wrong key: " + Arrays.toString(wrongKey) + "\n");
                logArea.appendText("Wrong decyphered: " + wrongDecyphered + "\n");
            } else {
                showMsg("Error", "Fill file field by plaintext.", Alert.AlertType.ERROR);
            }
        });

        keyField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[01]*")) keyField.setText(oldValue);
            lengthLabel.setText("LFSR length: " + newValue.length());
        });

        printKeyBtn.setOnAction(actionEvent -> {
            if(keyField.getText().length() > 2) {
                StringBuilder sb = new StringBuilder();
                sb.append("Полином: a^").append(keyField.getText().length()).append(" + a^1 + 1").append("\n");
                sb.append("Начальное состояние: ").append(keyField.getText()).append("\n\n");

                LFSR lfsr = new LFSR(StreamCypher.stringToBoolArr(keyField.getText()));
                for(int i = 0; i < keyField.getText().length() + 20; i++) {
                    sb.append("После такта №").append(i + 1).append(":\n");
                    sb.append("Сгенерированный ключ: ").append(lfsr.getNext() ? "1" : "0").append("\n");
                    sb.append("Состояние регистра: ").append(lfsr.toString()).append("\n\n");
                }

                printLog(sb.toString());
            } else {
                showMsg("Error", "Enter key.", Alert.AlertType.ERROR);
            }
        });
    }

    private void printLog(String message) {
        logArea.setText(message);
    }

    public String dialog(String title, String question) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(question);
        dialog.getDialogPane().setMinWidth(400);

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private void executeTransform(File plain, String startValue) {
        StringBuilder outputPath = new StringBuilder(plain.getAbsolutePath());
        outputPath.insert(outputPath.lastIndexOf("."), "_transformed");
        CypherSocket cs = new CypherSocket(new StreamCypher(StreamCypher.stringToBoolArr(startValue)));
        new TransformThreadWriter(plain, cs).start();
        new TransformThreadReader(new File(outputPath.toString()), cs).start();
    }

    public File openFile(String title) {
        FileChooser fc = new FileChooser();
        fc.setTitle(title);
        fc.setInitialDirectory(new File("."));
        return fc.showOpenDialog(Main.root.getScene().getWindow());
    }

    public void showMsg(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    class TransformThreadReader extends Thread {
        private final File cyphered;
        private final CypherSocket socket;

        public TransformThreadReader(File inCyphered, CypherSocket inCS) {
            cyphered = inCyphered;
            socket = inCS;
        }

        @Override
        public void run() {
            BlockingQueue<Byte> fromCypher = socket.getInput();

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(cyphered);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                interrupt();
            }

            if (fos == null) {
                socket.interrupt();
                return;
            }

            try {
                while (!socket.isInterrupted() || fromCypher.size() > 0) {
                    Byte part = fromCypher.poll(2500, TimeUnit.MILLISECONDS);
                    if(part == null) break;
                    fos.write(part);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            try {
                fos.close();
                System.out.println("Cypher writer is closed.");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> showMsg("Encrypting", "Done!", Alert.AlertType.INFORMATION));
        }
    }

    class TransformThreadWriter extends Thread {
        private final File plain;
        private final CypherSocket socket;

        public TransformThreadWriter(File inPlain, CypherSocket inCS) {
            plain = inPlain;
            socket = inCS;
        }

        @Override
        public void run() {
            BlockingQueue<Byte> toCypher = socket.getOutput();

            FileInputStream fis = null;
            try {
                fis = new FileInputStream(plain);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                interrupt();
            }

            if (fis == null) {
                socket.interrupt();
                return;
            }

            try {
                while (!isInterrupted()) {
                    if (fis.available() == 0) {
                        socket.interrupt();
                        break;
                    }
                    int part = fis.read();
                    System.out.println(part);
                    toCypher.put((byte) part);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            while (toCypher.size() > 0) ;

            try {
                fis.close();
                System.out.println("Plain reader is closed.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}