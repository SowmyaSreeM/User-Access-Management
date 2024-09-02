package uam.uamm;

import java.sql.Connection;
import java.sql.DriverManager;

public class db {
    public static Connection connect() throws Exception {
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/project";
        String userName = "root";
        String password = "root";
        
        Class.forName(driver);
        Connection c = DriverManager.getConnection(url, userName, password);
        return c;
    }
}

