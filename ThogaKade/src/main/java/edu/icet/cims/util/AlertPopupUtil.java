package edu.icet.cims.util;

import javafx.scene.control.Alert;

public class AlertPopupUtil {

    // Message dialog box alert
    public static void alertMsg(Alert.AlertType type, String msg){
        Alert alert = new Alert(type);
        alert.setTitle("Alert");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}


// Extra point -  use of popup window

//JOptionPane.showMessageDialog(getParent(),"Incorrect Password or Username");  // java swing way
// has to extend class  'Components' // but is not okay bcoz Joptionpane is Java.swing

// mixing swing and JavaFx not good mamory issues
// instead used below JavaFx Alert
// can even customize alert types: INFORMATION, WARNING, ERROR, CONFIRMATION, NONE.

//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("ERROR");
//            alert.setHeaderText(null);
//            alert.setContentText("Incorrect Password or Username");
//            alert.showAndWait();

//            new Alert(Alert.AlertType.ERROR, "Incorrect Password or Username",).showAndWait();    // short way