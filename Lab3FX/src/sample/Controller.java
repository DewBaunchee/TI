package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import sample.cypher.Keys;
import sample.cypher.PrivateKey;
import sample.cypher.PublicKey;
import sample.cypher.RabinCypher;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Controller {

    @FXML
    private TextArea keysArea;

    @FXML
    private AnchorPane inputPane;

    @FXML
    private TextField pField;

    @FXML
    private TextField qField;

    @FXML
    private TextField nField;

    @FXML
    private TextField bField;

    @FXML
    private Text errorText;

    @FXML
    private TextField keyLengthField;

    @FXML
    private Button generateBtn;

    @FXML
    private Button encryptBtn;

    @FXML
    private Button decryptBtn;

    @FXML
    private RadioButton generateRB;

    @FXML
    private RadioButton inputRB;

    @FXML
    private TextField pathToFileField;

    @FXML
    private Button browseBtn;

    @FXML
    private TextArea outputTextArea;

    @FXML
    void initialize() {
        nField.setEditable(false);
        inputPane.setVisible(false);
        errorText.setText("");
        disable();

        keyLengthField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9]{0,3}")) {
                keyLengthField.setText(oldValue);
            }
        });

        pathToFileField.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue.length() > 0) {
                enable();
            } else {
                disable();
            }
        }));

        pField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("[0-9]*")) {
                pField.setText(oldValue);
                return;
            }
            if(newValue.length() == 0) return;
            if(!new BigInteger(newValue).isProbablePrime(100)) {
                errorText.setText("p is not prime");
                return;
            }
            if(new BigInteger(newValue).mod(new BigInteger("4")).compareTo(new BigInteger("3")) != 0) {
                errorText.setText("p mod 4 must be 3");
                return;
            }
            errorText.setText("");
            if(pField.getText().length() > 0 && qField.getText().length() > 0) {
                nField.setText(new BigInteger(pField.getText())
                        .multiply(new BigInteger(qField.getText()))
                        .toString());
            }
        });

        qField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("[0-9]*")) {
                qField.setText(oldValue);
                return;
            }
            if(newValue.length() == 0) return;
            if(!new BigInteger(newValue).isProbablePrime(100)) {
                errorText.setText("q is not prime");
                return;
            }
            if(new BigInteger(newValue).mod(new BigInteger("4")).compareTo(new BigInteger("3")) != 0) {
                errorText.setText("q mod 4 must be 3");
                return;
            }
            errorText.setText("");
            if(pField.getText().length() > 0 && qField.getText().length() > 0) {
                nField.setText(new BigInteger(pField.getText())
                        .multiply(new BigInteger(qField.getText()))
                        .toString());
            }
        });

        bField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("[0-9]*")) {
                bField.setText(oldValue);
                return;
            }
            if(newValue.length() == 0) return;
            if(new BigInteger(newValue).compareTo(new BigInteger(nField.getText())) > -1) {
                errorText.setText("b is not lesser than n");
                return;
            }
            errorText.setText("");
        });

        generateRB.setOnAction(actionEvent -> {
            inputPane.setVisible(false);
        });

        inputRB.setOnAction(actionEvent -> {
            inputPane.setVisible(true);
        });

        encryptBtn.setOnAction(actionEvent -> {
            Keys keys;
            if (generateRB.isSelected()) {
                keys = Keys.getKeys();
            } else {
                keys = getKeysFromInput();
            }
            if (keys == null) return;
            try {
                String text = pathToFileField.getText();
                Path outputFile;
                byte[] plain;

                if (Files.exists(Paths.get(text))) {
                    plain = Files.readAllBytes(Paths.get(text));
                    outputFile = getModifiedPath(text, "_locked");
                } else {
                    plain = text.getBytes();
                    outputFile = Paths.get("temp.rbn");
                }

                byte[] cypher = getCypherBytes(RabinCypher.encrypt(plain, keys.getPublicKey()));
                showAnswer(cypher);
                Files.write(outputFile, cypher);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        decryptBtn.setOnMouseClicked(actionEvent -> {
            Keys keys;
            if (generateRB.isSelected()) {
                keys = Keys.getKeys();
            } else {
                keys = getKeysFromInput();
            }
            if(keys == null) return;
            try {
                String text = pathToFileField.getText();
                Path outputFile;
                Scanner scanner;

                if (Files.exists(Paths.get(text))) {
                    scanner = new Scanner(new ByteArrayInputStream(Files.readAllBytes(Paths.get(text))));
                    outputFile = getModifiedPath(text, "_unlocked");
                } else {
                    scanner = new Scanner(new ByteArrayInputStream(text.getBytes()));
                    outputFile = Paths.get("temp.rbn");
                }

                BigInteger[] cypher = new BigInteger[scanner.nextInt()];
                for (int i = 0; i < cypher.length; i++) {
                    cypher[i] = new BigInteger(scanner.next());
                }
                scanner.close();
                StringBuilder sb = new StringBuilder();
                byte[] plain = RabinCypher.decrypt(cypher, keys, sb);

                outputTextArea.setText(sb.toString());
                Files.write(outputFile, plain);
                showAnswer(plain);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        generateBtn.setOnAction(actionEvent -> {
            if (keyLengthField.getText().length() == 0) {
                Keys.setKeys(RabinCypher.generateKeys());
            } else {
                Keys.setKeys(RabinCypher.generateKeys(Integer.parseInt(keyLengthField.getText())));
            }
            keysArea.setText(Keys.getKeys().toString());
        });

        browseBtn.setOnAction(actionEvent -> {
            File file = askFile("Choose plain file", browseBtn.getParent().getScene().getWindow());
            if (file != null) {
                pathToFileField.setText(file.getAbsolutePath());
            }
        });
    }

    private Keys getKeysFromInput() {
        String error = "";
        if(pField.getText().length() == 0) error += "Enter p";
        if(qField.getText().length() == 0) error += "\nEnter q";
        if(nField.getText().length() == 0) error += "\nEnter n";
        if(bField.getText().length() == 0) error += "\nEnter b";

        if (error.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(error);
            alert.showAndWait();
            return null;
        }

        BigInteger p = new BigInteger(pField.getText());
        BigInteger q = new BigInteger(qField.getText());
        BigInteger n = new BigInteger(nField.getText());
        BigInteger b = new BigInteger(bField.getText());

        if (!p.isProbablePrime(100)) error += "\np is not prime";
        if(p.mod(new BigInteger("4")).compareTo(new BigInteger("3")) != 0) error += "\np mod 4 must be 3";
        if (!q.isProbablePrime(100)) error += "\nq is not prime";
        if(q.mod(new BigInteger("4")).compareTo(new BigInteger("3")) != 0) error += "\nq mod 4 must be 3";
        if (b.compareTo(n) > -1) error += "\nb is not lesser than n";
        if (n.compareTo(new BigInteger("256")) < 0) error += "\nn must be lesser than 256";

        if (error.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(error);
            alert.showAndWait();
            return null;
        }

        return new Keys(new PublicKey(n, b), new PrivateKey(p, q));
    }

    private void showAnswer(byte[] answer) {
        TextInputDialog answerWindow = new TextInputDialog();
        answerWindow.setTitle("Answer");
        String str = new String(answer);
        answerWindow.getEditor().setText(str.substring(0, Math.min(str.length(), 200)));
        answerWindow.getDialogPane().getButtonTypes().remove(1);
        answerWindow.showAndWait();
    }

    private File askFile(String title, Window window) {
        FileChooser fc = new FileChooser();
        fc.setTitle(title);
        fc.setInitialDirectory(new File("."));
        return fc.showOpenDialog(window);
    }

    void enable() {
        encryptBtn.setDisable(false);
        decryptBtn.setDisable(false);
    }

    void disable() {
        encryptBtn.setDisable(true);
        decryptBtn.setDisable(true);
    }

    private Path getModifiedPath(String text, String modifying) {
        if (text == null) return null;

        int insertPlace = text.lastIndexOf(".") == -1 ? text.length() : text.lastIndexOf(".");
        return Paths.get(text.substring(0, insertPlace) + modifying + text.substring(insertPlace));
    }

    byte[] getCypherBytes(BigInteger[] cypher) {
        StringBuilder answer = new StringBuilder(cypher.length + "");
        for (BigInteger bigInteger : cypher) {
            answer.append(" ").append(bigInteger);
        }
        return answer.toString().getBytes();
    }
}