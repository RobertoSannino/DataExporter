package connection;

import config.Env;
import config.PropertiesLoader;
import util.QueryUtils;
import java.util.ArrayList;

public abstract class ConnectionInstance {

    Env env;
    QueryUtils queryUtils;
    private final String connectionName;


    public ConnectionInstance(Env env, String connectionName, String queryPath) {
        this.env = env;
        this.connectionName = connectionName;
        this.queryUtils = new QueryUtils(queryPath);
    }

    public String getConnectionName() {
        return connectionName;
    }

    public String getConnectionString() {
        return "jdbc:" + PropertiesLoader.getInstance().getProperty("db.driver",env) +
                getHostSeparator() + PropertiesLoader.getInstance().getProperty("db.host",env) +
                ":" + PropertiesLoader.getInstance().getProperty("db.port",env) +
                getServiceSeparator() + PropertiesLoader.getInstance().getProperty("db.serviceName",env);
    }

    String getHostSeparator() {
        return "://";
    }

    String getServiceSeparator() {
        return "/";
    }

    public String getRangedQuery(int[] range) { return null; }

    public String getUsername() { return PropertiesLoader.getInstance().getProperty("db.user",env); }

    public String getPwd() { return PropertiesLoader.getInstance().getProperty("db.pwd",env); }

    public int getMaxParallelConnection() { return Integer.parseInt(PropertiesLoader.getInstance().getProperty("db.maxParallelConnections",env)); }

    public String getQuery() {
        return this.queryUtils.getQuery();
    }

    public ArrayList<String> getProjectionAttributes() {
        return this.queryUtils.getProjectionAttributes();
    }

    public String getCountQuery() { return this.queryUtils.getCountQuery(); }

}
