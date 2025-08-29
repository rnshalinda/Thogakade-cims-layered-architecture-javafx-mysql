package edu.icet.cims.util;

import edu.icet.cims.model.dto.ActiveUserDTO;

// Active user
public class SessionUserUtil {
    private static ActiveUserDTO loggedUser;

    // set logged user
    public static void setLoggedUser(ActiveUserDTO loggedUser) {
        SessionUserUtil.loggedUser = loggedUser;
    }
    // set current user
    public static ActiveUserDTO getLoggedUser() {
        return SessionUserUtil.loggedUser;
    }

    // Clear logged-in user
    public static void clearSessionUser(){
        SessionUserUtil.loggedUser = null;
    }

}
