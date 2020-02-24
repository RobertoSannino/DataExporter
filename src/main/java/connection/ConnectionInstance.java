package connection;

import config.Env;
import config.PropertiesLoader;
import util.QueryHelper;
import java.util.ArrayList;

public class ConnectionInstance {

    private Env env;
    private String connectionName;
    private boolean sid;
    private QueryHelper queryHelper;

    public ConnectionInstance(Env env, String connectionName, boolean SID, String queryPath) {
        this.env = env;
        this.connectionName = connectionName;
        this.sid = SID;
        this.queryHelper = new QueryHelper(queryPath);
    }

    public String getConnectionName() {
        return connectionName;
    }

    public String getConnectionString() {
        return "jdbc:" + PropertiesLoader.getInstance().getProperty("db.driver",env) +
                ":@" + PropertiesLoader.getInstance().getProperty("db.host",env) +
                ":" + PropertiesLoader.getInstance().getProperty("db.port",env) +
                getServiceSeparator() + PropertiesLoader.getInstance().getProperty("db.serviceName",env);
    }

    private String getServiceSeparator() {
        return this.sid ? ":" : "/";
    }

    public String getUsername() { return PropertiesLoader.getInstance().getProperty("db.user",env); }

    public String getPwd() { return PropertiesLoader.getInstance().getProperty("db.pwd",env); }

    public int getMaxParallelConnection() {
        return Integer.valueOf(PropertiesLoader.getInstance().getProperty("db.maxParallelConnections",env));
    }

    public String getQuery() {
        return this.queryHelper.getQuery();
    }

    public ArrayList<String> getProjectionAttributes() {
        return this.queryHelper.getProjectionAttributes();
    }

    public String getCountQuery() { return this.queryHelper.getCountQuery(); }
}
