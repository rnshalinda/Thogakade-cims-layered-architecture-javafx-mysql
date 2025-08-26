package edu.icet.cims.repository;

import edu.icet.cims.db.DBConnection;
import edu.icet.cims.model.dto.ActiveUserDTO;
import edu.icet.cims.model.entity.UserCredentialsEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RepositoryCredentials {

    public ActiveUserDTO credentialsCheck(UserCredentialsEntity credEntity){

//        This function only checks if it exists
//        String sql = " SELECT EXISTS(" +
//                " SELECT 1 " +
//                " FROM user_credentials " +
//                " WHERE BINARY username= ? AND BINARY password= ? " +
//                " )";

//        BINARY - enforces the case sensitivity here
//        If it returns at least one row → EXISTS = 1
//        If it returns no rows → EXISTS = 0


//        This function if it exists and matches uernmae, pswd, returns  user_id and name from that table
        String sql = "SELECT user_id, name FROM user_credentials WHERE username=? AND password=?";

        try {
            PreparedStatement stm = DBConnection.getInstance().getConnection().prepareStatement(sql);
            stm.setObject(1, credEntity.getUsername());
            stm.setObject(2, credEntity.getPassword());
            ResultSet rst = stm.executeQuery();

            if (rst.next()) {
//                return rst.getInt(1) > 0;   // EXISTS = 1, so returns true //  works with exist only function

                return (new ActiveUserDTO(rst.getString("user_id"), rst.getString("name")));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
