package edu.icet.cims.repository;

import edu.icet.cims.db.DbConfig;
import edu.icet.cims.db.DbConnection;
import edu.icet.cims.model.dto.ActiveUserDto;
import edu.icet.cims.model.entity.UserCredentialsEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RepositoryCredentials {

    public ActiveUserDto credentialsCheck(UserCredentialsEntity credEntity) throws SQLException {

//     This function if it exists and matches uernmae, pswd, returns  user_id and name from that table
        String sql = "SELECT user_id, name FROM credentials WHERE username=? AND password=?";

            DbConnection.useDb(DbConfig.getDbConfigData().getDbName());                                    // use db
            PreparedStatement stm = DbConnection.getInstance().getConnection().prepareStatement(sql);      // send query

            stm.setObject(1, credEntity.getUsername());
            stm.setObject(2, credEntity.getPassword());
            ResultSet rst = stm.executeQuery();

            if (rst.next()) {
                return (new ActiveUserDto(rst.getString("user_id"), rst.getString("name")));
            }

        return null;
    }
}





// another way
//        This function only checks if it exists
//        String sql = " SELECT EXISTS(" +
//                " SELECT 1 " +
//                " FROM user_credentials " +
//                " WHERE BINARY username= ? AND BINARY password= ? " +
//                " )";

//        BINARY - enforces the case sensitivity here
//        If it returns at least one row → EXISTS = 1
//        If it returns no rows → EXISTS = 0

//            if (rst.next()) {
//                return rst.getInt(1) > 0;   // EXISTS = 1, so returns true //  works with exist only function
//        }