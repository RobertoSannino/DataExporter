package db;

import connection.ConnectionInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import java.sql.*;

public class DbManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbManager.class);
    private Connection connection;
    private ConnectionInstance connectionProperties;

    public DbManager(ConnectionInstance connectionProperties) {
        this.connectionProperties = connectionProperties;
    }

    private Connection getConnection() throws NamingException, SQLException {
        try {
            if (this.connection == null || this.connection.isClosed()) {
                this.connection = DriverManager.getConnection(this.connectionProperties.getConnectionString(), this.connectionProperties.getUsername(), this.connectionProperties.getPwd());
            }
        } catch (Exception e) {
            LOGGER.error("DB CONNECTION EXCEPTION FOR: "+ connectionProperties.getConnectionName());
            if(e.getMessage().contains("ORA-12519"))
                LOGGER.error("TOO MANY OPEN CONNECTIONS, CONSIDER CHANGING DB CONFIGURATION OR TUNING THE db.maxParallelConnections.* PROPERTY ACCORDINGLY");
            else
                e.printStackTrace();
        }
        return connection;
    }

    public void releaseConnection() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeQuery(String query)  {
        PreparedStatement stm;
        ResultSet results = null;
        try {
            stm = getConnection().prepareStatement(query);
            results = stm.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NamingException e) {
            e.printStackTrace();
        }

        return results;
    }

    public int countTable(String query) {
        int count = -1;
        PreparedStatement stm;
        ResultSet results;
        try {
            stm = getConnection().prepareStatement(query);
            results = stm.executeQuery();
            if(results.next())
                count = results.getInt(1);
            results.close();
            stm.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NamingException e) {
            e.printStackTrace();
        } finally {
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return count;
    }

}
