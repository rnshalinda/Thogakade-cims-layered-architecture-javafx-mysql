package edu.icet.cims.repository;

import edu.icet.cims.db.DbConnection;
import edu.icet.cims.model.entity.ItemEntity;

import java.sql.*;
import java.util.ArrayList;

public class RepositoryItem {

    // Search item in DB
    public ArrayList<ItemEntity> getItemEntityList() throws SQLException {

        ArrayList<ItemEntity> entityList = new ArrayList<>();

        String sql = "SELECT * FROM item;";

        Statement stm = DbConnection.getInstance().getConnection().createStatement();
        ResultSet rst = stm.executeQuery(sql);

        while (rst.next()){
            entityList.add(
                new ItemEntity(
                      rst.getString("Item_Code"),
                      rst.getString("Item_Description"),
                      rst.getString("Pack_Size"),
                      rst.getDouble("Unit_Price"),
                      rst.getInt("Qty")
                )
            );
        }
        return entityList;
    }



    // delete item from DB
    public boolean deleteItem(String itemCode) throws SQLException {

        String sql = "DELETE FROM item WHERE Item_Code=?;";

        PreparedStatement stm = DbConnection.getInstance().getConnection().prepareStatement(sql);
        stm.setObject(1,itemCode);

        return stm.executeUpdate() > 0;
    }



    // Add new item record to DB
    public boolean addNewItem(ItemEntity entity) throws SQLException {

        String sql = "INSERT INTO item (Item_Code, Item_Description, Pack_Size, Unit_Price, Qty) VALUES (?,?,?,?,?);";

        PreparedStatement stm = DbConnection.getInstance().getConnection().prepareStatement(sql);
        stm.setObject(1, entity.getItemCode());
        stm.setObject(2, entity.getDescription());
        stm.setObject(3, entity.getPackSize());
        stm.setObject(4, entity.getUnitPrice());
        stm.setObject(5, entity.getQtyOnHand());

        return stm.executeUpdate() > 0;     // if add successful stm returns 1 = true
    }



    // Update existing values in DB
    public boolean updateItem(ItemEntity entity) throws SQLException {

        String sql = "UPDATE item SET " +
                "Item_Description=?, " +
                "Pack_Size=?, "+
                "Unit_Price=?, " +
                "Qty=? " +
                "WHERE Item_Code=?;";

        PreparedStatement stm = DbConnection.getInstance().getConnection().prepareStatement(sql);
        stm.setObject(1, entity.getDescription());
        stm.setObject(2, entity.getPackSize());
        stm.setObject(3, entity.getUnitPrice());
        stm.setObject(4, entity.getQtyOnHand());
        stm.setObject(5, entity.getItemCode());

        return stm.executeUpdate() > 0;     // if update successful return 1 = true
    }



    // Check if item alredy sxist in DB
    public boolean itemExistCheck(String itemCode) throws SQLException {

        String sql = "SELECT EXISTS(" +
                "SELECT 1 " +
                "FROM item " +
                "WHERE BINARY Item_Code=? " +
                ");";

        PreparedStatement stm = DbConnection.getInstance().getConnection().prepareStatement(sql);
        stm.setObject(1, itemCode);
        ResultSet rst =  stm.executeQuery();

        if(rst.next()){
            return rst.getInt(1) > 0;   // EXISTS = 1, so returns true //  works with exist only function
        }
        return false;
    }
}
