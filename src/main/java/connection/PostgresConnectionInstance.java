package connection;

public class PostgresConnectionInstance extends ConnectionInstance {

    public PostgresConnectionInstance(String propertiesSuffix, String connectionName, String queryPath) {
        super(propertiesSuffix, connectionName, queryPath);
    }

    @Override
    public String getRangedQuery(int[] range) {
        return this.queryUtils.getPostgresRangedQuery(range);
    }
}
