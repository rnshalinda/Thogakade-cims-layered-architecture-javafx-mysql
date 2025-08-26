package edu.icet.cims.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Singleton design pattern DB connection
public class DBConnection {

    private static DBConnection db;
    private Connection connection;

    private DBConnection() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost/thogakade_cims","root","1234");
    }

    public static DBConnection getInstance() throws SQLException {
        if(db == null){
            db = new DBConnection();
        }
        return db;
    }

    public Connection getConnection(){
        return connection;
    }
}
