package connection;

import config.Env;
import config.PropertiesLoader;
import util.QueryHelper;
import java.util.ArrayList;

public abstract class ConnectionInstance {

    Env env;
    QueryHelper queryHelper;
    private String connectionName;


    public ConnectionInstance(Env env, String connectionName, String queryPath) {
        this.env = env;
        this.connectionName = connectionName;
        this.queryHelper = new QueryHelper(queryPath);
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

    public int getMaxParallelConnection() { return Integer.valueOf(PropertiesLoader.getInstance().getProperty("db.maxParallelConnections",env)); }

    public String getQuery() {
        return this.queryHelper.getQuery();
    }

    public ArrayList<String> getProjectionAttributes() {
        return this.queryHelper.getProjectionAttributes();
    }

    public String getCountQuery() { return this.queryHelper.getCountQuery(); }

}
