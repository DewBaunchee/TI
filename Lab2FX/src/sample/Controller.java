package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.FileChooser;
import sample.Cypher.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

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
            if (fileField.getText().length() > 0 && Files.exists(Paths.get(fileField.getText())) && keyField.getText().length() > 0) {
                executeTransform(Paths.get(fileField.getText()), keyField.getText());
            } else {
                opFile = null;
                showMsg("Error", "Fill all fields.", Alert.AlertType.ERROR);
            }
        });

        browseBtn.setOnAction(actionEvent -> {
            File file = openFile("Choose plain file");
            if (file != null) fileField.setText(file.getAbsolutePath());
        });

        printBtn.setOnAction(actionEvent -> {
            if ((opFile = new File(fileField.getText())).exists() && keyField.getText().length() > 1) {

                Platform.runLater(() -> {
                    try {
                        byte[] plain = Files.readAllBytes(Paths.get(fileField.getText()));
                        if(plain.length > 1000) {
                            printLog(StreamCypher.transformForLog(Arrays.copyOfRange(plain, 0, 1000), keyField.getText()));
                        } else {
                            printLog(StreamCypher.transformForLog(plain, keyField.getText()));
                        }
                        byte[] answer = StreamCypher.transformByteArray(plain, keyField.getText());
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
            if (!newValue.matches("[01]*")) {
                keyField.setText(oldValue);
                return;
            }
            if (newValue.length() == 34) {
                enableBtns();
            } else {
                disableBtns();
            }
            lengthLabel.setText("LFSR length: " + newValue.length());
        });

        printKeyBtn.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton() == MouseButton.PRIMARY) {
                if (keyField.getText().length() > 3) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Полином: x^34 + x^15 + x^14 + x + 1").append("\n");
                    sb.append("Начальное состояние: ").append(keyField.getText()).append("\n\n");

                    LFSR lfsr = new LFSR(StreamCypher.stringToBoolArr(keyField.getText()));
                    for (int i = 0; i < keyField.getText().length() + 20; i++) {
                        sb.append("После такта №").append(i + 1).append(":\n");
                        sb.append("Сгенерированный ключ: ").append(lfsr.getNext() ? "1" : "0").append("\n");
                        if(mouseEvent.getClickCount() != 2) {
                            sb.append("Состояние регистра: ").append(lfsr.toString("(", ")")).append("\n\n\n");
                        } else {
                            sb.append("Состояние регистра: ").append(lfsr.toString()).append("\n\n\n");
                        }
                    }

                    printLog(sb.toString());
                } else {
                    showMsg("Error", "Enter key with length that bigger than 3.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void enableBtns() {
        printKeyBtn.setDisable(false);
        printBtn.setDisable(false);
        transformBtn.setDisable(false);
    }

    private void disableBtns() {
        printKeyBtn.setDisable(true);
        printBtn.setDisable(true);
        transformBtn.setDisable(true);
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

    private void executeTransform(Path plain, String startValue) {
        StringBuilder outputPath = new StringBuilder(plain.toString());
        outputPath.insert(outputPath.lastIndexOf("."), "_transformed");
        new Thread(() -> {
            try {
                byte[] plainArr = Files.readAllBytes(plain);
                byte[] cypherArr = StreamCypher.transformByteArray(plainArr, startValue);
                if (cypherArr != null)
                    Files.write(Paths.get(outputPath.toString()), cypherArr);
                Platform.runLater(() -> showMsg("Cyphered", "Done!", Alert.AlertType.INFORMATION));
            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> showMsg("Error", "Error: " + e.getMessage(), Alert.AlertType.INFORMATION));
            }
        }).start();
    }

    public File openFile(String title) {
        FileChooser fc = new FileChooser();
        fc.setTitle(title);
        fc.setInitialDirectory(new File("."));
        return fc.showOpenDialog(Main.root.getScene().getWindow());
    }

    public static void showMsg(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}