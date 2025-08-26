package edu.icet.cims;

import edu.icet.cims.utill.Database.DbCheckUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Starter extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // checks if Database available
        // if available launch login window
        // otherwise attempt to create database

        if(DbCheckUtil.isDbAvailable()){
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/CIMS-Login.fxml"))));
        }
        else {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/CreateDB.fxml"))));
        }
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
