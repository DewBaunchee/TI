package digital.sign.ui;

import digital.sign.MyLogger;
import digital.sign.implementation.DigitalSign;
import digital.sign.implementation.DigitalSigner;
import digital.sign.implementation.Hasher.Custom;
import digital.sign.implementation.Hasher.Hasher;
import digital.sign.implementation.Hasher.SHA1;
import digital.sign.implementation.MillerRabin;
import digital.sign.implementation.SignCheckResult;
import digital.sign.implementation.keys.Keys;
import digital.sign.implementation.keys.PrivateKey;
import digital.sign.implementation.keys.PublicKey;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

public class Controller {

    @FXML
    private TextArea logArea;

    @FXML
    private TextField qField;

    @FXML
    private TextField pField;

    @FXML
    private TextField hField;

    @FXML
    private TextField xField;

    @FXML
    private TextField kField;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField pathToFileField;

    @FXML
    private Button browseBtn;

    @FXML
    private RadioButton customHasherChoose;

    @FXML
    private RadioButton sha1Choose;

    @FXML
    private Button signBtn;

    @FXML
    private Button checkSignBtn;

    @FXML
    private Button generateBtn;

    private static final int CERTAINTY = 100;

    @FXML
    void initialize() {
        MyLogger.setLogger(
                new MyLogger().setOnMessageAction(
                        (message) -> Platform.runLater(
                                () -> logArea.appendText(message + "\n")
                        )
                )
        );
        checkFields();

        qField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            checkEntered(qField, oldValue, newValue);
            checkFields();
        });

        pField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            checkEntered(pField, oldValue, newValue);
            checkFields();
        });

        hField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            checkEntered(hField, oldValue, newValue);
            checkFields();
        });

        xField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            checkEntered(xField, oldValue, newValue);
            checkFields();
        });

        kField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            checkEntered(kField, oldValue, newValue);
            checkFields();
        });

        browseBtn.setOnAction(actionEvent -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Choose file for signing...");
            fc.setInitialDirectory(new File("").getAbsoluteFile());
            pathToFileField.setText(fc.showOpenDialog(browseBtn.getScene().getWindow()).getAbsolutePath());
        });

        signBtn.setOnAction(actionEvent -> {
            try {
                Path path = Paths.get(pathToFileField.getText());
                Path modifiedPath = getModifiedPath(path, "_signed");
                byte[] bytes = Files.readAllBytes(path);

                BigInteger x = new BigInteger(xField.getText());
                BigInteger k = new BigInteger(kField.getText());

                PublicKey publicKey = getPublicKey();
                if (publicKey == null) return;

                PrivateKey privateKey = new PrivateKey(x);
                DigitalSign sign = DigitalSigner.getSign(bytes, publicKey, privateKey, k, Objects.requireNonNull(getHasher()));

                byte[] signBytes = (sign + ";").getBytes(StandardCharsets.UTF_8);
                byte[] signed = new byte[signBytes.length + bytes.length];
                System.arraycopy(signBytes, 0, signed, 0, signBytes.length);
                System.arraycopy(bytes, 0, signed, signBytes.length, bytes.length);

                Files.write(modifiedPath, signed);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        checkSignBtn.setOnAction(actionEvent -> {
            try {
                Path path = Paths.get(pathToFileField.getText());
                byte[] bytes = Files.readAllBytes(path);

                DigitalSign sign = null;
                byte[] msg = bytes;
                for (int i = 0; i < bytes.length; i++) {
                    if (bytes[i] == ';') {
                        sign = new DigitalSign(new String(Arrays.copyOfRange(bytes, 0, i)));
                        msg = Arrays.copyOfRange(bytes, i + 1, bytes.length);
                        break;
                    }
                }
                if (sign == null) {
                    alertMsg("Error", "No sign", Alert.AlertType.ERROR);
                    return;
                }

                PublicKey publicKey = getPublicKey();
                if (publicKey == null) return;

                SignCheckResult result = DigitalSigner.checkSign(msg, sign, publicKey, Objects.requireNonNull(getHasher()));
                alertMsg("Sign check result",
                        result.get() ? "Sign is correct" : "Sign is incorrect",
                        Alert.AlertType.INFORMATION);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        generateBtn.setOnAction(actionEvent -> {
            Keys keys = DigitalSigner.generateKeys(16);
            qField.setText(keys.getPublicKey().getQ().toString());
            pField.setText(keys.getPublicKey().getP().toString());
            hField.setText(
                    DigitalSigner.randomBetween(
                            16,
                            BigInteger.TWO,
                            keys.getPublicKey().getP().subtract(BigInteger.TWO)
                    ).toString()
            );
            xField.setText(keys.getPrivateKey().getX().toString());
            kField.setText(
                    DigitalSigner.randomBetween(
                            16,
                            BigInteger.ONE,
                            keys.getPublicKey().getQ().subtract(BigInteger.ONE)
                    ).toString()
            );
        });
    }

    private Hasher getHasher() {
        if (customHasherChoose.isSelected()) return new Custom();
        if (sha1Choose.isSelected()) return new SHA1();
        return null;
    }

    private PublicKey getPublicKey() {
        BigInteger p = new BigInteger(pField.getText());
        BigInteger q = new BigInteger(qField.getText());
        BigInteger h = new BigInteger(hField.getText());
        BigInteger x = new BigInteger(xField.getText());

        BigInteger g = h.modPow(p.subtract(BigInteger.ONE).divide(q), p);
        if (g.compareTo(BigInteger.ONE) < 1) {
            alertMsg("Error", "G is not bigger than 1. Enter another H.", Alert.AlertType.ERROR);
            return null;
        }
        BigInteger y = g.modPow(x, p);

        return new PublicKey(p, q, g, y);
    }

    private void alertMsg(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private Path getModifiedPath(Path plainPath, String modifier) {
        String path = plainPath.toString();
        int dotIndex = path.lastIndexOf(".");
        int slashIndex = path.lastIndexOf("/");

        int insertIndex = (dotIndex > -1 && dotIndex > slashIndex) ? dotIndex : -1;
        if (insertIndex == -1) {
            return Paths.get(path + modifier);
        } else {
            return Paths.get(path.substring(0, insertIndex) + modifier + path.substring(insertIndex));
        }
    }

    private void checkEntered(TextField field, String oldValue, String newValue) {
        if (!newValue.matches("[0-9]*")) {
            field.setText(oldValue);
        }
    }

    private void checkFields() {
        StringBuilder errorText = new StringBuilder();

        BigInteger q = null;
        BigInteger p = null;
        BigInteger h;
        BigInteger x;
        BigInteger k;

        if (qField.getText().length() == 0) {
            errorText.append("Enter Q; ");
        } else {
            q = new BigInteger(qField.getText());
            if (!MillerRabin.isProbablePrime(q, CERTAINTY)) {
                errorText.append("Q is not prime; ");
            }
        }

        if (pField.getText().length() == 0) {
            errorText.append("Enter P; ");
        } else {
            p = new BigInteger(pField.getText());
            if (!MillerRabin.isProbablePrime(p, CERTAINTY)) {
                errorText.append("P is not prime; ");
            }
            if(q != null) {
                if (p.subtract(BigInteger.ONE).mod(q).compareTo(BigInteger.ZERO) != 0) {
                    errorText.append("Q must be divider of P - 1; ");
                }
            }
        }

        if (hField.getText().length() == 0) {
            errorText.append("Enter H; ");
        } else {
            if (p != null) {
                h = new BigInteger(hField.getText());
                if (h.compareTo(BigInteger.ONE) < 1 || h.compareTo(p.subtract(BigInteger.ONE)) > -1) {
                    errorText.append("1 < H < P - 1; ");
                }
            }
        }

        if (xField.getText().length() == 0) {
            errorText.append("Enter X; ");
        } else {
            if (q != null) {
                x = new BigInteger(xField.getText());
                if (x.compareTo(BigInteger.ZERO) < 1 || x.compareTo(q) > -1) {
                    errorText.append("0 < X < Q; ");
                }
            }
        }

        if (kField.getText().length() == 0) {
            errorText.append("Enter K; ");
        } else {
            if (q != null) {
                k = new BigInteger(kField.getText());
                if (k.compareTo(BigInteger.ZERO) < 1 || k.compareTo(q) > -1) {
                    errorText.append("0 < K < Q; ");
                }
            }
        }

        errorLabel.setText(errorText.toString());
        if (errorText.length() == 0) {
            btnEnable();
        } else {
            btnDisable();
        }
    }

    private void btnDisable() {
        signBtn.setDisable(true);
        checkSignBtn.setDisable(true);
    }

    private void btnEnable() {
        signBtn.setDisable(false);
        checkSignBtn.setDisable(false);
    }
}
