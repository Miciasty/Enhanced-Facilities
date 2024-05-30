package nsk.enhanced.Methods;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private String url = "mysql://localhost:3306/enhanced";
    private String user = "root";
    private String password = "root";

    private String name;

    public String getUrl() { return url; }
    private String getUser() { return user; }
    private String getPassword() { return password; }
    public String getName() { return name; }

    public Database(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.name = "enhanced-facilities";

        createDatabase();
    }

    public Database(String url, String user, String password, String name) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.name = name;

        createDatabase();
    }

    private void createDatabase() {
        try {
            Connection connection = DriverManager.getConnection(this.url, this.user, this.password);
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS" + this.name);
            statement.executeUpdate("USE " + this.name);

            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + "factions" + " (" +
                        "`id` INT NOT NULL AUTO_INCREMENT," +
                        "`name` VARCHAR(255) NOT NULL," +
                        "`players` TEXT NOT NULL," +
                        "`buildings` TEXT NOT NULL," +
                        "PRIMARY KEY (`id`));");

            statement.close();
            connection.close();

        } catch (SQLException e) {
            PluginInstance.getInstance().consoleError(e);
        }
    }
}
