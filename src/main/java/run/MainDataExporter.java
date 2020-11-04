package run;

import config.Env;
import connection.ConnectionInstance;
import connection.PostgresConnectionInstance;

import java.util.ArrayList;
import java.util.List;

import static config.Const.QUERY_DIR;

public class MainDataExporter {

    public static void main (String[] args) throws Exception {
        List<ConnectionInstance> connectionInstances = new ArrayList<>();

        /*ConnectionInstance c1 = new OracleConnectionInstance(Env.LOCAL_1, "LOCAL_1", false, QUERY_DIR + "ROLE_MEMS_2");
        ConnectionInstance c2 = new OracleConnectionInstance(Env.LOCAL_2,"LOCAL_2", true, QUERY_DIR + "ROLE_MEMS");

        ConnectionInstance c3 = new MySqlConnectionInstance(Env.MY_SQL, "MY_SQL", QUERY_DIR + "USER");*/
        ConnectionInstance c4 = new PostgresConnectionInstance(Env.POSTGRES,"PG_1", QUERY_DIR + "q_select_descr_random_1");
        ConnectionInstance c5 = new PostgresConnectionInstance(Env.POSTGRES,"PG_2", QUERY_DIR + "q_select_descr_random_2");

        /*connectionInstances.add(c1);
        connectionInstances.add(c2);
        connectionInstances.add(c3);*/
        connectionInstances.add(c4);
        connectionInstances.add(c5);

        DataService tbe = new DataService(connectionInstances);
        tbe.exportData();
    }
}
