package connection;

import config.PropertiesLoader;
import util.QueryUtils;
import java.util.ArrayList;

public abstract class ConnectionInstance {

    String propertiesSuffix;
    QueryUtils queryUtils;
    private final String connectionName;


    public ConnectionInstance(String propertiesSuffix, String connectionName, String queryPath) {
        this.propertiesSuffix = propertiesSuffix;
        this.connectionName = connectionName;
        this.queryUtils = new QueryUtils(queryPath);
    }

    public String getConnectionName() {
        return connectionName;
    }

    public String getConnectionString() {
        return "jdbc:" + PropertiesLoader.getInstance().getProperty("db.driver",propertiesSuffix) +
                getHostSeparator() + PropertiesLoader.getInstance().getProperty("db.host",propertiesSuffix) +
                ":" + PropertiesLoader.getInstance().getProperty("db.port",propertiesSuffix) +
                getServiceSeparator() + PropertiesLoader.getInstance().getProperty("db.serviceName",propertiesSuffix);
    }

    String getHostSeparator() {
        return "://";
    }

    String getServiceSeparator() {
        return "/";
    }

    public String getRangedQuery(int[] range) { return null; }

    public String getUsername() { return PropertiesLoader.getInstance().getProperty("db.user",propertiesSuffix); }

    public String getPwd() { return PropertiesLoader.getInstance().getProperty("db.pwd",propertiesSuffix); }

    public int getMaxParallelConnection() { return Integer.parseInt(PropertiesLoader.getInstance().getProperty("db.maxParallelConnections",propertiesSuffix)); }

    public String getQuery() {
        return this.queryUtils.getQuery();
    }

    public ArrayList<String> getProjectionAttributes() {
        return this.queryUtils.getProjectionAttributes();
    }

    public String getCountQuery() { return this.queryUtils.getCountQuery(); }

}
