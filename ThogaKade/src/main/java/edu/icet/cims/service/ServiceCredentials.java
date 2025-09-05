package edu.icet.cims.service;

import edu.icet.cims.model.dto.ActiveUserDTO;
import edu.icet.cims.model.dto.UserCredentialsDTO;
import edu.icet.cims.model.entity.UserCredentialsEntity;
import edu.icet.cims.repository.RepositoryCredentials;
import edu.icet.cims.util.AlertPopupUtil;
import javafx.scene.control.Alert;

public class ServiceCredentials {
    
    private RepositoryCredentials repository = new RepositoryCredentials();
    
    public ActiveUserDTO credentialsValidate(UserCredentialsDTO credDTO){
        if(validate(credDTO)){
            return repository.credentialsCheck(new UserCredentialsEntity(credDTO.getUname(),credDTO.getPswd()));
        }
        return null;
    }


    // credentials format validation
    private boolean validate(UserCredentialsDTO credDTO){
        if (!credDTO.getUname().matches("^[a-zA-Z0-9]+$")){
            AlertPopupUtil.alertMsg(Alert.AlertType.WARNING, "Username should only contain alphanumerics");
            return false;
        }
        if(!credDTO.getPswd().matches("^[a-zA-Z0-9]{3,50}$")){
            AlertPopupUtil.alertMsg(Alert.AlertType.WARNING, "Password must be at least 3 characters long");
            return false;
        }
        return true;
    }

}
