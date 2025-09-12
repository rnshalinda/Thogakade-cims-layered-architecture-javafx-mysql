package edu.icet.cims.controller;

import edu.icet.cims.db.DbConfig;
import edu.icet.cims.model.dto.*;

import edu.icet.cims.service.ServiceCredential;
import edu.icet.cims.service.exception.UserCredentialsException;
import edu.icet.cims.service.impl.ServiceCredentialsImpl;
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


    private ServiceCredential serviceCredInterface = new ServiceCredentialsImpl();      // interface type = service object type


    @FXML
    void btn_CancelAction(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void btn_LoginAction(ActionEvent event) throws IOException {

        // checks if Database available,
        // if available launch login window
        // otherwise attempt to create database

        // txt fields check - not empty
        if(initialFieldValidation()){

            // load db config from ymal file
            if(LoadDbConfigUtil.loadDbConfig()){

                // check if such db exist
                if(DbCheckUtil.isDbAvailable( DbConfig.getDbConfigData().getDbName() )){
                    proceedToLogin(event);  // login
                }
                else {
                    AlertPopupUtil.alertMsg(Alert.AlertType.WARNING, "Database '"+ DbConfig.getDbConfigData().getDbName()+"', not available, Please configure database access details.");
                }
            }
            else AlertPopupUtil.alertMsg(Alert.AlertType.ERROR, "Unable to load db configuration (db-config.yml)");
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
    private void proceedToLogin(ActionEvent event){
        try {
            UserCredentialsDto userDTO = new UserCredentialsDto(txt_uname.getText(), txt_pswd.getText());

            serviceCredInterface.credentialsValidate(userDTO);              // validate user with username, pswd and get user id, name

            WindowManagerUtil.switchScene(event, "/view/Dashboard.fxml");           // open dashboard

        } catch (UserCredentialsException | IOException e) {
            AlertPopupUtil.alertMsg(Alert.AlertType.WARNING, e.getMessage());
        }
    }
}