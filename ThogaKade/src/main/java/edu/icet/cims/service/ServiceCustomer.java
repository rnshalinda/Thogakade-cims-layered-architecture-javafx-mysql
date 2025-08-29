package edu.icet.cims.service;

import edu.icet.cims.model.dto.CustomerDTO;
import edu.icet.cims.model.entity.CustomerEntity;
import edu.icet.cims.repository.RepositoryCustomer;
import edu.icet.cims.util.validator.CommonValidatorUtil;
import edu.icet.cims.util.validator.CustomerValidatorUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceCustomer {

    RepositoryCustomer repository = new RepositoryCustomer();

    // populate customer details table
    public List<CustomerDTO> getCustomerDTOList() throws SQLException {
        ArrayList<CustomerEntity> entities= repository.getCustomerEntityList();

        List<CustomerDTO> listDTO = new ArrayList<>();

        for (CustomerEntity entity : entities){
            listDTO.add(
                    new CustomerDTO(
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
    }

    // delete customer from DB
    public String deleteCustomerCall(String custID) throws SQLException {
        String status = validateCustId(custID);
        if(status.equalsIgnoreCase("Valid")){
            return (repository.deleteCustomer(custID)) ? "Successful" : "Error! Customer may not exist";
        }
        return status;
    }

    // validate customer id
    private String validateCustId(String custId){
        return (custId.isEmpty() || custId == null) ? "Null Input" : (!custId.matches("^[a-zA-Z0-9]+$")) ? "Invalid Customer-ID format" : "Valid";
    }


    // validate fields
    public String fieldFormatValidation(CustomerDTO customerDTO){

        // validate customer id format - alphanumeric only
        if(!CustomerValidatorUtil.isValidCustId(customerDTO.getId())){
            return "Invalid Customer-ID, Allowed format :- C001";
        }

        // validate customer name - checks if letters only, name format, char length
        if(!CustomerValidatorUtil.isValidName(customerDTO.getName()) || !CommonValidatorUtil.isLettersOnly(customerDTO.getName())){                      // name format

            if(!CommonValidatorUtil.charLengthCheck(customerDTO.getName(), 30)){       // char limit
                return "Name exceeded max allowed character length 30";
            }
            return "Invalid name, cannot contain symbols, first letter uppercase & space between names :- Jack Sparrow";
        }

        // cheks if DOB is a future date
        if(!CustomerValidatorUtil.isDobFuture(customerDTO.getDob())){
            return "DOB cannot be in the future.";
        }

        // cheks customer age to be less than 120 from given dob
        if(!CommonValidatorUtil.isValidAge(customerDTO.getDob(), 120)){
            return "Invalid DOB, Customer age cannot be over 120 years";
        }

        // validate address - allowed characters (Aa9-\#.,/:)
        if(!CommonValidatorUtil.isValidAddress(customerDTO.getAddress())){
            return "Invalid Address, only allowed characters ( Aa 9-\\#.,/: )";
        }

        // validate city only alphabet characters
        if(!CommonValidatorUtil.isLettersOnly(customerDTO.getCity())){
            return "Invalid, City only accept english alphabet character :- Colombo";
        }

        // validate postal code - only alphanumeric & #.,-/ \
        if(!CustomerValidatorUtil.isValidPostal(customerDTO.getPostalCode())){
            return "Invalid, Postal code can only contain :- Aa3#-/ \\,.";
        }

        return "valid";         // if all fields has correct format
    }
}
