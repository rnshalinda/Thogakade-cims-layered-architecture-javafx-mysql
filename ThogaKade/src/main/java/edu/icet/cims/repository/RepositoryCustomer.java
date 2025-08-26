package edu.icet.cims.repository;

import edu.icet.cims.db.DBConnection;
import edu.icet.cims.model.entity.CustomerEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

public class RepositoryCustomer {

    // get all customer details
    public ArrayList<CustomerEntity> getCustomerEntityList() throws SQLException {

        String sql = "SELECT * FROM customer";
        Statement stm = DBConnection.getInstance().getConnection().createStatement();
        ResultSet rst = stm.executeQuery(sql);

        ArrayList<CustomerEntity> entityList =  new ArrayList<>();

        while (rst.next()){
            entityList.add( new CustomerEntity(
                    rst.getString("Customer_Id"),
                    rst.getString("Title"),
                    rst.getString("Name"),
                    LocalDate.parse(rst.getString("Dob")),      // convert to LocalDate format  // match customer entity format
                    rst.getString("Address"),
                    rst.getString("City"),
                    rst.getString("Province"),
                    rst.getString("Postal_Code")
                    ));
        }
        return entityList;
    }

    // delete customer
    public boolean deleteCustomer(String custID) throws SQLException {
        String sql = "DELETE FROM customer WHERE Customer_Id=?";

        PreparedStatement stm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        stm.setObject(1,custID);
        return stm.executeUpdate() > 0;
    }
}
