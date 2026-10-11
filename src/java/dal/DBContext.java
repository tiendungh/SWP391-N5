
package dal;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBContext {
    protected Connection c;

    public DBContext() {
        try {
            String url = "jdbc:sqlserver://localhost:1433;databaseName=SWP391;encrypt=true;trustServerCertificate=true";
            String username = "sa";
            String password = "123";
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            c = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            if (c != null && !c.isClosed()) c.close();
        } catch (Exception e) {
            System.err.println("Close connection failed: " + e.getMessage());
        }
    }
}
