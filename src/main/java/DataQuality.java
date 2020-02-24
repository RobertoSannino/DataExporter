import config.Env;
import connection.ConnectionInstance;
import connection.MySqlConnectionInstance;
import connection.OracleConnectionInstance;
import connection.PostgresConnectionInstance;

import java.util.ArrayList;
import java.util.List;

import static config.Const.QUERY_DIR;

public class DataQuality {

    public static void main (String[] args) {
        List<ConnectionInstance> connectionInstances = new ArrayList<>();

        ConnectionInstance c1 = new OracleConnectionInstance(Env.LOCAL_1, "LOCAL_1", false, QUERY_DIR + "ROLE_MEMS_2");
        ConnectionInstance c2 = new OracleConnectionInstance(Env.LOCAL_2,"LOCAL_2", true, QUERY_DIR + "ROLE_MEMS");
        connectionInstances.add(c1);
        connectionInstances.add(c2);

/*
        ConnectionInstance c1 = new MySqlConnectionInstance(Env.MY_SQL, "MY_SQL", QUERY_DIR + "USER");
        ConnectionInstance c2 = new PostgresConnectionInstance(Env.POSTGRES,"POSTGRES", QUERY_DIR + "USER");
        connectionInstances.add(c1);
        connectionInstances.add(c2);
*/
        DataService tbe = new DataService(connectionInstances);
        //tbe.substractQueryResults();
        tbe.exportData();
    }
}
