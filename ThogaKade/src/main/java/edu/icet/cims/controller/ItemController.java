package edu.icet.cims.controller;

import edu.icet.cims.model.dto.ItemDTO;
import edu.icet.cims.service.ServiceItem;
import edu.icet.cims.utill.AlertPopupUtil;
import edu.icet.cims.utill.SessionUserUtil;
import edu.icet.cims.utill.WindowManagerUtil;
import edu.icet.cims.utill.validator.CommonValidatorUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class ItemController implements Initializable {

    ServiceItem service = new ServiceItem();

    @FXML
    private Button btn_delete;

    @FXML
    private Button btn_goBack;

    @FXML
    private Button btn_logout;

    @FXML
    private Button btn_updateStock;


    @FXML
    private RadioButton radioBtn_new;

    @FXML
    private RadioButton radioBtn_update;

    @FXML
    private ToggleGroup radioToggleGroup;   // Action toggle group, didn't use here but kept as a reminder (function - auto switches when one option is selected)

    @FXML
    private TableColumn<?, ?> col_description;

    @FXML
    private TableColumn<?, ?> col_itemCode;

    @FXML
    private TableColumn<?, ?> col_packSize;

    @FXML
    private TableColumn<?, ?> col_qty;

    @FXML
    private TableColumn<?, ?> col_unitPrice;

    @FXML
    private Label lbl_ativeUserID;

    @FXML
    private TableView<ItemDTO> tbl_itemsList;

    @FXML
    private TextArea txtArea_description;

    @FXML
    private TextField txt_itemCode;

    @FXML
    private TextField txt_itemCode_Delete;

    @FXML
    private TextField txt_packSize;

    @FXML
    private ComboBox<String> comboBox_unit;     // put String type here to set list elements

    @FXML
    private TextField txt_qty;

    @FXML
    private TextField txt_unitPrice;

    @FXML
    void btn_LogoutAction(ActionEvent event) throws IOException {
        SessionUserUtil.clearSessionUser();
        WindowManagerUtil.switchScene(event, "/view/CIMS-Login.fxml");
    }

    @FXML
    void btn_deleteAction(ActionEvent event) throws SQLException {
        String status = service.deleteItemCall(txt_itemCode_Delete.getText().toUpperCase());
        if(status.equalsIgnoreCase("successful")){

            AlertPopupUtil.alertMsg(Alert.AlertType.CONFIRMATION, "Item delete "+status); // popup dialog box
            clearFields();      // clear all fields
            loadItemData();     // load updated table
        }
        else{
            clearFields();      // clear all fields
            AlertPopupUtil.alertMsg(Alert.AlertType.ERROR, status); // popup dialog box
        }
    }

    @FXML
    void btn_goBackAction(ActionEvent event) throws IOException {
        WindowManagerUtil.switchScene(event, "/view/Dashboard.fxml");
    }

    @FXML
    void btn_updateStockAction(ActionEvent event) throws SQLException {
//        RadioButton action = (RadioButton) radioToggleGroup.getSelectedToggle(); // if needed, can get value txt of selected radio btn

        String code = txt_itemCode.getText().toUpperCase();         // item code
        String description = txtArea_description.getText();         // item description
        String packSize = txt_packSize.getText();                   // Pack size
        String price = txt_unitPrice.getText();                     // Unit price
        String qty = txt_qty.getText();                             // Quantity on hand

        String status = "Unknown error!";
        boolean flag = false;
        Alert.AlertType alertType = Alert.AlertType.ERROR;          // default AlertType ERROR

        // initial validation of txt fields
        if( initialFieldValidation(code, description, packSize, price, qty) ) {
            // Item object
            ItemDTO item =  new ItemDTO(code, description, packSize+comboBox_unit.getValue(), Double.parseDouble(price), Integer.parseInt(qty));

            // txt fields format validation
            status = service.fieldFormatValidation(item);

            // if validate pass execute DB call
            if(status.equalsIgnoreCase("valid")){

                if(radioBtn_new.isSelected()){                      // add new item to DB
                    status = service.addNewItem(item);              // returns confirm / error msg
                    flag = true;
                }
                else if(radioBtn_update.isSelected()){              // update item DB
                    status = service.updateItem(item);
                    flag = true;
                }
                else{
                    status = "Action not selected [New/Update]";    // if Action to perform not selected
                    alertType = Alert.AlertType.WARNING;
                }
            }
            // perform after DB update
            if(flag){
                alertType = Alert.AlertType.CONFIRMATION;
                loadItemData();     // load updated table
                clearFields();      // clear all fields
            }
            AlertPopupUtil.alertMsg( alertType, status );     // display msg dialog-box (errors, confirmations)
        }
    }

    // Run at start of class
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        btn_updateStock.setDefaultButton(true);                                 // default btn, keyboard Enter key

        lbl_ativeUserID.setText(SessionUserUtil.getLoggedUser().getUserId());   // set active user id

        unitsComboBox();                                                        // set units for pack size

        // Execute populate item table command
        try {
            loadItemData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // execute load table data call from DB
    private void loadItemData() throws SQLException {

        ObservableList<ItemDTO> list = FXCollections.observableArrayList(
                service.getItemDTOList());

        col_itemCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        col_description.setCellValueFactory(new PropertyValueFactory<>("description"));
        col_packSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));
        col_unitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        col_qty.setCellValueFactory(new PropertyValueFactory<>("qty"));

        tbl_itemsList.setItems(list);
    }

    // set units in comboBox_unit for packSize
    private void unitsComboBox(){
        ObservableList<String> units = FXCollections.observableArrayList();
        units.addAll("g", "kg", "ml","L" );
        comboBox_unit.setItems(units);
    }

    // clear all txt fields
    private void clearFields(){
        txt_itemCode_Delete.clear();
        txt_itemCode.clear();
        txtArea_description.clear();
        txt_unitPrice.clear();
        txt_packSize.clear();
        txt_qty.clear();
    }

    // Initial validations - UI level
    private boolean initialFieldValidation(String code, String description, String packSize, String price, String qty){
        String status = null;

        if(code.isEmpty() || description.isEmpty() || packSize.isEmpty() || price.isEmpty() || qty.isEmpty()){     // null check
            status = "Fields cannot be empty";

        } else if (comboBox_unit.getValue() == null) {
            status = "Please select unit size";

        } else if (!CommonValidatorUtil.isDoubleFormat(price)) {                 // is double
            status = "Invalid unit price";

        } else if (!CommonValidatorUtil.isIntFormat(qty)) {                      // is int
            status = "Invalid quantity";
        }
        else { return true; }                                           // if all above conditions pass

        AlertPopupUtil.alertMsg(Alert.AlertType.WARNING, status);       // display warning msg dialog-box
        return false;                                                   // if any condition fail
    }

}
