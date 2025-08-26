package edu.icet.cims.utill.Database;

import edu.icet.cims.utill.AlertPopupUtil;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.sql.*;

public class DbCheckUtil {

    // check if db exits
    public  static boolean isDbAvailable() throws IOException {
         if(!check()){
             AlertPopupUtil.alertMsg(Alert.AlertType.WARNING, "Database not available in the system, Do you want to create 'thogakade_cims' database");
              return false;
         }
         return true;
    }

    // check
    private static boolean check(){
        try {
            return checkDbExist();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // checkDbExist
    private static boolean checkDbExist() throws SQLException {
        String sql = "SHOW DATABASES LIKE 'thogakade_cims'";

        // To automatically close all resources (dbCon, stm, rs) when finished, using try()
        try(
            Connection dbCon = DriverManager.getConnection("jdbc:mysql://localhost/", "root", "1234");
            Statement stm = dbCon.createStatement();
            ResultSet rst = stm.executeQuery(sql)
        ){
            return rst.next();
        }
    }
}
