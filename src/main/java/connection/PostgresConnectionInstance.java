package connection;

public class PostgresConnectionInstance extends ConnectionInstance {

    public PostgresConnectionInstance(String propertiesSuffix, String connectionName, String queryPath) {
        super(propertiesSuffix, connectionName, queryPath);
    }

    public PostgresConnectionInstance(String connectionName, String queryPath, String driver, String host, String port, String serviceName, String user, String pwd, int mpc) {
        super(connectionName, queryPath, driver, host, port, serviceName, user, pwd, mpc);
    }

    @Override
    public String getRangedQuery(int[] range) {
        return this.queryUtils.getPostgresRangedQuery(range);
    }
}
