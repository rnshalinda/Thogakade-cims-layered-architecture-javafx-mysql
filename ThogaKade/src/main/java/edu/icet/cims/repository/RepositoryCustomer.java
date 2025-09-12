package edu.icet.cims.repository;

import edu.icet.cims.db.DbConnection;
import edu.icet.cims.model.dto.CustomerDto;
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

        String sql = "SELECT * FROM customer;";
        Statement stm = DbConnection.getInstance().getConnection().createStatement();
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
        String sql = "DELETE FROM customer WHERE Customer_Id=?;";

        PreparedStatement stm = DbConnection.getInstance().getConnection().prepareStatement(sql);
        stm.setObject(1,custID);
        return stm.executeUpdate() > 0;
    }


    public boolean addNewCustomer(CustomerEntity customerEntity) throws SQLException {

        String sql = "INSERT INTO customer (Customer_Id, Title, Name, Dob, Address, City, Province, Postal_Code) VALUES(?, ?, ?, ?, ?, ?, ?, ?);";

        PreparedStatement stm = DbConnection.getInstance().getConnection().prepareStatement(sql);
        stm.setObject(1, customerEntity.getCustomerId());
        stm.setObject(2, customerEntity.getTitle());
        stm.setObject(3, customerEntity.getName());
        stm.setObject(4, customerEntity.getDob());
        stm.setObject(5, customerEntity.getAddress());
        stm.setObject(6, customerEntity.getCity());
        stm.setObject(7, customerEntity.getProvince());
        stm.setObject(8, customerEntity.getPostalCode());

        return  stm.executeUpdate() > 0;       // if add successful stm returns 1 = true
    }


    // update customer
    public boolean updateCustomer(CustomerEntity customerEntity) throws SQLException {

        String sql = "UPDATE customer SET "+
                "Title=?, "+
                "Name=?, "+
                "Dob=?, "+
                "Address=?, "+
                "City=?, "+
                "Province=?, "+
                "Postal_Code=? "+
                "WHERE Customer_Id=?;";

        PreparedStatement stm = DbConnection.getInstance().getConnection().prepareStatement(sql);
        stm.setObject(1, customerEntity.getTitle());
        stm.setObject(2, customerEntity.getName());
        stm.setObject(3, customerEntity.getDob());
        stm.setObject(4, customerEntity.getAddress());
        stm.setObject(5, customerEntity.getCity());
        stm.setObject(6, customerEntity.getProvince());
        stm.setObject(7, customerEntity.getPostalCode());
        stm.setObject(8, customerEntity.getCustomerId());

        return stm.executeUpdate() > 0;     // if update successful return 1 = true
    }


    // check if customer exist in db
    public boolean customerExistCheck(String customerId) throws SQLException {

        String sql = "SELECT EXISTS(" +
                "SELECT 1 " +
                "FROM customer " +
                "WHERE BINARY Customer_Id=? " +
                ");";

        PreparedStatement stm = DbConnection.getInstance().getConnection().prepareStatement(sql);
        stm.setObject(1, customerId);
        ResultSet rst =  stm.executeQuery();

        if(rst.next()){
            return rst.getInt(1) > 0;   // EXISTS = 1, so returns true //  works with exist only function
        }
        return false;
    }
}
