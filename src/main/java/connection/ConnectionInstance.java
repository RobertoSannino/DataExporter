package connection;

import config.PropertiesLoader;
import util.QueryUtils;
import java.util.ArrayList;

public abstract class ConnectionInstance {

    String propertiesSuffix;
    QueryUtils queryUtils;
    private final String connectionName;

    String driver;
    String host;
    String port;
    String serviceName;
    String user;
    String pwd;
    int maxParallelCollections;


    public ConnectionInstance(String propertiesSuffix, String connectionName, String queryPath) {
        this.propertiesSuffix = propertiesSuffix;
        this.connectionName = connectionName;
        this.queryUtils = new QueryUtils(queryPath);
        loadProperties();
    }

    public ConnectionInstance(String connectionName, String queryPath, String driver, String host, String port, String serviceName, String user, String pwd, int mpc) {
        this.connectionName = connectionName;
        this.queryUtils = new QueryUtils(queryPath);
        this.driver = driver;
        this.host = host;
        this.port = port;
        this.serviceName = serviceName;
        this.user = user;
        this.pwd = pwd;
        this.maxParallelCollections = mpc;
    }

    private void loadProperties() {
        this.driver =  PropertiesLoader.getInstance().getProperty("db.driver",propertiesSuffix);
        this.host =  PropertiesLoader.getInstance().getProperty("db.host",propertiesSuffix);
        this.port =  PropertiesLoader.getInstance().getProperty("db.port",propertiesSuffix);
        this.serviceName =  PropertiesLoader.getInstance().getProperty("db.serviceName",propertiesSuffix);
        this.user =  PropertiesLoader.getInstance().getProperty("db.user",propertiesSuffix);
        this.pwd =  PropertiesLoader.getInstance().getProperty("db.pwd",propertiesSuffix);
        this.maxParallelCollections =  Integer.parseInt(PropertiesLoader.getInstance().getProperty("db.maxParallelConnections",propertiesSuffix));
    }

    public String getConnectionName() {
        return connectionName;
    }

    public String getConnectionString() {
        return "jdbc:" + this.driver + getHostSeparator() + this.host + ":" + this.port + getServiceSeparator() + this.serviceName;
    }

    String getHostSeparator() {
        return "://";
    }

    String getServiceSeparator() {
        return "/";
    }

    public String getRangedQuery(int[] range) { return null; }

    public String getUser() { return this.user; }

    public String getPwd() { return this.pwd; }

    public int getMaxParallelConnection() { return this.maxParallelCollections; }

    public String getQuery() {
        return this.queryUtils.getQuery();
    }

    public ArrayList<String> getProjectionAttributes() {
        return this.queryUtils.getProjectionAttributes();
    }

    public String getCountQuery() { return this.queryUtils.getCountQuery(); }

}
