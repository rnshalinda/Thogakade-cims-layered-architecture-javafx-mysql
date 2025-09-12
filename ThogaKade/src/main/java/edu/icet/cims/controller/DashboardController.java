package edu.icet.cims.controller;

import edu.icet.cims.util.SessionUserUtil;
import edu.icet.cims.util.WindowManagerUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Button btn_exit;

    @FXML
    private Button btn_logout;

    @FXML
    private Button btn_mngeCust;

    @FXML
    private Button btn_mngeItems;

    @FXML
    private Label lbl_activeUsername;

    @FXML
    private Label lbl_ativeUserID;

    @FXML
    void btn_ExitAction(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void btn_LogoutAction(ActionEvent event) throws IOException {
        SessionUserUtil.clearSessionUser();
        WindowManagerUtil.switchScene(event, "/view/CIMS-Login.fxml");
    }


    @FXML
    void btn_MngeCustAction(ActionEvent event) throws IOException {
        WindowManagerUtil.switchScene(event, "/view/CustomerManagementForm.fxml");
    }

    @FXML
    void btn_MngeItemsAction(ActionEvent event) throws IOException {
        WindowManagerUtil.switchScene(event, "/view/ItemManagementForm.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lbl_ativeUserID.setText(SessionUserUtil.getLoggedUser().getUserId());
        lbl_activeUsername.setText(SessionUserUtil.getLoggedUser().getName());
    }
}

