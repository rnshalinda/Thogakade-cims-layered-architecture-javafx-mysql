package edu.icet.cims.util;

import edu.icet.cims.model.dto.ActiveUserDto;

// Active user
public class SessionUserUtil {
    private static ActiveUserDto loggedUser;

    // set logged user
    public static void setLoggedUser(ActiveUserDto loggedUser) {
        SessionUserUtil.loggedUser = loggedUser;
    }
    // set current user
    public static ActiveUserDto getLoggedUser() {
        return SessionUserUtil.loggedUser;
    }

    // Clear logged-in user
    public static void clearSessionUser(){
        SessionUserUtil.loggedUser = null;
    }

}
