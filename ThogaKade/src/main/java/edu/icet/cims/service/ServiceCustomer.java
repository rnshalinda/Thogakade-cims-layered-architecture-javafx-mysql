package edu.icet.cims.service;

import edu.icet.cims.model.dto.CustomerDto;
import edu.icet.cims.service.exception.CustomerServiceException;

import java.util.List;

public interface ServiceCustomer {

    List<CustomerDto> getCustomerDTOList() throws CustomerServiceException;

    void fieldFormatValidation(CustomerDto customerDTO) throws CustomerServiceException;

    void deleteCustomerCall(String custId) throws CustomerServiceException;

    void addNewCustomer(CustomerDto customerDTO) throws CustomerServiceException;

    void updateCustomer(CustomerDto customerDTO) throws CustomerServiceException;


}
