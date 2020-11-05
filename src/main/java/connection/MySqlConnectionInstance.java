package connection;

public class MySqlConnectionInstance extends ConnectionInstance {

    public MySqlConnectionInstance(String propertiesSuffix, String connectionName, String queryPath) {
        super(propertiesSuffix, connectionName, queryPath);
    }

    @Override
    public String getRangedQuery(int[] range) {
        return this.queryUtils.getMySqlRangedQuery(range);
    }
}
