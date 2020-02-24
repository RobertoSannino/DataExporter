package connection;

import config.Env;
import config.PropertiesLoader;

public class OracleConnectionInstance extends ConnectionInstance {

    private boolean sid;

    public OracleConnectionInstance(Env env, String connectionName, boolean SID, String queryPath) {
        super(env, connectionName, queryPath);
        this.sid = SID;
    }

    @Override
    String getHostSeparator() { return ":@"; }

    @Override
    String getServiceSeparator() {
        return this.sid ? ":" : "/";
    }

    @Override
    public String getRangedQuery(int[] range) {
        return this.queryHelper.getOracleRangedQuery(range);
    }
}
