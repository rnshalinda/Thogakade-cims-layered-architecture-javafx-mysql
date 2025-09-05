package edu.icet.cims.util.configDb;

import edu.icet.cims.db.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ManageDbUtil {

    // expected db structure
    private static Map<String, List<String>> requiredTables = Map.of(
            "customer", List.of("Customer_Id", "Title", "Name", "Dob", "Address", "City", "Province", "Postal_Code"),
            "item", List.of("Item_Code", "Item_Description", "Pack_Size", "Unit_Price", "Qty"),
            "user_credentials", List.of("user_id", "username", "password", "name")
    );

    // get tables names
    public static ArrayList<String> getRequiredTableNames() {
        return new ArrayList<>(requiredTables.keySet());
    }

    // get tables column names
    public static ArrayList<String> getRequiredColumnNames(String tblName) {
        return new ArrayList<>(requiredTables.get(tblName));
    }


    // create tables SQL queries
    private static String customer =  "CREATE TABLE customer(\n" +
            "\tCustomer_Id VARCHAR(10),\n" +
            "\tTitle VARCHAR(5),\n" +
            "\tName VARCHAR(30) NOT NULL,\n" +
            "\tDob date,\n" +
            "\tAddress TEXT,\n" +
            "\tCity VARCHAR(30),\n" +
            "\tProvince VARCHAR(30),\n" +
            "\tPostal_Code VARCHAR(20),\n" +
            "\tprimary key(Customer_Id)\n" +
            "\t);";

    private static String item = "CREATE TABLE item(\n" +
            "\tItem_Code VARCHAR(10),\n" +
            "\tItem_Description TEXT,\n" +
            "\tPack_Size VARCHAR(10),\n" +
            "\tUnit_Price DECIMAL(10,2),\n" +
            "\tQty INT,\n" +
            "\tPRIMARY KEY(Item_Code)\n" +
            "\t);";

    private static String user_credentials = "CREATE TABLE user_credentials(\n" +
            "\tuser_id VARCHAR(10),\n" +
            "\tusername VARCHAR(50) UNIQUE,\n" +
            "\tpassword VARCHAR(50),\n" +
            "\tname VARCHAR(30),\n" +
            "\tPRIMARY KEY(user_id)\n" +
            "\t);";

    // get item table string
    public static String getItemTbl() {
        return item;
    }
    // get customer table string
    public static String getCustomerTbl() {
        return customer;
    }
    // get user-credentials table string
    public static String getUser_credentialsTbl() {
        return user_credentials;
    }

    // create database
    public static boolean createDB(String dbName){

        String sql = "CREATE DATABASE "+dbName+";";

        try {
            DbConnection.getInstance().getConnection().createStatement().executeUpdate(sql);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    // create tables - inner method
    public static boolean createTables(ArrayList<String> dbStrcture){

        // remove the first element (dbName) from the list, because we only need tables
//        dbStrcture.removeFirst();

        // execute for each element ("Table") in ArrayList
        for (String element : dbStrcture){

            String sql = (element.equalsIgnoreCase("item")) ? getItemTbl() : (element.equalsIgnoreCase("customer")) ? getCustomerTbl() : (element.equalsIgnoreCase("user_credentials")) ? getUser_credentialsTbl() : null;

            if(sql != null && !sql.isBlank()){

                String trimedSql = sql.replaceAll("[\n\t]","");

                try {
                    Statement stm = DbConnection.getInstance().getConnection().createStatement();

                    // use db, go into specifies db
                    DbConnection.useDb( dbStrcture.getFirst() );

                    stm.executeUpdate(trimedSql);

                } catch (SQLException e) {
//                    throw new RuntimeException(e);
                    return false;
                }
            }
        }
        return true;
    }


    // delete database
    public static boolean dropDb(String dbName){

        String sql = "DROP DATABASE "+dbName+";";
        try {
            DbConnection.getInstance().getConnection().createStatement().executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // check if delete successful
        if(!DbCheckUtil.isDbAvailable(dbName)) return true;
        return false;
    }

}
