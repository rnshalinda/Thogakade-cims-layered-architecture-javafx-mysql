package edu.icet.cims.service;

import edu.icet.cims.model.dto.UserCredentialsDto;
import edu.icet.cims.service.exception.UserCredentialsException;

public interface ServiceCredential {

    void credentialsValidate(UserCredentialsDto credDTO) throws UserCredentialsException;
}
