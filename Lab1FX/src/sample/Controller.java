package sample;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.util.*;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

public class Controller {

    public Controller() {
    }

    private class MyTimer extends AnimationTimer {
        @Override
        public void handle(long now) {
            doHandle();
        }

        private void doHandle() {
            hintPane.setOpacity(hintPane.getOpacity() - 0.02);
            if (hintPane.getOpacity() <= 0) {
                stop();
            }
        }
    }

    public static File opFile, buffFile;
    public static int encryptionMethod = 1;
    public static int language = 2;

    @FXML
    private Button exitBtn;

    @FXML
    private Button helpBtn;

    @FXML
    private Button browseBtn;

    @FXML
    private Button genBtn;

    @FXML
    private Button encryptBtn;

    @FXML
    private Button decryptBtn;

    @FXML
    private Button chooseBtn;

    @FXML
    private ImageView folderBtn;

    @FXML
    private ImageView keyBtn;

    @FXML
    private ImageView eyeBtn;

    @FXML
    private MenuItem mmOpen;

    @FXML
    private MenuItem mmChoose;

    @FXML
    private MenuItem mmExit;

    @FXML
    private MenuItem mmEncrypt;

    @FXML
    private MenuItem mmDecrypt;

    @FXML
    private MenuItem mmGenKey;

    @FXML
    private MenuItem mmCopyKey;

    @FXML
    private MenuItem mmHelp;

    @FXML
    private MenuItem mmHotKeys;

    @FXML
    private MenuItem mmAboutProgram;

    @FXML
    private MenuItem mmAboutAuthor;

    @FXML
    private TextField keyField;

    @FXML
    private TextField fileField;

    @FXML
    private PasswordField keyPass;

    @FXML
    private Label myTextHint;

    @FXML
    private Pane hintPane;

    @FXML
    private ImageView closeLockImg;

    @FXML
    private ImageView openLockImg;

    @FXML
    void initialize() {
        EventHandler<ActionEvent> exitEvent = event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit");
            alert.setHeaderText(null);
            alert.setContentText("Do you want to exit?");

            ImageView image = new ImageView("sample\\assets\\images\\warning.png");
            image.setFitWidth(32);
            image.setFitHeight(32);
            alert.setGraphic(image);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                buffFile = null;
                Platform.exit();
            }
        };

        EventHandler<ActionEvent> genEvent = event -> {
            keyField.setText(generateKey(((new Random()).nextInt() % 5) + 35));
            keyPass.setText(keyField.getText());
            showHintWith("Key generated!");
        };

        EventHandler<ActionEvent> helpEvent = event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Help");
            alert.setHeaderText("How to use");
            alert.setContentText("   Click on folder image, \"Browse\" button or \"Browse\" button in main menu to " +
                    "open file. Click on key image to copy key. Click on \"Generate\" to generate a key.\n   Open file," +
                    "enter key and click on \"encrypt\\decrypt\" to encrypt\\decrypt file.");
            alert.showAndWait();
        };

        EventHandler<MouseEvent> copyKeyEvent = mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                StringSelection stringSelection = new StringSelection(keyField.getText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                showHintWith("Key was copied!");
            }
        };

        EventHandler<ActionEvent> mmCopyKeyEvent = event -> {
            StringSelection stringSelection = new StringSelection(keyField.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            showHintWith("Key was copied!");
        };

        EventHandler<ActionEvent> openFileEvent = event -> executeOpenFileDialog();

        EventHandler<ActionEvent> encryptEvent = event -> {
            try {
                File file = new File(fileField.getText());
                if (file.exists()) {
                    Variant4.encrypt(new File(fileField.getText()), keyField.getText(), encryptionMethod);
                } else {
                    TextInputDialog dialog = new TextInputDialog(Variant4
                            .encryptText(fileField.getText(), keyField.getText(), encryptionMethod));
                    dialog.setTitle("Answer");
                    dialog.setHeaderText("Cypher text:");
                    dialog.getDialogPane().setPrefWidth(400);
                    dialog.getDialogPane().getButtonTypes().remove(1);

                    dialog.show();
                }
                showHintWith("Encrypted!");
            } catch (Exception e) {
                alertWith(e.getLocalizedMessage(), "Error", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        };

        EventHandler<ActionEvent> decryptEvent = event -> {
            try {
                File file = new File(fileField.getText());
                if (file.exists()) {
                    Variant4.decrypt(new File(fileField.getText()), keyField.getText(), encryptionMethod);
                } else {
                    TextInputDialog dialog = new TextInputDialog(Variant4
                            .decryptText(fileField.getText(), keyField.getText(), encryptionMethod));
                    dialog.setTitle("Answer");
                    dialog.setHeaderText("Plain text:");
                    dialog.getDialogPane().setPrefWidth(400);
                    dialog.getDialogPane().getButtonTypes().remove(1);

                    dialog.show();
                }
                showHintWith("Decrypted!");
            } catch (Exception e) {
                alertWith(e.getLocalizedMessage(), "Error", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        };

        EventHandler<ActionEvent> chooseEvent = event -> methodTypeChoosing();

        eyeBtn.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                if (keyPass.isVisible()) {
                    eyeBtn.setImage(new Image("sample\\assets\\images\\eye_open.png"));
                    keyField.positionCaret(keyPass.getCaretPosition());
                    keyPass.setVisible(false);
                } else {
                    eyeBtn.setImage(new Image("sample\\assets\\images\\eye_close.png"));
                    keyPass.positionCaret(keyField.getCaretPosition());
                    keyPass.setVisible(true);
                }
            }
        });

        folderBtn.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                executeOpenFileDialog();
            }
        });

        keyField.textProperty().addListener((observable, oldValue, newValue) -> {
            keyPass.setText(keyPass.getText());
            keyPass.positionCaret(keyField.getText().length());
            if (keyField.getText().length() > 0 && fileField.getText().length() > 0) {
                enableBtns();
            } else {
                disableBtns();
            }
        });

        keyPass.textProperty().addListener((observable, oldValue, newValue) -> {
            keyField.setText(keyPass.getText());
            keyField.positionCaret(keyField.getText().length());
            if (keyField.getText().length() > 0 && fileField.getText().length() > 0) {
                enableBtns();
            } else {
                disableBtns();
            }
        });

        fileField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (keyField.getText().length() > 0 && fileField.getText().length() > 0) {
                enableBtns();
            } else {
                disableBtns();
            }
        });

        fileField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (keyField.getText().length() > 0 && fileField.getText().length() > 0) {
                enableBtns();
            } else {
                disableBtns();
            }
        });

        mmHotKeys.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Hot keys");
            alert.setHeaderText(null);
            alert.setContentText("Alt+O - open file;\nAlt+S - save file;\nAlt+D - decrypt file;\n" +
                    "Alt+E - encrypt file;\nAlt+C - copy key;\nAlt+G - generate key;\nAlt+H - help;\n"
                    + "Alt+K - hot keys;\nAlt+A - about author;\nAlt+P - about program;\nEsc - exit.");
            alert.showAndWait();
        });

        mmAboutProgram.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("About program");
            alert.setHeaderText(null);
            alert.setContentText("This application can encrypt and decrypt your text or media file.");
            alert.showAndWait();
        });

        mmAboutAuthor.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("About author");
            alert.setHeaderText(null);
            alert.setContentText("Application created by Matvey Vorivoda (Student of group 951007).");
            alert.showAndWait();
        });

        keyBtn.setOnMouseClicked(copyKeyEvent);
        mmCopyKey.setOnAction(mmCopyKeyEvent);

        exitBtn.setOnAction(exitEvent);
        mmExit.setOnAction(exitEvent);

        helpBtn.setOnAction(helpEvent);
        mmHelp.setOnAction(helpEvent);

        browseBtn.setOnAction(openFileEvent);
        mmOpen.setOnAction(openFileEvent);

        encryptBtn.setOnAction(encryptEvent);
        mmEncrypt.setOnAction(encryptEvent);

        decryptBtn.setOnAction(decryptEvent);
        mmDecrypt.setOnAction(decryptEvent);

        genBtn.setOnAction(genEvent);
        mmGenKey.setOnAction(genEvent);

        chooseBtn.setOnAction(chooseEvent);
        mmChoose.setOnAction(chooseEvent);

        mmChoose.setAccelerator(KeyCombination.keyCombination("Alt+M"));
        mmExit.setAccelerator(KeyCombination.keyCombination("Esc"));
        mmOpen.setAccelerator(KeyCombination.keyCombination("Alt+O"));
        mmCopyKey.setAccelerator(KeyCombination.keyCombination("Alt+C"));
        mmGenKey.setAccelerator(KeyCombination.keyCombination("Alt+G"));
        mmDecrypt.setAccelerator(KeyCombination.keyCombination("Alt+D"));
        mmEncrypt.setAccelerator(KeyCombination.keyCombination("Alt+E"));
        mmHelp.setAccelerator(KeyCombination.keyCombination("Alt+H"));
        mmHotKeys.setAccelerator(KeyCombination.keyCombination("Alt+K"));
        mmAboutAuthor.setAccelerator(KeyCombination.keyCombination("Alt+A"));
        mmAboutProgram.setAccelerator(KeyCombination.keyCombination("Alt+P"));
    }

    private String generateKey(int keyLength) {
        StringBuilder key = new StringBuilder();
        Random randomizer = new Random();
        for (int i = 0; i < keyLength; i++) {
            switch (language) {
                case 1:
                    key.append(nextChar(Math.abs(randomizer.nextInt()), 'A', 'Z'));
                    break;
                case 2:
                    key.append(nextChar(Math.abs(randomizer.nextInt()), 'А', 'Я'));
                    break;
            }
        }
        return key.toString();
    }

    private char nextChar(int chr, int firstLetterInAlphabet, int lastLetterInAlphabet) {
        return (char) (chr % (lastLetterInAlphabet - firstLetterInAlphabet) + firstLetterInAlphabet);
    }

    public static void alertWith(String content, String title, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void methodTypeChoosing() {
        ArrayList<String> lst = new ArrayList<>();
        lst.add("Vigenere");
        lst.add("Playfair");
        lst.add("Rail fence");
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Vigenere", lst);

        dialog.setTitle("Choice dialog");
        dialog.setHeaderText("Encryption method for text files");
        dialog.setContentText("Choose encryption method:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            switch (result.get()) {
                case "Vigenere":
                    encryptionMethod = 1;
                    language = 2;
                    keyField.setDisable(false);
                    keyPass.setVisible(true);
                    break;
                case "Playfair":
                    encryptionMethod = 2;
                    language = 1;
                    keyField.setText("Unnecessary");
                    keyField.setDisable(true);
                    keyPass.setVisible(false);
                    break;
                case "Rail fence":
                    encryptionMethod = 3;
                    language = 1;
                    keyField.setDisable(false);
                    keyPass.setVisible(true);
                    break;
            }
        }
    }

    void enableBtns() {
        encryptBtn.setDisable(false);
        decryptBtn.setDisable(false);
        mmEncrypt.setDisable(false);
        mmDecrypt.setDisable(false);
        openLockImg.setOpacity(1);
        closeLockImg.setOpacity(1);
    }

    void disableBtns() {
        encryptBtn.setDisable(true);
        decryptBtn.setDisable(true);
        mmEncrypt.setDisable(true);
        mmDecrypt.setDisable(true);
        openLockImg.setOpacity(0.2);
        closeLockImg.setOpacity(0.2);
    }

    void showHintWith(String content) {
        myTextHint.setText(content);
        hintPane.setOpacity(1);
        (new Timer()).schedule(new TimerTask() {
            @Override
            public void run() {
                AnimationTimer fading = new MyTimer();
                fading.start();
            }
        }, 1000);
    }

    void executeOpenFileDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files", "*.txt", "*.doc", "*.docx", "*.html", "*.pdf", "*.rtf", "*.jpg", "*.jpeg", "*.bmp", "*.png", "*.mp3", "*.mp4", "*.avi", "*.mov", "*.flv", "*.vma"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Encrypted files (vma)", "*.vma"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files (txt, doc, docx, html, pdf, rtf)", "*.txt", "*.doc", "*.docx", "*.html", "*.pdf", "*.rtf"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images (jpg, jpeg, bmp, png)", "*.jpg", "*.jpeg", "*.bmp", "*.png"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio files (mp3)", "*.mp3"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Video files (mp4, avi, mov, flv)", "*.mp4", "*.avi", "*.mov", "*.flv"));
        opFile = fileChooser.showOpenDialog(Main.root.getScene().getWindow());
        if (opFile != null) {
            fileField.setText(opFile.getAbsolutePath());
        }
    }
}