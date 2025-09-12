package edu.icet.cims.service.impl;

import edu.icet.cims.model.dto.ActiveUserDto;
import edu.icet.cims.model.dto.UserCredentialsDto;
import edu.icet.cims.model.entity.UserCredentialsEntity;
import edu.icet.cims.repository.RepositoryCredentials;
import edu.icet.cims.service.ServiceCredential;
import edu.icet.cims.service.exception.UserCredentialsException;
import edu.icet.cims.util.SessionUserUtil;

import java.sql.SQLException;


public class ServiceCredentialsImpl implements ServiceCredential {
    
    private RepositoryCredentials repository = new RepositoryCredentials();

    // if credDTO (username. password) validated return ActiveUserDto (active user-name, user id)
    @Override
    public void credentialsValidate(UserCredentialsDto credDTO){
        try {
            // if not true - trigger
            if(!validateUsername(credDTO)){
                throw new UserCredentialsException("Username should only contain alphanumerics");
            }

            if(!validatePassword(credDTO)){
                throw new UserCredentialsException("Password must be 3–50 characters & cannot contain whitespaces.");
            }

            // if validation pass check user details in db
            ActiveUserDto loggedUserDetail = repository.credentialsCheck(new UserCredentialsEntity(credDTO.getUname(), credDTO.getPswd()));

            if (loggedUserDetail == null) {
                throw new UserCredentialsException("Incorrect Password or Username");
            }
            else SessionUserUtil.setLoggedUser(loggedUserDetail);     // set current user holder
        }
        catch (SQLException e){
            throw new UserCredentialsException("Database error while checking credentials", e);
        }
    }




    // credentials format validation

    private boolean validateUsername(UserCredentialsDto credDTO){
        return credDTO.getUname().matches("^[a-zA-Z0-9]+$");
    }

    private boolean validatePassword(UserCredentialsDto credDTO){
        return credDTO.getPswd().matches("^[\\S]{3,50}$");              // allow [\\S] any non-whitespace char  // ength between 3 and 50
    }

}
