package com.mycompany.bookloanandreturn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/bookloan_and_return";
        String username = "root";
        String password = "";

        return DriverManager.getConnection(url, username, password);
     
    }
}

