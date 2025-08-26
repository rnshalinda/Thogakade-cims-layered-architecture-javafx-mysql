package edu.icet.cims.controller;

import edu.icet.cims.model.dto.*;
import edu.icet.cims.service.*;

import edu.icet.cims.utill.AlertPopupUtil;
import edu.icet.cims.utill.SessionUserUtil;
import edu.icet.cims.utill.WindowManagerUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;


public class LoginController{

    private ServiceCredentials service = new ServiceCredentials();

    @FXML
    private Button btn_cancel;

    @FXML
    private Button btn_login;

    @FXML
    private TextField txt_pswd;

    @FXML
    private TextField txt_uname;

    @FXML
    void btn_CancelAction(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void btn_LoginAction(ActionEvent event) throws IOException {

        if(!txt_uname.getText().isEmpty() && !txt_pswd.getText().isEmpty()) {

            UserCredentialsDTO userDTO = new UserCredentialsDTO(txt_uname.getText(), txt_pswd.getText());
            ActiveUserDTO user = service.credentialsValidate(userDTO); // validate user with username, pswd and get user id, name

            if (user != null) {
                SessionUserUtil.setLoggedUser(user);    // display current user
                WindowManagerUtil.switchScene(event, "/view/Dashboard.fxml");   // open dashboard
            } else {
                AlertPopupUtil.alertMsg(Alert.AlertType.ERROR, "Incorrect Password or Username");   // moved to separate dialog box class
                txt_uname.clear();
                txt_pswd.clear();
            }
        }
        else{
            AlertPopupUtil.alertMsg(Alert.AlertType.ERROR, "Username & Password cannot be empty");
        }
    }

}