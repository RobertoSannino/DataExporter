import config.Env;
import connection.ConnectionInstance;

import java.util.ArrayList;
import java.util.List;

import static config.Const.QUERY_DIR;

public class DataQuality {

    public static void main (String[] args) {
        List<ConnectionInstance> connectionInstances = new ArrayList<>();
        ConnectionInstance c1 = new ConnectionInstance(Env.LOCAL_1, "LOCAL_1", false, QUERY_DIR + "USER");
        ConnectionInstance c2 = new ConnectionInstance(Env.LOCAL_2,"LOCAL_2", true, QUERY_DIR + "USER");
        //ConnectionInstance c3 = new ConnectionInstance(Env.LOCAL_1,"LOCAL_3", true, QUERY_DIR + "USER");
        //ConnectionInstance c1 = new ConnectionInstance(Env.LOCAL_1, "LOCAL_1", false, QUERY_DIR + "ROLE_MEMS_2");
        //ConnectionInstance c2 = new ConnectionInstance(Env.LOCAL_2,"LOCAL_2", true, QUERY_DIR + "ROLE_MEMS");

        connectionInstances.add(c1);
        connectionInstances.add(c2);
        //connectionInstances.add(c3);

        DataService tbe = new DataService(connectionInstances);
        //tbe.substractQueryResults();
        tbe.exportData();
    }
}
