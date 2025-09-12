package edu.icet.cims.util.configDb;

import edu.icet.cims.db.DbConnection;
import edu.icet.cims.db.DbConfig;

import java.sql.*;
import java.util.ArrayList;


public class DbCheckUtil {

    // check if db name already exits, return true/false
    public static boolean isDbAvailable(String dbName) {

        String sql = "SHOW DATABASES LIKE '"+dbName+"';";       // returns a table of dbName   // that's why below rst.next() works, since its a table, it has rows

        // To automatically close all resources (dbCon, stm, rs) after finished, used try()
        try( ResultSet rst  = DbConnection.getInstance().getConnection().createStatement().executeQuery(sql) ){     // get db connection and statement

            return rst.next();              // return true if db exist

        } catch (SQLException e) {
            return false;
        }
    }

    // this returns a list containing table names of db
    public static ArrayList<String> checkDbTblExist(String dbName){

        DbConnection.useDb(dbName);         // go into specified Db

        String sql = "SHOW TABLES;";        // returns a table of table names in db
        try( ResultSet rst = DbConnection.getInstance().getConnection().createStatement().executeQuery(sql) ){

            // array of tbl contains -> (customer. item, user_credentials)
            ArrayList<String> dbTables = new ArrayList<>();

            // add table names to array
            while (rst.next()) {
                dbTables.add(rst.getString("Tables_in_" + dbName));         // add to array
            }
            return dbTables;                // return array

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // match table names
    public static boolean matchTblName(ArrayList<String> existingDbTableNames, ArrayList<String> requiredTableNames){


        if (requiredTableNames == null || requiredTableNames.isEmpty()) {
            return false;
        }

        int foundCount = 0;

        for (String reqTblName : requiredTableNames) {
            for (String exiTblName : existingDbTableNames) {
                if (exiTblName.equals(reqTblName)) {
                    foundCount++;
                    break;          // stop checking once found
                }
            }
        }
        return foundCount == requiredTableNames.size();     // count should match with required tbl count
    }


    public static boolean matchTblColumn(ArrayList<String> existingDbTableNames){

        for (String exiTbl : existingDbTableNames){

            System.out.println("\nTable : "+exiTbl);

            String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_SCHEMA = '"+ DbConfig.getDbConfigData().getDbName()+"'" +
                    "  AND TABLE_NAME = '"+exiTbl+"';";

            ResultSet rst;
            try {
                rst = DbConnection.getInstance().getConnection().createStatement().executeQuery(sql);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            // existing db column names
            ArrayList<String> exiTbl_COLUMN_NAME = new ArrayList<>();      // holds currently checking 'exiTbl' table col names

            try {
                while (rst.next()) {
                    exiTbl_COLUMN_NAME.add(rst.getString("COLUMN_NAME"));      // "COLUMN_NAME" is the table in the sql result
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


            if(exiTbl_COLUMN_NAME == null){
                System.out.println("exiTbl_COLUMN_NAME : null");
                return false;
            }
            else {
                boolean flag = false;
                for(String reqCol : ManageDbUtil.getRequiredColumnNames(exiTbl)){

                    flag = false;
                    for(String exiCol : exiTbl_COLUMN_NAME){
                        if(reqCol.equalsIgnoreCase(exiCol)){
                            flag = true;
                            break;
                        }
                    }

                    System.out.println(reqCol+" : "+ flag);
                    if (!flag){
                        return false;
                    }
                }

            }
        }
        return true;
    }

}
