package Database;

import Models.Exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * A connection to a PostgreSQL server
 */
public class PostGreSQLConnection {
    private final String _url ;
    private final String _username ;
    private final String _password ;

    /**
     * Constructs a new PostGresSQLConnection used for connecting to a postgreSQL database
     * @param properties Properties used for connecting to the database.
     *                   Must contain:
     *                   - a url property which has the url for the db in jdbc connection string format such (for example jdbc:postgresql://localhost/postgres)
     *                   - a username property which has the username of the postgreSQL user to access the db as
     *                   - a password property which has the password of the postgreSQL user
     */
    public PostGreSQLConnection(Properties properties){
        _url = properties.getProperty("url");
        _username = properties.getProperty("username");
        _password = properties.getProperty("password");
    }

    /**
     * Gets a connection to the PostgreSQL database
     * @return a Connection to the database
     */
    public Connection getPostgreSqlConnection() throws DatabaseException {
        try {
            return DriverManager.getConnection(_url, _username, _password);
        } catch (SQLException e) {
            throw new DatabaseException("Unable to get connection to the database\n" + e.getMessage());
        }
    }
}
