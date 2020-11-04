package connection;

import config.Env;

public class OracleConnectionInstance extends ConnectionInstance {

    private boolean sid;

    public OracleConnectionInstance(Env env, String connectionName, boolean sid, String queryPath) {
        super(env, connectionName, queryPath);
        this.sid = sid;
    }

    @Override
    String getHostSeparator() { return ":@"; }

    @Override
    String getServiceSeparator() {
        return this.sid ? ":" : "/";
    }

    @Override
    public String getRangedQuery(int[] range) {
        return this.queryUtils.getOracleRangedQuery(range);
    }
}
