package edu.icet.cims.controller;

import edu.icet.cims.util.WindowManagerUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;

public class TrueFalseController {

    private String truePath;
    private String falsePath;

    TrueFalseController(){}

    TrueFalseController(String lbTrue, String lblFalse, String txtLbl, String truePath, String falsePath){
        btn_true.setText(lbTrue);
        btn_false.setText(lblFalse);
        txt.setText(txtLbl);
        this.truePath = truePath;
        this.falsePath = falsePath;
    }

    @FXML
    private Button btn_false;

    @FXML
    private Button btn_true;

    @FXML
    private Label txt;

    @FXML
    void btn_FalseAction(ActionEvent event) throws IOException {
        WindowManagerUtil.switchScene(event, falsePath);
    }

    @FXML
    void btn_TrueAction(ActionEvent event) throws IOException {
        WindowManagerUtil.switchScene(event, truePath);
    }

}


