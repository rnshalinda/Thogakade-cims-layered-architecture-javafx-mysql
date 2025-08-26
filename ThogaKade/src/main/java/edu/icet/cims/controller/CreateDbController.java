package edu.icet.cims.controller;

import edu.icet.cims.utill.Database.CreateDatabaseUtil;
import edu.icet.cims.utill.WindowManagerUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class CreateDbController {

    @FXML
    private Button btn_logout1;

    @FXML
    private Button btn_yes;

    @FXML
    void btn_NoAction(ActionEvent event) throws IOException {
        WindowManagerUtil.switchScene(event, "/view/CIMS-Login.fxml");
    }

    @FXML
    void btn_YesAction(ActionEvent event) {
        CreateDatabaseUtil.createDB();
    }

}
