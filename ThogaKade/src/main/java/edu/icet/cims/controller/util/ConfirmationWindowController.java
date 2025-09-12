package edu.icet.cims.controller.util;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ConfirmationWindowController implements Initializable {


    private boolean result = false;     // default

    @FXML
    private Label lbl_heading;

    @FXML
    private Button btn_no;

    @FXML
    private Button btn_yes;

    @FXML
    private StackPane stackpane;


    @FXML
    void btn_noAction(ActionEvent event) {
        result = false;                                         // save response
        ((Stage) btn_no.getScene().getWindow()).close();        // close window
    }

    @FXML
    void btn_yesAction(ActionEvent event) {
        result = true;                                          // save response
        ((Stage) btn_yes.getScene().getWindow()).close();       // close window
    }

    // get saved Yes/No response of buttons
    public boolean getResult() {
        return result;
    }


    // display String in stack-pane [ can display any type image, String.. by overloading this methode
    // for now below method accepts ArrayList only
    public void setStackpane(ArrayList<String> dbList){

        // create text area to push on top of stack-pane
        TextArea textArea = new TextArea();
        textArea.setEditable(false);            // make it read-only
        textArea.setWrapText(true);             // wrap long lines

        for(int i = 0; i < dbList.toArray().length; i++){

            if(i == 0) textArea.appendText("db - "+(String) dbList.toArray()[i++]);     // show first element - dbName

            // if dbList array has more than 1 element continue
            if(dbList.toArray().length > 1) {
                textArea.appendText("\n\t|-->" + (String) dbList.toArray()[i] + " (table)");          // append text line
            }
        }
        stackpane.getChildren().setAll(textArea);
    }

    // window heading
    public void setHeading(String str){
        lbl_heading.setText(str);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setStackpane(new ArrayList<String>());
        setHeading("User Confirmation");
    }
}

