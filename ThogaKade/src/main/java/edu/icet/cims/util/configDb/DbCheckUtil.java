package edu.icet.cims.util.configDb;

import edu.icet.cims.db.DbConnection;
import edu.icet.cims.db.dbConfig;

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


    // function cheeks to see,
    // if found db with same name in the system
    // check if exiting tables structure in the found db matches with the expected
//    public static boolean DoesDbStructureMatches(ArrayList<String> existingDbTables, ArrayList<String> requiredTables){
//
//        if(matchTblName(existingDbTables,requiredTables)){
//            return true;
//        }


//
//        boolean customerFlag = false, itemFlag = false, userFlag = false;
//
//        ArrayList<String> requiredTables = new ArrayList<>();
//        requiredTables.addAll("customer", "item", "user_credentials");
//
//        // Loop over DB tables
//        for ( String dbTbl : db) {
//            // Remove matched element from required list
//            requiredTables.remove(dbTbl);
//
//            // If removed all required, no need to continue
//            if (requiredTables.isEmpty()) {
//                return;
//            }
//        }
//
//        requiredTables.addAll("customer", "item", "user_credentials");
//
//        for( String tblName : requiredTables){
//
//            String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
//                    "WHERE TABLE_SCHEMA = '"+dbConfig.getDbConfigData().getDbName()+"'" +
//                    "  AND TABLE_NAME = '"+tblName+"';";
//
//            DbConnection.useDb(dbConfig.getDbConfigData().getDbName());
//            ResultSet rst;
//            try {
//                rst = DbConnection.getInstance().getConnection().createStatement().executeQuery(sql);
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//
//            ArrayList<String> COLUMN_NAME = new ArrayList<>();
//
//            if(rst != null) {
//                try {
//                    while (rst.next()) {
//                        COLUMN_NAME.add(rst.getString("COLUMN_NAME"));
//                    }
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            else return false;
//
//            if(tblName.equals("customer")){
//                matchColumns();
//            }
//            if(tblName.equals("item")){
//
//            }
//            if(tblName.equals("user_credentials")){
//
//            }
//
//
//        }
//        return false;
//
//    }


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
        return foundCount == requiredTableNames.size();
    }


    public static boolean matchTblColumn(ArrayList<String> existingDbTableNames, ArrayList<String> requiredTableNames){


        for (String exiTbl : existingDbTableNames){

            String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_SCHEMA = '"+ dbConfig.getDbConfigData().getDbName()+"'" +
                    "  AND TABLE_NAME = '"+exiTbl+"';";

            DbConnection.useDb(dbConfig.getDbConfigData().getDbName());

            ResultSet rst;
            try {
                rst = DbConnection.getInstance().getConnection().createStatement().executeQuery(sql);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            // existing db column names
            ArrayList<String> db_COLUMN_NAME = new ArrayList<>();      // holds currently checking exiTbl column names

            if(rst != null) {
                try {
                    while (rst.next()) {
                        db_COLUMN_NAME.add(rst.getString("COLUMN_NAME"));      // "COLUMN_NAME" is the table in the sql result
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            if(db_COLUMN_NAME != null){

                for (String reqTbl : requiredTableNames){

                    if(exiTbl.equals(reqTbl)){
                        for(String reqCol : ManageDbUtil.getRequiredColumnNames(reqTbl)){
                            for(String colName : db_COLUMN_NAME){
                                if(!reqCol.equals(colName)) { return false;}
                            }
                        }
                    }
                }
            }

        }
        return true;
    }




}
