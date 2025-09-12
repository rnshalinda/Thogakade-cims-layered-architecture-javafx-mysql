package edu.icet.cims.controller.configDb;

import edu.icet.cims.controller.util.ConfirmationWindowController;
import edu.icet.cims.db.DbConnection;
import edu.icet.cims.db.DbConfig;
import edu.icet.cims.model.dto.DbConfigDto;
import edu.icet.cims.util.AlertPopupUtil;
import edu.icet.cims.util.WindowManagerUtil;
import edu.icet.cims.util.configDb.InsertDbDataUtil;
import edu.icet.cims.util.configDb.ManageDbUtil;
import edu.icet.cims.util.configDb.DbCheckUtil;
import edu.icet.cims.util.configDb.SaveDbConfigUtil;
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
    private Button btn_addTbl;

    @FXML
    private Button btn_addData;

    @FXML
    private Button btn_remove;

    @FXML
    private Button btn_changeDbName;

    @FXML
    private Button btn_createDb;

    @FXML
    private Button btn_goBack;

    @FXML
    private CheckBox chkBox_defaultDbName;

    @FXML
    private CheckBox chkBox_populateTbl;

    @FXML
    private ComboBox<String> comboBox1_selectTbl;

    @FXML
    private ComboBox<String> comboBox2_selectTbl;

    @FXML
    private StackPane stackPane_contentArea;

    @FXML
    private TextArea txtArea_sqlQuery;

    @FXML
    private TextField txt_dbName;



    String newDbName = "";       // new dbName holder

    // db Structure List
    // contains -> { dbName, tbl1, tbl2, tbl3.... } ** must start with dbName
    ArrayList<String> dbStructure = new ArrayList<>();              // contain db tbl structure
    ArrayList<String> addDataTblList = new ArrayList<>();           // contain selected tables to add data


    // change dbName
    @FXML
    void btn_changeDbNameAction(ActionEvent event) {

        newDbName = txt_dbName.getText();       // new dbName
        editDbStructureList();                  // update db Structure List - replace first element dbName with new name
        viewDbStructure(dbStructure, addDataTblList);
    }


    // add tables to db structure
    @FXML
    void btn_addTblAction(ActionEvent event) {

        // check if selection combo-box not null
        if(comboBox1_selectTbl.getValue() != null){

            dbStructure.add(comboBox1_selectTbl.getValue());                        // append selected combo-box value(table name) to dbStructure list

            viewDbStructure(dbStructure, addDataTblList);                           // preview db structure in stack-pane area

            comboTables2.add(comboBox1_selectTbl.getValue());                       // update combo-box2 selection
            updateSelectComboBox2(dbStructure, addDataTblList);

            comboBox1_selectTbl.getItems().remove(comboBox1_selectTbl.getValue());  // remove item from selection combo-box once added

        }

        removeBtnState();       // change remove btn state
    }


    // remove items form stack-pane area and re populate selection combo
    @FXML
    void btn_removeAction(ActionEvent event) {

        if(dbStructure.toArray().length > 1) {

            comboTables1.add(dbStructure.getLast());                // get last added element to dbStructure and add it to combo
            if(!addDataTblList.isEmpty()) removeElementFromAddDataTblList(dbStructure.getLast());     // update data add pending tbl list

            dbStructure.removeLast();                               // remove that last element from dbStructure

            viewDbStructure(dbStructure, addDataTblList);           // view in stack-pane

            updateSelectComboBox2(dbStructure, addDataTblList);     // update comboxo 2
        }

        removeBtnState();       // change remove btn state
    }



    // create db
    @FXML
    void btn_createDbAction(ActionEvent event) throws IOException, SQLException {

        // check if user added at least one table to the db before finalizing
        if(!(dbStructure.toArray().length <= 1)) {

            // if default dbName check-box not selected, create new dbName and url DbConfigDTO
            if (!chkBox_defaultDbName.isSelected()) {

                // store new DbConfigDTO in dbConfig.setDbConfigData()
                DbConfig.setDbConfigData(new DbConfigDto(DbConfig.getDbConfigData().getHost() ,newDbName, DbConfig.getDbConfigData().getUser(), DbConfig.getDbConfigData().getPswd(), DbConfig.getDbConfigData().getPort(), DbConfig.getDbConfigData().getExtraParam()));
            }

            // create database
            // check if db with user given name already exist
            if ( DbCheckUtil.isDbAvailable( DbConfig.getDbConfigData().getDbName()) ) {

                ArrayList dbList = DbCheckUtil.checkDbTblExist(DbConfig.getDbConfigData().getDbName());      // if db exist, return list containing existing tables
                dbList.addFirst(DbConfig.getDbConfigData().getDbName());                                     // push existing db name as first element

                // Ask user confirmation, Yes/No
                if (launchConfirmWindow( dbList )) {      // if 'yes' execute below

                    // delete existing db
                    if (dropDbMethod()) {
                        // and create new create db
                        if(createDbMethod()){
                            // create tables
                            if(createTblMethod()){
                                // pupate tables with dummy data
                                if(populateTblData()){

                                    if(!SaveDbConfigUtil.saveToYlm(DbConfig.getDbConfigData())){

                                        AlertPopupUtil.alertMsg(Alert.AlertType.ERROR, "Error caused while saving db-config.yml");

                                    }else AlertPopupUtil.alertMsg(Alert.AlertType.CONFIRMATION, "Database '"+ DbConfig.getDbConfigData().getDbName()+"' created successfully.\n\nTables,\n->"+(DbCheckUtil.checkDbTblExist( DbConfig.getDbConfigData().getDbName() ))+"\n\nData added to tables successful");
                                }
                                else   AlertPopupUtil.alertMsg(Alert.AlertType.INFORMATION, "Database '"+ DbConfig.getDbConfigData().getDbName()+"' created with empty tables.\n\nTables,\n->"+(DbCheckUtil.checkDbTblExist( DbConfig.getDbConfigData().getDbName() )));

                            }else   AlertPopupUtil.alertMsg(Alert.AlertType.ERROR, "Could not create tables.");

                        } else   AlertPopupUtil.alertMsg(Alert.AlertType.ERROR,"Could not create '"+ DbConfig.getDbConfigData().getDbName()+"' database");
                    }
                }
                // if 'No' close the window without doing anything
            }
            // if it doesn't exist, go straight to create new db
            else {
                createDbMethod();
                DbConnection.useDb( DbConfig.getDbConfigData().getDbName());
                createTblMethod();
            }
        }
        // if user not add tables show warning
        else AlertPopupUtil.alertMsg(Alert.AlertType.WARNING, "You have not added any tables.");
    }


    // go back
    @FXML
    void btn_goBackAction(ActionEvent event) throws IOException {
        WindowManagerUtil.switchScene(event, "/view/configDb/ConfigDbAccess.fxml");
    }


    @FXML
    void btn_addDataToTblAction(ActionEvent event) throws Exception {

        if(comboBox2_selectTbl.getValue() != null){

            addDataTblList.add(comboBox2_selectTbl.getValue());

            viewDbStructure(dbStructure, addDataTblList);                           //  overloaded methode, passed the selected tbl name

            comboBox2_selectTbl.getItems().remove(comboBox2_selectTbl.getValue());  // remove item from selection combo-box once added
        }
    }


    // default db name check-box
    @FXML
    void chkBox_defaultDbNameAction(ActionEvent event) {
        changeDbNameState();                                        // enable/disable dbName txt, change btn
        if(chkBox_defaultDbName.isSelected()) {
            newDbName = DbConfig.getDbConfigData().getDbName();     // reassign default db name from yml as newDbName
            editDbStructureList();                                  // update db Structure list - replace first element
        }
    }


    @FXML
    void chkBox_populateTblAction(ActionEvent event) {
        populateDbTblState();                                       // enable / disable fields
    }


    // populate preview with sql are when selecting combo value
    @FXML
    void comboBox1_selectTblAction(ActionEvent event) {
        txtArea_sqlQuery.setWrapText(true);

        if(comboBox1_selectTbl.getValue() != null) {
            txtArea_sqlQuery.setText(getSql(comboBox1_selectTbl.getValue()));    // get table sql string and show in preview area
        }
    }



    // initially apply these settings
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        comboBox2_selectTbl.setDisable(true);
        btn_addData.setDisable(true);

        // disable remove tables
        btn_remove.setDisable(true);

        // push the current dbName in DbConfigDTO to dbStructure list as first element
        dbStructure.add(DbConfig.getDbConfigData().getDbName());

        // add table selections to combo-box comboBox_selectTbl
        loadSelectComboBox();

        // set current db name as default name in check-box
        chkBox_defaultDbName.setText("default name '"+ DbConfig.getDbConfigData().getDbName());
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



    ObservableList<String> comboTables1 = FXCollections.observableArrayList();
    // set values to combo box
    private void loadSelectComboBox(){
        comboTables1.addAll(ManageDbUtil.getRequiredTableNames());       // get tables names as list from ManageDbUtil
        comboBox1_selectTbl.setItems(comboTables1);
    }



    ObservableList<String> comboTables2 = FXCollections.observableArrayList();
    // update combo box 2
    private void updateSelectComboBox2(ArrayList<String> dbStructure, ArrayList<String> addDataTblList){
        comboTables2.clear();
        if( dbStructure.toArray().length > 1 ) {
            for(int i = 1; i < dbStructure.toArray().length; i++){

                if(!addDataTblList.contains( dbStructure.get(i) )){
                    comboTables2.add( dbStructure.get(i) );
                }
            }
        }

        if(!comboTables2.isEmpty()) {
            comboBox2_selectTbl.setItems(comboTables2);
        }
    }

    // remove element from addDataTblList
    private void removeElementFromAddDataTblList(String element){
        if(addDataTblList.contains(element)){
            addDataTblList.remove(element);
        }
    }



    // get sql query for selected table in combo-box, to display in stack-pane area
    private String getSql(String str){
        if (str.equalsIgnoreCase("item")) return ManageDbUtil.getItemTbl();
        if (str.equalsIgnoreCase("customer")) return ManageDbUtil.getCustomerTbl();
        if (str.equalsIgnoreCase("credentials")) return ManageDbUtil.getCredentialsTbl();
        return null;
    }


    // preview area for db structure - dbName, tables
    // overloaded methode
    private void  viewDbStructure(ArrayList<String> dbTblList, ArrayList<String> addDataTblList){
        for(int i = 0; i < dbTblList.toArray().length; i++){

            if(i == 0)  txtArea_sqlQuery.setText("db - "+ dbTblList.get(i));                                    // setText - clear text area set  first [i==0] element of list

            else {
                if(addDataTblList.contains( dbTblList.get(i) )){
                    txtArea_sqlQuery.appendText("\n\t|-->" + dbTblList.get(i) + " (table - pending data)");   // append every-other list element as new line text
                }
                else txtArea_sqlQuery.appendText("\n\t|-->" + dbTblList.get(i) + " (table - empty)");         // append every-other list element as new line text

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

        boolWindow.setStackpane(dbList);                                    // pass list elements to set stack-pane
        boolWindow.setHeading("Database already exists, Replace?");         // window heading methode

        // Show the new scene
        Stage stage = new Stage();
        stage.setScene(new Scene(root));

        stage.initModality(Modality.APPLICATION_MODAL);     // block other windows
        stage.showAndWait();                                // wait for user to press Yes/No

        return boolWindow.getResult();                      // fetch result after bool confirm window closes
    }


    // create db inner method
    private boolean createDbMethod(){
        return (ManageDbUtil.createDB( DbConfig.getDbConfigData().getDbName()) ) ? true : false;
    }


    // create tables inner method
    private boolean createTblMethod(){
        return (ManageDbUtil.createTables(dbStructure)) ? true : false;
    }


    // delete db inner method
    private boolean dropDbMethod(){
        if( ManageDbUtil.dropDb( DbConfig.getDbConfigData().getDbName()) ){
            return true;
        }
        else AlertPopupUtil.alertMsg(Alert.AlertType.ERROR, "Could not delete db '"+ DbConfig.getDbConfigData().getDbName()+"'");    return false;
    }


    // populate tables with data
    private boolean populateTblData(){

        if(!addDataTblList.isEmpty() && chkBox_populateTbl.isSelected()) {
            return InsertDbDataUtil.runSeedQueries(addDataTblList);
        }
        return false;
    }


    // change remove btn state -  activate only if tattletale 1 tbl added
    private void removeBtnState(){

        if(dbStructure.toArray().length > 1){
            btn_remove.setDisable(false);
        }
        else btn_remove.setDisable(true);
    }


    // check box populate tables state
    private void populateDbTblState(){

        if(chkBox_populateTbl.isSelected()){
            comboBox2_selectTbl.setDisable(false);
            btn_addData.setDisable(false);
        }
        else{
            comboBox2_selectTbl.setDisable(true);
            btn_addData.setDisable(true);
        }
    }

}
