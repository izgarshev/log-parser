package parser.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbConnection {
    private static final Logger logger = LoggerFactory.getLogger(DbConnection.class);
    private static DbConnection INSTANCE;

    private Connection connection;

    public static DbConnection getInstance() {
        if (INSTANCE == null) INSTANCE = new DbConnection();
        return INSTANCE;
    }


    private DbConnection() {
        try {
            Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream("/application.properties"));
            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            logger.error("no connect to database", e);
        } catch (IOException e) {
            logger.error("can't load application.properties", e);
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
