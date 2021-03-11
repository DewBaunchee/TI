package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Main extends Application {

    public static Parent root;

    @Override
    public void start(Stage primaryStage) throws Exception{
        root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Stream cypher");
        primaryStage.setScene(new Scene(root, 550, 500));
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(550);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
