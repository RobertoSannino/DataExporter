package connection;

public class OracleConnectionInstance extends ConnectionInstance {

    private boolean sid;

    public OracleConnectionInstance(String propertiesSuffix, String connectionName, boolean sid, String queryPath) {
        super(propertiesSuffix, connectionName, queryPath);
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
