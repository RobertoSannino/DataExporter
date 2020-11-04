package connection;

import config.Env;

public class MySqlConnectionInstance extends ConnectionInstance {

    public MySqlConnectionInstance(Env env, String connectionName, String queryPath) {
        super(env, connectionName, queryPath);
    }

    @Override
    public String getRangedQuery(int[] range) {
        return this.queryUtils.getMySqlRangedQuery(range);
    }
}
