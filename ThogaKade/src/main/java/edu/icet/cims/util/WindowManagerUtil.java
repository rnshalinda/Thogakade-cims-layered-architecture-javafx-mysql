package edu.icet.cims.util;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class WindowManagerUtil {

    private static Stage stage;

    public static void switchScene(ActionEvent winEvent, String fxmlPath) throws IOException {

        stage = (Stage) ((Node) winEvent.getSource()).getScene().getWindow();                           // gets current window from the action-event that executed 'switchScene()' method
        stage.setScene(new Scene(FXMLLoader.load(WindowManagerUtil.class.getResource(fxmlPath))));      // assign new scene 'fxmlPath' to the old stage reference
        stage.show();

        // since here we're dealing with one window at a time
        // I used the 'winEven' to assign the current window as a reference point to the new window
        // every new window is opened the previous window's stage
        // no new stage objects been created, thus saving memory
    }
}
