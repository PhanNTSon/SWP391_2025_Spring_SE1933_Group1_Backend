import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DropSchema {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/steam_clone_db";
        String user = "sa";
        String password = "sa";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            System.out.println("Dropping public schema...");
            stmt.execute("DROP SCHEMA IF EXISTS public CASCADE;");
            System.out.println("Creating public schema...");
            stmt.execute("CREATE SCHEMA public;");
            System.out.println("Schema reset successful!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
