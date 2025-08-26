package edu.icet.cims.service;

import edu.icet.cims.model.dto.ActiveUserDTO;
import edu.icet.cims.model.dto.UserCredentialsDTO;
import edu.icet.cims.model.entity.UserCredentialsEntity;
import edu.icet.cims.repository.RepositoryCredentials;

public class ServiceCredentials {
    
    private RepositoryCredentials repository = new RepositoryCredentials();
    
    public ActiveUserDTO credentialsValidate(UserCredentialsDTO credDTO){
        if(validate(credDTO)){
            return repository.credentialsCheck(new UserCredentialsEntity(credDTO.getUname(),credDTO.getPswd()));
        }
        return null;
    }

    private boolean validate(UserCredentialsDTO credDTO){
        if (credDTO.getUname().matches("^[a-zA-Z0-9]+$") && credDTO.getPswd().matches("^[a-zA-Z0-9]{3,50}$")){
            return true;
        }
        return false;
    }

}
