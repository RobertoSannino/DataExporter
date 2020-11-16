package connection;

public class OracleConnectionInstance extends ConnectionInstance {

    private boolean sid;

    public OracleConnectionInstance(String propertiesSuffix, String connectionName, boolean sid, String queryPath) {
        super(propertiesSuffix, connectionName, queryPath);
        this.sid = sid;
    }

    public OracleConnectionInstance(String connectionName, String queryPath, String driver, String host, String port, String serviceName, String user, String pwd, int mpc, boolean sid) {
        super(connectionName, queryPath, driver, host, port, serviceName, user, pwd, mpc);
        this.sid = sid;
    }

    public boolean isSid() {
        return sid;
    }

    public void setSid(boolean sid) {
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
