package edu.icet.cims.controller.configDb;

import edu.icet.cims.controller.util.ConfirmationWindowController;
import edu.icet.cims.db.DbConnection;
import edu.icet.cims.db.dbConfig;
import edu.icet.cims.model.dto.DbConfigDTO;
import edu.icet.cims.util.AlertPopupUtil;
import edu.icet.cims.util.WindowManagerUtil;
import edu.icet.cims.util.configDb.ManageDbUtil;
import edu.icet.cims.util.configDb.DbCheckUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CreateDbController implements Initializable {

    @FXML
    private Button btn_add;

    @FXML
    private Button btn_changeDbName;

    @FXML
    private Button btn_createDb;

    @FXML
    private Button btn_goBack;

    @FXML
    private CheckBox chkBox_defaultDbName;

    @FXML
    private ComboBox<String> comboBox_selectTbl;

    @FXML
    private StackPane stackPane_contentArea;

    @FXML
    private TextArea txtArea_sqlQuery;

    @FXML
    private TextField txt_dbName;


    String newDbName = "";       // new dbName holder

    // db Structure List
    // contains -> { dbName, tbl1, tbl2, tbl3.... } ** must start with dbName
    ArrayList<String> dbStructure = new ArrayList<>();


    // add tables to db structure
    @FXML
    void btn_addAction(ActionEvent event) {
        // check if selection combo-box not null
        if(comboBox_selectTbl.getValue() != null){

            dbStructure.add(comboBox_selectTbl.getValue());                         // append selected combo-box value(table) to dbStructure list

            viewDbStructure(dbStructure);                                           // preview db structure in stack-pane area

            comboBox_selectTbl.getItems().remove(comboBox_selectTbl.getValue());    // remove item from selection combo-box once added
        }
    }


    // change dbName
    @FXML
    void btn_changeDbNameAction(ActionEvent event) {

        newDbName = txt_dbName.getText();       // new dbName
        editDbStructureList();                  // update db Structure List - replace first element dbName with new name
        viewDbStructure(dbStructure);
    }


    // create db
    @FXML
    void btn_createDbAction(ActionEvent event) throws IOException, SQLException {

        // check if user added at least one table to the db before finalizing
        if(!(dbStructure.toArray().length <= 1)) {

            // if default dbName check-box not selected, create new dbName and url DbConfigDTO
            if (!chkBox_defaultDbName.isSelected()) {

                // store new DbConfigDTO in dbConfig.setDbConfigData()
                dbConfig.setDbConfigData(new DbConfigDTO(dbConfig.getDbConfigData().getHost() ,newDbName, dbConfig.getDbConfigData().getUser(), dbConfig.getDbConfigData().getPswd(), dbConfig.getDbConfigData().getPort(), dbConfig.getDbConfigData().getExtraParam()));
            }

            // create database
            // check if db with user given name already exist
            if ( DbCheckUtil.isDbAvailable( dbConfig.getDbConfigData().getDbName()) ) {

                ArrayList dbList = DbCheckUtil.checkDbTblExist(dbConfig.getDbConfigData().getDbName());      //if db exist, return list containing existing tables
                dbList.addFirst(dbConfig.getDbConfigData().getDbName());                                     // push existing db name as first element

                // Ask user confirmation, Yes/No
                if (launchConfirmWindow( dbList )) {      // if 'yes' execute below

                    // delete existing db
                    if (dropDbMethod()) {

                        // and create new create db
                        createDbMethod();

                        // create tables
                        createTblMethod();
                    }
                }
                // if 'No' close the window without doing anything
            }
            // if it doesn't exist, go straight to create new db
            else {
                createDbMethod();
                DbConnection.useDb( dbConfig.getDbConfigData().getDbName());
                createTblMethod();
            }
        }
        // if user had not add tables show warning
        else AlertPopupUtil.alertMsg(Alert.AlertType.WARNING, "You have not selected any tables.");
    }


    // go back
    @FXML
    void btn_goBackAction(ActionEvent event) throws IOException {
        WindowManagerUtil.switchScene(event, "/view/configDb/ConfigDbAccess.fxml");
    }


    // default db name check-box
    @FXML
    void chkBox_defaultDbNameAction(ActionEvent event) {
        changeDbNameState();                                        // enable/disable dbName txt, change btn
        if(chkBox_defaultDbName.isSelected()) {
            newDbName = dbConfig.getDbConfigData().getDbName();     // reassign default db name from yml as newDbName
            editDbStructureList();                                  // update db Structure list - replace first element
        }
    }


    // populate preview with sql are when selecting combo value
    @FXML
    void comboBox_selectTblAction(ActionEvent event) {
        txtArea_sqlQuery.setWrapText(true);

        if(comboBox_selectTbl.getValue() != null) {
            txtArea_sqlQuery.setText(getSql(comboBox_selectTbl.getValue()));    // get table sql string and show in preview area
        }

    }


    // initially apply these settings
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // push the current dbName in DbConfigDTO to dbStructure list as first element
        dbStructure.add(dbConfig.getDbConfigData().getDbName());

        // add table selections to combo-box comboBox_selectTbl
        loadSelectCombobox();

        // set current db name as default name in check-box
        chkBox_defaultDbName.setText("default name '"+ dbConfig.getDbConfigData().getDbName());
        chkBox_defaultDbName.setSelected(true);     // select check-box

        // dbNAme txt, change btn initial active state
        changeDbNameState();
    }


    // check defaultDbName check box selected
    private void changeDbNameState(){
        if(chkBox_defaultDbName.isSelected()){
            txt_dbName.setDisable(true);
            btn_changeDbName.setDisable(true);
        }else {
            txt_dbName.setDisable(false);
            btn_changeDbName.setDisable(false);
        }
    }


    // set values to combo box
    private void loadSelectCombobox(){
        ObservableList<String> tables = FXCollections.observableArrayList();
//        tables.addAll("customer", "item", "user_credentials");
        tables.addAll(ManageDbUtil.getRequiredTableNames());    // get tables names as list from ManageDbUtil
        comboBox_selectTbl.setItems(tables);
    }

    // get select table combo box sql
    private String getSql(String str){
        if (str.equalsIgnoreCase("item")) return ManageDbUtil.getItemTbl();
        if (str.equalsIgnoreCase("customer")) return ManageDbUtil.getCustomerTbl();
        if (str.equalsIgnoreCase("user_credentials")) return ManageDbUtil.getUser_credentialsTbl();
        return null;
    }


    // preview area for db structure - dbName, tables
    private void viewDbStructure(ArrayList<String> dbList){
        // print db structure to stack-pane TextArea
        for(int i = 0; i < dbList.toArray().length; i++){

            if(i == 0) txtArea_sqlQuery.setText("db - "+(String) dbList.toArray()[i++]);          // setText - clear text area set  first [i==0] element of list

            // if dbList array has more than 1 element continue
            if(dbList.toArray().length > 1) {
                txtArea_sqlQuery.appendText("\n\t|-->" + (String) dbList.toArray()[i] + " (table)");  // append every-other list element as new line text
            }
        }
    }

    // pop the first element in the dbStructure and add first newDbName
    // this is for the preview
    private void editDbStructureList(){
        dbStructure.removeFirst();                // pop the previous db name from the dbStructure list
        dbStructure.addFirst(newDbName);          // add first changed dn name to dbStructure list
    }


    // launch boolean confirmation window
    private boolean launchConfirmWindow(ArrayList<String> dbList) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/util/ConfirmationWindow.fxml"));
        Parent root = loader.load();

        // Get the controller created by FXMLLoader
        ConfirmationWindowController boolWindow = loader.getController();

        boolWindow.setStackpane(dbList);                                  // pass list elements to set stack-pane
        boolWindow.setHeading("Database already exists, Replace?");     // window heading methode

        // Show the new scene
        Stage stage = new Stage();
        stage.setScene(new Scene(root));

        stage.initModality(Modality.APPLICATION_MODAL);     // block other windows
        stage.showAndWait();                                // wait for user to press Yes/No

        return boolWindow.getResult();                      // fetch result after bool confirm window closes
    }


    // create db inner method
    private void createDbMethod(){
        if(ManageDbUtil.createDB( dbConfig.getDbConfigData().getDbName()) ){
            AlertPopupUtil.alertMsg(Alert.AlertType.CONFIRMATION, "Database '"+dbConfig.getDbConfigData().getDbName()+"' created successfully.");
        }
        else AlertPopupUtil.alertMsg(Alert.AlertType.ERROR,"Could not create '"+dbConfig.getDbConfigData().getDbName()+"' database");
    }


    // create tables inner method
    private void createTblMethod(){

        if(ManageDbUtil.createTables(dbStructure)){
            AlertPopupUtil.alertMsg(Alert.AlertType.CONFIRMATION, "Successfully created tables. \n"+(DbCheckUtil.checkDbTblExist( dbConfig.getDbConfigData().getDbName() ).toString()) );
        }
        else AlertPopupUtil.alertMsg(Alert.AlertType.ERROR, "Could not create tables.");

    }


    // delete db inner method
    private boolean dropDbMethod(){
        if( ManageDbUtil.dropDb( dbConfig.getDbConfigData().getDbName()) ){
            return true;
        }
        else AlertPopupUtil.alertMsg(Alert.AlertType.ERROR, "Could not delete db '"+dbConfig.getDbConfigData().getDbName()+"'");    return false;
    }
}
