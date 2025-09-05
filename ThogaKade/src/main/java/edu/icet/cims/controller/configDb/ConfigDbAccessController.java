package edu.icet.cims.controller.configDb;

import edu.icet.cims.db.dbConfig;
import edu.icet.cims.model.dto.DbConfigDTO;
import edu.icet.cims.util.AlertPopupUtil;
import edu.icet.cims.util.WindowManagerUtil;
import edu.icet.cims.util.configDb.DbCheckUtil;
import edu.icet.cims.util.configDb.ManageDbUtil;
import edu.icet.cims.util.configDb.SaveDbConfigUtil;
import edu.icet.cims.util.validator.CommonValidatorUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class ConfigDbAccessController implements Initializable {

    private String host;
    public String dbName;
    private String port;
    private String user;
    private String pswd;
    private String extraParam;


    @FXML
    private Button btn_configure;

    @FXML
    private Button btn_createDb;

    @FXML
    private Button btn_checkDb;

    @FXML
    private Button btn_goBack;

    @FXML
    private CheckBox chkBox_defaultLocalhost;

    @FXML
    private CheckBox chkBox_optionalParam;

    @FXML
    private CheckBox chkBox_portDefault;

    @FXML
    private CheckBox chkBox_userDefault;

    @FXML
    private TextField txt_dbName;

    @FXML
    private TextField txt_host;

    @FXML
    private TextField txt_port;

    @FXML
    private TextField txt_pswd;

    @FXML
    private TextField txt_urlOptional;

    @FXML
    private TextField txt_user;



    @FXML
    void btn_checkDbAction(ActionEvent event) throws SQLException, IOException {

        // get txt field inputs
        getFieldData();

        // if all fields not null and not empty proceed to dbConfigDTO otherwise show empty field alert
        // note extraParam not checked here, because its optional and can be empty
        if(initialFieldValidation(dbName, port, user, pswd, host)){

            // set DbConfigDTO to dbConfig
            dbConfig.setDbConfigData(new DbConfigDTO(host, dbName, user, pswd, port, extraParam));

            // check if db empty
            if(DbCheckUtil.isDbAvailable( dbConfig.getDbConfigData().getDbName() )){

                // check if db data exist, return tables names in db  [table 1, table 2, table 3]
                ArrayList<String> existingDbTableNames = DbCheckUtil.checkDbTblExist(dbConfig.getDbConfigData().getDbName());

                if(existingDbTableNames != null && dbStructureMatches(existingDbTableNames)){
                    AlertPopupUtil.alertMsg(Alert.AlertType.ERROR, "Successful!\nExisting database matches required structure.\n\n"+dbConfig.getDbConfigData().getDbName()+"\n\t|-> customer : "+ManageDbUtil.getRequiredColumnNames("cutomer")+"\n\t|-> item : "+ManageDbUtil.getRequiredColumnNames("item")+"\n\t|-> user_credentials : "+ManageDbUtil.getRequiredColumnNames("user_credentials")+"\n\nPlease press configure button.");
                    btn_configure.setDisable(false);        // enable configure button
                }
                else {
                    btn_createDb.setDisable(false);         // enable create db button
                }
            }
            else {
                AlertPopupUtil.alertMsg(Alert.AlertType.ERROR, "Database not available, \nRecheck access details or create database.");
                btn_createDb.setDisable(false);             // enable create db button
                btn_configure.setDisable(true);             // disable configure button
            }
        }
        else AlertPopupUtil.alertMsg(Alert.AlertType.WARNING, "Fields cannot be empty");       // else show error msg
    }


    // Configure Db button action
    @FXML
    void btn_configureAction(ActionEvent event) throws IOException {

        // get current stored DbConfigDTO stored from dbConfig.getDbConfigData() and save as yml file to local file root
        if(SaveDbConfigUtil.saveToYlm(dbConfig.getDbConfigData())){

            AlertPopupUtil.alertMsg(Alert.AlertType.CONFIRMATION, "Configuration saved to dbConfig.yml");   // if success display msg confirmation

            WindowManagerUtil.switchScene(event, "/view/CIMS-Login.fxml");                              // redirect to log in window
        }
        else AlertPopupUtil.alertMsg(Alert.AlertType.ERROR, "Cannot save data to dbConfig.yml");           // could not save display msg error
    }


    // Create Database
    @FXML
    void btn_createDbAction(ActionEvent event) throws IOException {
        WindowManagerUtil.switchScene(event, "/view/configDb/CreateDb.fxml");   // open create database window
    }


    // back button action
    @FXML
    void btn_goBackAction(ActionEvent event) throws IOException {
        WindowManagerUtil.switchScene(event, "/view/CIMS-Login.fxml");          // Go back to login
    }

    // port & user check-box button action
    @FXML
    void checkBoxAction(ActionEvent event) {
        // check if default port is ticked
        if(chkBox_portDefault.isSelected()){
            txt_port.setDisable(true);
        } else{
            txt_port.setDisable(false);
        }

        // check if default root selected
        if(chkBox_userDefault.isSelected()){
            txt_user.setDisable(true);
        } else {
            txt_user.setDisable(false);
        }

        // check if default host selected
        if(chkBox_defaultLocalhost.isSelected()){
            txt_host.setDisable(true);
        } else {
            txt_host.setDisable(false);
        }
    }


    @FXML
    void chkBox_optionalUrlAction(ActionEvent event) {

        if(chkBox_optionalParam.isSelected())   txt_urlOptional.setDisable(false);
        else   txt_urlOptional.setDisable(true);
    }


    private void getFieldData(){

        this.host = txt_host.getText();
        this.user = txt_user.getText();
        this.port = txt_port.getText();
        this.dbName = txt_dbName.getText();
        this.pswd = txt_pswd.getText();
        this.extraParam ="";

        if(chkBox_defaultLocalhost.isSelected()) this.host = "localhost";    // default host chekBox selected
        if(chkBox_userDefault.isSelected())      this.user = "root";         // default user chekBox selected
        if(chkBox_portDefault.isSelected())      this.port = "3306";         // default port chekBox selected

        // if optional parameters selected
        if(chkBox_optionalParam.isSelected()){
            this.extraParam = txt_urlOptional.getText();    // get extra param if has
        }
    }

    // check if txt fields are empty
    private boolean initialFieldValidation(String dbName, String port, String user, String pswd, String host){
        if (dbName == null || port == null || user == null || pswd == null || host == null) return false;                  // if fields null
        if (dbName.isBlank() || port.isBlank() || user.isBlank() || pswd.isBlank() || host.isBlank()) return  false;     // if fields empty
        if (!CommonValidatorUtil.isIntFormat(port)) return false;                                                                         // if port not numbers
        return true;
    }

//     disable all fields
//    private void disableAllFields(boolean state){
//        txt_host.setDisable(state);
//        txt_dbName.setDisable(state);
//        txt_port.setDisable(state);
//        txt_user.setDisable(state);
//        txt_pswd.setDisable(state);
//        txt_urlOptional.setDisable(state);
//
//        chkBox_optionalParam.setDisable(state);
//        chkBox_userDefault.setDisable(state);
//        chkBox_portDefault.setDisable(state);
//    }


    private boolean dbStructureMatches(ArrayList<String> existingDbTableNames){

        // check db tables name matches
        if(DbCheckUtil.matchTblName(existingDbTableNames, ManageDbUtil.getRequiredTableNames())){
            // check each table columns
            if(DbCheckUtil.matchTblColumn(existingDbTableNames, ManageDbUtil.getRequiredTableNames())){
                return true;
            }
            else  AlertPopupUtil.alertMsg(Alert.AlertType.WARNING, "Found,'"+ dbConfig.getDbConfigData().getDbName()+"' database"+"\n\nTable columns does not match"+"\nExpected,\n\nCustomer table: \n->"+ManageDbUtil.getRequiredColumnNames("customer")+"\n\nitem table: \n->"+ManageDbUtil.getRequiredColumnNames("item")+"\n\nuser_credentials table: \n->"+ManageDbUtil.getRequiredColumnNames("user_credentials")+"\n\nPlease press create-db button.");
        }
        else AlertPopupUtil.alertMsg(Alert.AlertType.WARNING, "Found,'"+ dbConfig.getDbConfigData().getDbName()+"' database,\nContain -> "+existingDbTableNames.toString()+"\nRequired -> "+ManageDbUtil.getRequiredTableNames().toString()+"\n\nFound database does not contain required tables. \n\nPlease press create-db button");

        return false;
    }

    // initially apply these settings
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btn_configure.setDisable(true);     // disable configure button
        btn_createDb.setDisable(true);     // disable create db button

        txt_urlOptional.setDisable(true);
    }
}
