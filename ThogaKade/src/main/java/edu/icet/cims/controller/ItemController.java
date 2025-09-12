package edu.icet.cims.controller;

import edu.icet.cims.model.dto.ItemDto;
import edu.icet.cims.service.ServiceItem;
import edu.icet.cims.service.exception.ItemServiceException;
import edu.icet.cims.service.impl.ServiceItemImpl;
import edu.icet.cims.util.AlertPopupUtil;
import edu.icet.cims.util.SessionUserUtil;
import edu.icet.cims.util.WindowManagerUtil;
import edu.icet.cims.util.validator.CommonValidatorUtil;
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
import java.util.ResourceBundle;


public class ItemController implements Initializable {

    ServiceItem service = new ServiceItemImpl();

    @FXML
    private Button btn_clear;

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
    private TableView<ItemDto> tbl_itemsList;

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


    // logout
    @FXML
    void btn_LogoutAction(ActionEvent event) throws IOException {
        SessionUserUtil.clearSessionUser();
        WindowManagerUtil.switchScene(event, "/view/CIMS-Login.fxml");
    }


    // delete item
    @FXML
    void btn_deleteAction(ActionEvent event){

        try {
            service.deleteItemCall(txt_itemCode_Delete.getText().toUpperCase());

            AlertPopupUtil.alertMsg(Alert.AlertType.CONFIRMATION, "Item deleted successfully");
            clearFields();      // clear all fields
            loadItemData();     // load updated table

        } catch (ItemServiceException e) {
            AlertPopupUtil.alertMsg(Alert.AlertType.ERROR, e.getMessage());     // popup dialog box
        }
    }

    @FXML
    void btn_goBackAction(ActionEvent event) throws IOException {
        WindowManagerUtil.switchScene(event, "/view/Dashboard.fxml");
    }

    @FXML
    void btn_updateStockAction(ActionEvent event) {

        //RadioButton action = (RadioButton) radioToggleGroup.getSelectedToggle(); // if needed, can get value txt of selected radio btn
        String code = txt_itemCode.getText().toUpperCase();
        String description = txtArea_description.getText();
        String packSize = txt_packSize.getText();
        String price = txt_unitPrice.getText();
        String qty = txt_qty.getText();

        try {
            if (initialFieldValidation(code, description, packSize, price, qty)) {

                ItemDto item = new ItemDto( code, description, packSize + comboBox_unit.getValue(), Double.parseDouble(price), Integer.parseInt(qty) );

                // validate fields (throws exception if invalid)
                service.validateItemFields(item);

                if (radioBtn_new.isSelected()) {                // add new item to DB

                    service.addNewItem(item);

                    AlertPopupUtil.alertMsg(Alert.AlertType.CONFIRMATION, "Item added successfully");
                    clearFields();

                } else if (radioBtn_update.isSelected()) {      // update item DB

                    service.updateItem(item);

                    AlertPopupUtil.alertMsg(Alert.AlertType.CONFIRMATION, "Item updated successfully");
                    clearFields();

                } else {                                        //  if neither is selected
                    AlertPopupUtil.alertMsg(Alert.AlertType.WARNING, "Please select [New/Update]");
                }

                loadItemData();
            }
        } catch (ItemServiceException e) {
            AlertPopupUtil.alertMsg(Alert.AlertType.ERROR, e.getMessage());
        }

    }


    // clear all input text fields
    @FXML
    void btn_clearAllFieldsAction(ActionEvent event) {
        clearFields();
    }


    // Run at start of class
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        rowSelect();                                                            // select tbl row on click

        btn_updateStock.setDefaultButton(true);                                 // default btn, keyboard Enter key

        lbl_ativeUserID.setText(SessionUserUtil.getLoggedUser().getUserId());   // set active user id

        unitsComboBox();                                                        // set units for pack size

        // Execute populate item table command
        loadItemData();

    }

    // execute load table data call from DB
    private void loadItemData() {
        try{
            ObservableList<ItemDto> list = FXCollections.observableArrayList(
                    service.getItemDTOList());

            col_itemCode.setCellValueFactory(new PropertyValueFactory<>("code"));
            col_description.setCellValueFactory(new PropertyValueFactory<>("description"));
            col_packSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));
            col_unitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
            col_qty.setCellValueFactory(new PropertyValueFactory<>("qty"));

            tbl_itemsList.setItems(list);
        }
        catch (ItemServiceException e){
            AlertPopupUtil.alertMsg(Alert.AlertType.ERROR, e.getMessage());
        }
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

        radioBtn_new.setSelected(false);
        radioBtn_update.setSelected(false);

        comboBox_unit.setValue("");
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


    // select tbl row on click
    private void rowSelect(){

        tbl_itemsList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldDto, newDto) -> {

            if(newDto != null){
                txt_itemCode.setText(newDto.getCode());
                txt_itemCode_Delete.setText(newDto.getCode());
                txt_unitPrice.setText(String.valueOf(newDto.getUnitPrice()));
                txt_qty.setText(String.valueOf(newDto.getQty()));
                txtArea_description.setText(newDto.getDescription());

                txt_packSize.setText(newDto.getPackSize().replaceAll("[a-zA-Z]", ""));

                comboBox_unit.setValue(newDto.getPackSize().replaceAll("[0-9]",""));
            }

        } );
    }
}
