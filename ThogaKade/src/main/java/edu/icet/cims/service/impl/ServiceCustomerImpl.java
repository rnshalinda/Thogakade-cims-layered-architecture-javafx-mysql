package edu.icet.cims.service.impl;

import edu.icet.cims.model.dto.CustomerDto;
import edu.icet.cims.model.entity.CustomerEntity;
import edu.icet.cims.repository.RepositoryCustomer;
import edu.icet.cims.service.ServiceCustomer;
import edu.icet.cims.service.exception.CustomerServiceException;
import edu.icet.cims.util.AlertPopupUtil;
import edu.icet.cims.util.validator.CommonValidatorUtil;
import edu.icet.cims.util.validator.CustomerValidatorUtil;
import javafx.scene.control.Alert;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceCustomerImpl implements ServiceCustomer {

    RepositoryCustomer repository = new RepositoryCustomer();

    // populate customer details table
    @Override
    public List<CustomerDto> getCustomerDTOList() {

        try {
            ArrayList<CustomerEntity> entities = repository.getCustomerEntityList();

            List<CustomerDto> listDTO = new ArrayList<>();

            for (CustomerEntity entity : entities) {
                listDTO.add(
                        new CustomerDto(
                                entity.getCustomerId(),
                                entity.getTitle(),
                                entity.getName(),
                                entity.getDob(),
                                entity.getAddress(),
                                entity.getCity(),
                                entity.getProvince(),
                                entity.getPostalCode()
                        )
                );
            }
            return listDTO;

        } catch (SQLException e) {
            throw new CustomerServiceException("Database error while fetching customer list, check SQL syntax.", e);
        }
    }

    // delete customer from DB
    @Override
    public void deleteCustomerCall(String custID) {
        // validate customer id
        String status = validateCustId(custID);
        if (!status.equalsIgnoreCase("Valid")) {
            throw new CustomerServiceException(status);
        }
        // try delete
        try {
            boolean deleted = repository.deleteCustomer(custID);
            if(!deleted){
                throw new CustomerServiceException( "Error! Customer may not exist" );
            }
        } catch (SQLException e) {
            throw new RuntimeException( "Database error while deleting customer, check SQL syntax.", e);
        }
    }


    // validate customer id
    private String validateCustId(String custId){
        return (custId.isEmpty() || custId == null) ? "Null Input" : (!custId.matches("^[a-zA-Z0-9]+$")) ? "Invalid Customer-ID format" : "Valid";
    }



    @Override
    public void addNewCustomer(CustomerDto customerDTO) {
        try {
            CustomerEntity customerEntity = new CustomerEntity(customerDTO.getId(), customerDTO.getTitle(), customerDTO.getName(), customerDTO.getDob(), customerDTO.getAddress(), customerDTO.getCity(), customerDTO.getProvince(), customerDTO.getPostalCode());
            // check if customer already exits
            if (repository.customerExistCheck(customerEntity.getCustomerId())) {
                throw new CustomerServiceException("Error! Customer already exist in Database");
            }

            // add new customer
            boolean added = repository.addNewCustomer(customerEntity);
            if( !added ){
                throw new CustomerServiceException("Could not add new customer");
            }

        }catch (SQLException e){
            throw new CustomerServiceException("Database error while adding customer, check SQL syntax.", e);
        }
    }

    // Update customer details
    @Override
    public void updateCustomer(CustomerDto customerDTO) {
        try{
            CustomerEntity customerEntity = new CustomerEntity(customerDTO.getId(), customerDTO.getTitle(), customerDTO.getName(), customerDTO.getDob(), customerDTO.getAddress(), customerDTO.getCity(), customerDTO.getProvince(), customerDTO.getPostalCode());

            // check if customer exist
            if(repository.customerExistCheck(customerEntity.getCustomerId())){

                boolean updated = repository.updateCustomer(customerEntity);

                if( !updated ) throw new CustomerServiceException("Failed to update customer");

            }else throw new CustomerServiceException("Error! Customer not found in Database, Check customer-ID");

        }catch (SQLException e){
            throw new CustomerServiceException("Database error while updating customer, check SQL syntax.", e);
        }
    }


    @Override
    // validate fields
    public void fieldFormatValidation(CustomerDto customerDTO){

        // validate customer id format - alphanumeric only
        if(!CustomerValidatorUtil.isValidCustId(customerDTO.getId())){
            throw new CustomerServiceException( "Invalid Customer-ID, Allowed format :- C001" );
        }

        // validate customer name - checks if letters only, name format, char length
        if(!CustomerValidatorUtil.isValidName(customerDTO.getName()) || !CommonValidatorUtil.isLettersOnly(customerDTO.getName())){                      // name format

            if(!CommonValidatorUtil.charLengthCheck(customerDTO.getName(), 30)){       // char limit
                throw new CustomerServiceException( "Name exceeded max allowed character length 30" );
            }
            throw new CustomerServiceException( "Invalid name, cannot contain symbols, first letter uppercase & space between names :- Jack Sparrow" );
        }

        // cheks if DOB is a future date
        if(!CustomerValidatorUtil.isDobFuture(customerDTO.getDob())){
            throw new CustomerServiceException( "DOB cannot be in the future." );
        }

        // cheks customer age to be less than 120 from given dob
        if(!CommonValidatorUtil.isValidAge(customerDTO.getDob(), 120)){
            throw new CustomerServiceException( "Invalid DOB, Customer age cannot be over 120 years" );
        }

        // validate address - allowed characters (Aa9-\#.,/:)
        if(!CommonValidatorUtil.isValidAddress(customerDTO.getAddress())){
            throw new CustomerServiceException( "Invalid Address, only allowed characters ( Aa 9-\\#.,/: )" );
        }

        // validate city only alphabet characters
        if(!CommonValidatorUtil.isLettersOnly(customerDTO.getCity())){
            throw new CustomerServiceException( "Invalid, City only accept english alphabet character :- Colombo" );
        }

        // validate postal code - only alphanumeric & #.,-/ \
        if(!CustomerValidatorUtil.isValidPostal(customerDTO.getPostalCode())){
            throw new CustomerServiceException( "Invalid, Postal code can only contain :- Aa3#-/ \\,." );
        }

        // if all fields has correct format
    }

}
