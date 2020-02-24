import config.Env;
import connection.ConnectionInstance;

import static config.Const.QUERY_DIR;

public class DataQuality {

    public static void main (String[] args) {
        //ConnectionInstance c1 = new ConnectionInstance(Env.LOCAL_1, "LOCAL_1", false, QUERY_DIR + "USER");
        ConnectionInstance c1 = new ConnectionInstance(Env.LOCAL_1, "LOCAL_1", false, QUERY_DIR + "ROLE_MEMS_2");
        //ConnectionInstance c2 = new ConnectionInstance(Env.LOCAL_2,"LOCAL_2", true, QUERY_DIR + "USER");
        ConnectionInstance c2 = new ConnectionInstance(Env.LOCAL_2,"LOCAL_2", true, QUERY_DIR + "ROLE_MEMS");

        DataService tbe = new DataService(c1, c2);
        //tbe.substractQueryResults();
        tbe.exportData();
    }
}
