package edu.icet.cims.controller;

import edu.icet.cims.model.dto.CustomerDto;
import edu.icet.cims.service.ServiceCustomer;
import edu.icet.cims.service.exception.CustomerServiceException;
import edu.icet.cims.service.impl.ServiceCustomerImpl;
import edu.icet.cims.util.AlertPopupUtil;
import edu.icet.cims.util.SessionUserUtil;
import edu.icet.cims.util.WindowManagerUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {

    private ServiceCustomer service = new ServiceCustomerImpl();

    @FXML
    private Button btn_delete;

    @FXML
    private Button btn_goBack;

    @FXML
    private Button btn_logout;

    @FXML
    private Button btn_updateCustList;

    @FXML
    private CheckBox checkBox_new;

    @FXML
    private CheckBox checkBox_update;

    @FXML
    private TableColumn<?, ?> col_address;

    @FXML
    private TableColumn<?, ?> col_city;

    @FXML
    private TableColumn<?, ?> col_custID;

    @FXML
    private TableColumn<LocalDate, ?> col_dob;      // colum type LocalDate to match dob customerDTO format

    @FXML
    private TableColumn<?, ?> col_name;

    @FXML
    private TableColumn<?, ?> col_postalCode;

    @FXML
    private TableColumn<?, ?> col_province;

    @FXML
    private TableColumn<?, ?> col_title;

    @FXML
    private TableView<CustomerDto> tbl_customerList;

    @FXML
    private ComboBox<String> comboBox_province;     // set type to String here

    @FXML
    private DatePicker date_dob;

    @FXML
    private Label lbl_ativeUserID;

    @FXML
    private ToggleGroup radioTitleGroup;

    @FXML
    private RadioButton radio_Mr;

    @FXML
    private RadioButton radio_Mrs;

    @FXML
    private TextArea txtArea_address;

    @FXML
    private TextField txt_City;

    @FXML
    private TextField txt_custID;

    @FXML
    private TextField txt_custID_Delete;

    @FXML
    private TextField txt_name;

    @FXML
    private TextField txt_poatalCode;


    @FXML
    void btn_LogoutAction(ActionEvent event) throws IOException {
        SessionUserUtil.clearSessionUser();
        WindowManagerUtil.switchScene(event, "/view/CIMS-Login.fxml");  // current window even, and path to needed scene
    }

    @FXML
    void btn_goBackAction(ActionEvent event) throws IOException {
        WindowManagerUtil.switchScene(event, "/view/Dashboard.fxml");
    }

    @FXML
    void chkBox_addCustomerAction(ActionEvent event) {
        checkBox_update.setSelected(false);
    }

    @FXML
    void chkBox_updateCustomerAction(ActionEvent event) {
        checkBox_new.setSelected(false);
    }


    // delete customer
    @FXML
    void btn_deleteAction(ActionEvent event) {
        try {
            service.deleteCustomerCall(txt_custID_Delete.getText().toUpperCase());          // try to delete if fail throw exception

            AlertPopupUtil.alertMsg(Alert.AlertType.CONFIRMATION, "Successfully deleted customer.");
            loadCustomerData();
            txt_custID_Delete.clear();

        }catch (CustomerServiceException e){
            AlertPopupUtil.alertMsg(Alert.AlertType.ERROR, e.getMessage());
        }
    }


    // update/add-new customer
    @FXML
    void btn_updateCustListAction(ActionEvent event) {

        String custId = txt_custID.getText().toUpperCase();                                 // Customer ID
        String title =  ((RadioButton) radioTitleGroup.getSelectedToggle()).getText();      // Title Mr. / Mrs
        String name = txt_name.getText();                                                   // Customer name
        LocalDate dob = date_dob.getValue();                                                // Date of birth
        String address = txtArea_address.getText();                                         // Address
        String city = txt_City.getText();                                                   // City
        String province = comboBox_province.getValue();                                     // Province
        String postalCode = txt_poatalCode.getText();                                       // Postal code

        try {
            // initial validation of txt fields
            if (initialFieldValidation(custId, title, name, dob, address, city, province, postalCode)) {                  // initial validation txt fields

                city = txt_City.getText().substring(0, 1).toUpperCase() + txt_City.getText().substring(1);     // converts first letter to uppercase

                // Item object
                CustomerDto customerDTO = new CustomerDto(custId, title, name, dob, address, city, province, postalCode);

                // txt fields format validation
                service.fieldFormatValidation(customerDTO);

                // Execute DB call
                if (checkBox_new.isSelected()) {                        // add new customer to DB

                    service.addNewCustomer(customerDTO);                // returns confirm / throw error msg

                    AlertPopupUtil.alertMsg(Alert.AlertType.CONFIRMATION, "Successfully added new customer.");
                    clearFields();

                } else if (checkBox_update.isSelected()) {              // update customer in DB

                    service.updateCustomer(customerDTO);                // returns confirm / throw error msg

                    AlertPopupUtil.alertMsg(Alert.AlertType.CONFIRMATION, "Successfully updated customer.");
                    clearFields();

                } else {
                    AlertPopupUtil.alertMsg(Alert.AlertType.WARNING, "Action not selected [Add new / Update]");     // if Action to perform not selected
                }

                loadCustomerData();                                     // load updated table
            }

        } catch (CustomerServiceException e) {
            AlertPopupUtil.alertMsg(Alert.AlertType.ERROR, e.getMessage());     // error display msg dialog-box
        }

    }

    // Run at start of class
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Define formatter for output
        datePickerConverter();

        rowSelect();                // select tbl row on click

        setProvinceDetails();       // comboBox_province combo box values

        lbl_ativeUserID.setText(SessionUserUtil.getLoggedUser().getUserId());   // set active user id

        loadCustomerData();         // Execute populate customer table

    }

    // execute load table data call from DB
    private void loadCustomerData() {
        try {
            ObservableList<CustomerDto> list = FXCollections.observableArrayList(
                    service.getCustomerDTOList());

            col_custID.setCellValueFactory(new PropertyValueFactory<>("id"));
            col_title.setCellValueFactory(new PropertyValueFactory<>("title"));
            col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
            col_dob.setCellValueFactory(new PropertyValueFactory<>("dob"));
            col_address.setCellValueFactory(new PropertyValueFactory<>("address"));
            col_city.setCellValueFactory(new PropertyValueFactory<>("city"));
            col_province.setCellValueFactory(new PropertyValueFactory<>("province"));
            col_postalCode.setCellValueFactory(new PropertyValueFactory<>("postalCode"));

            tbl_customerList.setItems(list);

        } catch (CustomerServiceException e) {
            AlertPopupUtil.alertMsg(Alert.AlertType.ERROR, e.getMessage());
        }
    }

    // set provinces for comboBox_province
    private void setProvinceDetails(){

        comboBox_province.setPromptText("Select");

        ObservableList<String> provinces = FXCollections.observableArrayList();
        provinces.addAll("Central", "Eastern", "North Central" , "Northern", "North Western", "Sabaragamuwa", "Southern", "Uva", "Western");
        comboBox_province.setItems(provinces);
    }


    // clear all txt fields
    private void clearFields(){
        txt_custID.clear();
        txt_name.clear();
        txtArea_address.clear();
        txt_City.clear();
        txt_poatalCode.clear();
        txt_custID_Delete.clear();
    }

    // Initial validations - UI level
    private boolean initialFieldValidation(String custId, String title, String name, LocalDate dob, String address, String city, String province, String postalCode){

        String status;

        if(custId.isEmpty() || title.isEmpty() || name.isEmpty() || dob==null || address.isEmpty() || city.isEmpty()  || postalCode.isEmpty()){     // null check
            status = "Fields cannot be empty";

        } else if (comboBox_province.getValue() == null) {
            status = "Please select province";

        } else { return true; }                                         // if all above conditions pass

        AlertPopupUtil.alertMsg(Alert.AlertType.WARNING, status);       // display warning msg dialog-box
        return false;                                                   // if any condition fail
    }


    // select tbl row on click
    private void rowSelect(){
        tbl_customerList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldDto, newDto) -> {

            if(newDto != null){
                txt_custID.setText(newDto.getId());
                txt_name.setText(newDto.getName());
                txtArea_address.setText(newDto.getAddress());
                txt_City.setText(newDto.getCity());
                txt_poatalCode.setText(newDto.getPostalCode());

                txt_custID_Delete.setText(newDto.getId());

                comboBox_province.setValue(newDto.getProvince());

                radioTitleGroup.selectToggle( newDto.getTitle().equals("Mr.") ? radio_Mr : radio_Mrs );

                date_dob.setValue(newDto.getDob());
            }

        });
    }

    // convert data format from "M/d/yyyy" to "d/M/yyyy"
    private void datePickerConverter(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");

        date_dob.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty())
                        ? LocalDate.parse(string, formatter)
                        : null;
            }
        });
    }
}
