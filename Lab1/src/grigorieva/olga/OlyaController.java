package grigorieva.olga;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

import grigorieva.olga.Main;
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
import grigorieva.olga.Variant4;

import javax.swing.text.BadLocationException;

public class OlyaController {

    public static File opFile, buffFile;
    public static int encryptionMethod = 1;

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
    private MenuItem mmOpen;

    @FXML
    private MenuItem mmSave;

    @FXML
    private MenuItem mmExit;

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
    private CheckBox isPlaintext;

    @FXML
    void initialize() {
        isPlaintext.setOnAction(event -> {
            if (isPlaintext.isSelected()) {
                fileField.setPromptText("Enter plaintext");
            } else {
                fileField.setPromptText("Where is the file?");
            }
        });

        EventHandler<ActionEvent> exitEvent = event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit");
            alert.setHeaderText(null);
            alert.setContentText("Do you want to exit?");

            ImageView image = new ImageView("grigorieva/olga/assets/images/warning.png");
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

        EventHandler<ActionEvent> openFileEvent = event -> {
            executeOpenFileDialog();
        };

        EventHandler<ActionEvent> encryptEvent = event -> {
            try {
                if (fileField.getText().length() == 0) {
                    alertWith("Please, select a file first.", "Error", Alert.AlertType.ERROR);
                } else {
                    if (encryptionMethod == 1 || keyField.getText().length() > 0 && encryptionMethod != 1) {
                        if (isPlaintext.isSelected()) {
                            String answer = Variant4.encryptText(fileField.getText(), keyField.getText(), encryptionMethod);
                            if (answer == null) {
                                alertWith("Error during the encryption", "Error", Alert.AlertType.ERROR);
                            } else {
                                FileWriter fw = new FileWriter("cyphertext.txt");
                                alertWith(answer, "Answer", Alert.AlertType.INFORMATION);
                                fw.write(answer);
                                fw.close();
                            }
                        } else {
                            if (new File(fileField.getText()).exists()) {
                                Variant4.encrypt(new File(fileField.getText()), keyField.getText(), encryptionMethod);
                            } else {
                                alertWith("File not found", "Error", Alert.AlertType.ERROR);
                            }
                        }
                    } else {
                        alertWith("Enter key", "Error", Alert.AlertType.ERROR);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        EventHandler<ActionEvent> decryptEvent = event -> {
            try {
                if (fileField.getText().length() == 0) {
                    alertWith("Please, select a file first.", "Error", Alert.AlertType.ERROR);
                } else {
                    if (encryptionMethod == 1 || keyField.getText().length() > 0 && encryptionMethod != 1) {
                        if (isPlaintext.isSelected()) {
                            String answer = Variant4.decryptText(fileField.getText(), keyField.getText(), encryptionMethod);
                            if (answer == null) {
                                alertWith("Error during the encryption", "Error", Alert.AlertType.ERROR);
                            } else {
                                FileWriter fw = new FileWriter("plaintext.txt");
                                alertWith(answer, "Answer", Alert.AlertType.INFORMATION);
                                fw.write(answer);
                                fw.close();
                            }
                        } else {
                            if (new File(fileField.getText()).exists()) {
                                Variant4.decrypt(new File(fileField.getText()), keyField.getText(), encryptionMethod);
                            } else {
                                alertWith("File not found", "Error", Alert.AlertType.ERROR);
                            }
                        }
                    } else {
                        alertWith("Enter key", "Error", Alert.AlertType.ERROR);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        EventHandler<ActionEvent> chooseEvent = event -> {
            methodTypeChoosing();
        };

        mmSave.setOnAction(event -> {
            try {
                executeSaveFileDialog();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            alert.setContentText("Application created by Olga Grigorieva (Student of group 951007).");
            alert.showAndWait();
        });

        mmExit.setOnAction(exitEvent);

        mmHelp.setOnAction(helpEvent);

        browseBtn.setOnAction(openFileEvent);
        mmOpen.setOnAction(openFileEvent);

        encryptBtn.setOnAction(encryptEvent);

        decryptBtn.setOnAction(decryptEvent);

        genBtn.setOnAction(genEvent);

        chooseBtn.setOnAction(chooseEvent);
    }

    private String generateKey(int keyLength) {
        String key = "";
        Random randomizer = new Random();
        for (int i = 0; i < keyLength; i++) {
            key = key + nextChar((byte) Math.abs(randomizer.nextInt() % 128));
        }
        return key;
    }

    private char nextChar(byte chr) {
        if (chr > 57) {
            if (chr > 90) {
                chr = (byte) (97 + chr % 26);
            } else {
                chr = (byte) (65 + chr % 26);
            }
        } else {
            chr = (byte) (48 + chr % 10);
        }
        return (char) (chr);
    }

    public static void alertWith(String content, String title, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void methodTypeChoosing() {
        ArrayList<String> lst = new ArrayList<>();
        lst.add("Playfair");
        lst.add("Vigenere");
        lst.add("Railroad fence");
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Playfair", lst);

        dialog.setTitle("Choice dialog");
        dialog.setHeaderText("Encryption method for text files");
        dialog.setContentText("Choose encryption method:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            switch (result.get()) {
                case "Playfair":
                    encryptionMethod = 1;
                    break;
                case "Vigenere":
                    encryptionMethod = 2;
                    break;
                case "Railroad fence":
                    encryptionMethod = 3;
                    break;
            }
        }
    }

    void executeSaveFileDialog() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save file");
        //   fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Buffered file", "*." + Cypher.getExtension(buffFile)));
        opFile = fileChooser.showSaveDialog(Main.root.getScene().getWindow());
        Files.copy(buffFile.toPath(), opFile.toPath());
    }

    void executeOpenFileDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files (txt)", "*.txt"));
        opFile = fileChooser.showOpenDialog(Main.root.getScene().getWindow());
        if (opFile != null) {
            fileField.setText(opFile.getAbsolutePath());
        }
    }
}