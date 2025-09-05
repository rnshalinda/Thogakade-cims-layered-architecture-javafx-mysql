package edu.icet.cims.controller;

import edu.icet.cims.db.dbConfig;
import edu.icet.cims.model.dto.*;
import edu.icet.cims.service.*;

import edu.icet.cims.util.AlertPopupUtil;
import edu.icet.cims.util.SessionUserUtil;
import edu.icet.cims.util.WindowManagerUtil;
import edu.icet.cims.util.configDb.DbCheckUtil;
import edu.icet.cims.util.configDb.LoadDbConfigUtil;
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
    private Button btn_configDbAccess;

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

        // checks if Database available
        // if available launch login window
        // otherwise attempt to create database

        // txt fields check - not empty
        if(initialFieldValidation()){
            // load db config from ymal file
            if(LoadDbConfigUtil.loadDbConfig()){
                // check if such db exist
                if(DbCheckUtil.isDbAvailable( dbConfig.getDbConfigData().getDbName() )){
                    proceedToLogin(event);  // login
                }
                else {
                    AlertPopupUtil.alertMsg(Alert.AlertType.WARNING, "Database '"+ dbConfig.getDbConfigData().getDbName()+"', not available, Please configure database access details.");
                }
            }
            else AlertPopupUtil.alertMsg(Alert.AlertType.ERROR, "Unable to load db configuration (dbConfig.yml)");
        }
        else AlertPopupUtil.alertMsg(Alert.AlertType.WARNING, "Fields cannot be empty");

    }

    @FXML
    void btn_ConfigDbAccessAction(ActionEvent event) throws IOException {
        WindowManagerUtil.switchScene(event, "/view/configDB/ConfigDbAccess.fxml");
    }

    // txt field validation - ui level
    private boolean initialFieldValidation(){
        return !txt_uname.getText().isEmpty() && !txt_pswd.getText().isEmpty();
    }

    // check login
    private void proceedToLogin(ActionEvent event) throws IOException {

        UserCredentialsDTO userDTO = new UserCredentialsDTO(txt_uname.getText(), txt_pswd.getText());
        ActiveUserDTO user = service.credentialsValidate(userDTO);                      // validate user with username, pswd and get user id, name

        if (user != null) {
            SessionUserUtil.setLoggedUser(user);                                        // display current user
            WindowManagerUtil.switchScene(event, "/view/Dashboard.fxml");      // open dashboard
        } else {
            AlertPopupUtil.alertMsg(Alert.AlertType.ERROR, "Incorrect Password or Username");   // moved to separate dialog box class
        }

    }
}