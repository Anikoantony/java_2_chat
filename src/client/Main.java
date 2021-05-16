package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.OutputStream;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("СпэйсЧат");
        primaryStage.setScene(new Scene(root, 450, 350));
        primaryStage.show();
        OutputStream os=new OutputStream() {
            @Override
            public void write(int b) throws IOException {

            }
        };
    }


    public static void main(String[] args) {
        launch(args);
    }
}
