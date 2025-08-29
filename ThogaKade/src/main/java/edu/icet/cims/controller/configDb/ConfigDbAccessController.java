package edu.icet.cims.controller.configDb;

import edu.icet.cims.model.dto.dbConfigDTO;
import edu.icet.cims.util.AlertPopupUtil;
import edu.icet.cims.util.WindowManagerUtil;
import edu.icet.cims.util.configDb.SaveDbConfigYml;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ConfigDbAccessController implements Initializable {

    private String url;
    private String port;
    private String dbName;
    private String user;
    private String pswd;

    @FXML
    private Button btn_configure;

    @FXML
    private Button btn_goBack;

    @FXML
    private RadioButton btn_radioLocalDb;

    @FXML
    private RadioButton btn_radioRemoteDb;

    @FXML
    private ToggleGroup btn_radioToggleGroup;

    @FXML
    private CheckBox chkBox_userLocal;

    @FXML
    private CheckBox chkBox_portLocal;

    @FXML
    private TextField txt_portLocal;

    @FXML
    private TextField txt_pswdLocal;

    @FXML
    private TextField txt_dbNameLocal;

    @FXML
    private TextField txt_dbNameRemote;

    @FXML
    private TextField txt_urlRemote;

    @FXML
    private TextField txt_userLocal;

    @FXML
    private TextField txt_pswdRemote;

    @FXML
    private TextField txt_userRemote;

    // Configure Db button action
    @FXML
    void btn_configureAction(ActionEvent event) throws IOException {
        // if local db selected
        if(btn_radioLocalDb.isSelected()){
            if(!chkBox_userLocal.isSelected())  this.port = txt_userLocal.getText();        // if user chekBox not selected
            if(!chkBox_portLocal.isSelected())  this.port = txt_portLocal.getText();        // if port chekBox not selected

            this.dbName = txt_dbNameLocal.getText();
            this.url =  "jdbc:mysql://localhost:"+port+"/"+dbName;
            this.pswd = txt_pswdLocal.getText();
        }

        // if remote db selected
        if(btn_radioRemoteDb.isSelected()){
            this.port = "0000";
            this.dbName = txt_dbNameRemote.getText();
            this.url = txt_urlRemote.getText();
            this.user = txt_userRemote.getText();
            this.pswd = txt_pswdRemote.getText();
        }

        // if all fields not null and not empty proceed to dbDTO
        if(initialFieldValidation(dbName, url, port, user, pswd)){

            dbConfigDTO dbConfig = new dbConfigDTO(dbName, url, user, pswd);

            // save db configuration as yml file
            if(SaveDbConfigYml.saveToYlm(dbConfig)){
                AlertPopupUtil.alertMsg(Alert.AlertType.CONFIRMATION, "Configuration saved to dbConfig.yml");   // if success display msg confimation

                WindowManagerUtil.switchScene(event, "/view/CIMS-Login.fxml");          // rederact to login window
            }
            else AlertPopupUtil.alertMsg(Alert.AlertType.ERROR, "Cannot save data to dbConfig.yml");            // if if could not save display msg error
        }
        else AlertPopupUtil.alertMsg(Alert.AlertType.WARNING, "Fields cannot be empty");    // else show error msg
    }

    // port & user check-box button action
    @FXML
    void chkBoxAction(ActionEvent event) {
        // check if default port is ticked
        if(chkBox_portLocal.isSelected()){
            txt_portLocal.setDisable(true);
            this.port = "3306";
        } else{
            txt_portLocal.setDisable(false);
        }

        // check if default root selected
        if(chkBox_userLocal.isSelected()){
            txt_userLocal.setDisable(true);
            this.user = "root";
        } else {
            txt_userLocal.setDisable(false);
        }
    }


    // back button action
    @FXML
    void btn_goBackAction(ActionEvent event) throws IOException {
        WindowManagerUtil.switchScene(event, "/view/CIMS-Login.fxml");      // Go back to login
    }

    // local or remote db select, radio button action
    @FXML
    void btn_radaioToggleAction(ActionEvent event) {
        // activate local section only
        if(btn_radioLocalDb.isSelected()){
            enableSection(false, true);     // enable all local fields and disable all remote fields
        }
        // activate remote section only
        if(btn_radioRemoteDb.isSelected()){
            enableSection(true, false);     // disable all local fields and enable all remote fields
        }
    }

    // enable or disable fields toggle action
    private void enableSection(boolean local, boolean remote){
        // Remote db toggle
        txt_dbNameRemote.setDisable(remote);
        txt_urlRemote.setDisable(remote);
        txt_userRemote.setDisable(remote);
        txt_pswdRemote.setDisable(remote);

        // local db toggle
        txt_dbNameLocal.setDisable(local);
        txt_userLocal.setDisable(local);         // exception check chkBoxAction
        txt_portLocal.setDisable(local);         // exception check chkBoxAction
        txt_pswdLocal.setDisable(local);
        chkBox_userLocal.setDisable(local);
        chkBox_portLocal.setDisable(local);
    }

    // check if txt fields are empty
    private boolean initialFieldValidation(String dbName, String url, String port, String user, String pswd){
        if (dbName == null || url == null || port == null || user == null || pswd == null) return false;
        if (dbName.isEmpty() || url.isEmpty() || port.isEmpty() || user.isEmpty() || pswd.isEmpty()) return  false;
        return true;
    }

    // initially apply these settings
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        enableSection(true, true);
    }
}
