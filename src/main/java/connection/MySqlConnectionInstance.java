package connection;

import util.QueryUtils;

public class MySqlConnectionInstance extends ConnectionInstance {

    public MySqlConnectionInstance(String propertiesSuffix, String connectionName, String queryPath) {
        super(propertiesSuffix, connectionName, queryPath);
    }

    public MySqlConnectionInstance(String connectionName, String queryPath, String driver, String host, String port, String serviceName, String user, String pwd, int mpc) {
        super(connectionName, queryPath, driver, host, port, serviceName, user, pwd, mpc);
    }

    @Override
    public String getRangedQuery(int[] range) {
        return this.queryUtils.getMySqlRangedQuery(range);
    }
}
