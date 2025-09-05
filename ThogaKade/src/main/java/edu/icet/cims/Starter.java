package edu.icet.cims;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Starter extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/CIMS-Login.fxml"))));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
