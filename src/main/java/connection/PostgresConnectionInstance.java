package connection;

import config.Env;

public class PostgresConnectionInstance extends ConnectionInstance {

    public PostgresConnectionInstance(Env env, String connectionName, String queryPath) {
        super(env, connectionName, queryPath);
    }

    @Override
    public String getRangedQuery(int[] range) {
        return this.queryUtils.getPostgresRangedQuery(range);
    }
}
