package edu.icet.cims.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Singleton design pattern DB connection
public class DbConnection {

    private static DbConnection db;
    private Connection connection;

    // class instance - constructor
    private DbConnection() throws SQLException {

        connection = DriverManager.getConnection(
                // url - general connection,
                // connection doesn't contain end-point (dbname), why? to make the connection dynamic and accept queries that are not specific for a db
                "jdbc:mysql://"+ DbConfig.getDbConfigData().getHost()+":"+ DbConfig.getDbConfigData().getPort()+"/",
                // user
                DbConfig.getDbConfigData().getUser(),
                // pswd
                DbConfig.getDbConfigData().getPswd()
        );
    }

    // singleton check
    public static DbConnection getInstance() throws SQLException {
        if(db == null){
            db = new DbConnection();
        }
        return db;
    }


    // get db connection
    public Connection getConnection(){
        return connection;
    }


    // force override db connection
    public static void overideDbConection() throws SQLException {
        db = new DbConnection();
    }

    // use database - go into to specific db
    public static void useDb(String dbName){

        String sql = "USE "+dbName+";";
        try {
            DbConnection.getInstance().getConnection().createStatement().executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
