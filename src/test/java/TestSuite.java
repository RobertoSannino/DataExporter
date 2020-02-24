import config.Env;
import db.DbManager;
import org.junit.Before;
import org.junit.Test;
import connection.ConnectionInstance;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static config.Const.QUERY_DIR;
import static java.lang.Thread.sleep;

public class TestSuite {

    private String userQuery;
    private String roleMembershipsQuery;

    @Before
    public void init() {
        this.userQuery = QUERY_DIR + "USER";
        this.roleMembershipsQuery = QUERY_DIR + "ROLE_MEMS";
    }

    @Test
    public void testConnection() {
        ConnectionInstance c1 = new ConnectionInstance(Env.LOCAL_1, "LOCAL_1", false, this.userQuery);
        System.out.println(c1.getConnectionString());
        DbManager dbManager = new DbManager(c1);
        dbManager.executeQuery("SELECT USR_KEY FROM USR");

        ConnectionInstance c2 = new ConnectionInstance(Env.LOCAL_2, "LOCAL_2", false, this.userQuery);
        System.out.println(c2.getConnectionString());
        DbManager dbManager2 = new DbManager(c2);
        dbManager2.executeQuery("SELECT USR_KEY FROM USR");
    }

    @Test
    public void testSplitRange() {
        int rows = 301;
        int THREADS_NO = 4;
        HashMap<Integer, int[]> exportersRange = new HashMap<>();
        int rangeInf = 0; int rangeSup = rows / THREADS_NO;
        for (int i = 0; i < THREADS_NO; i++ ) {
            if (i == THREADS_NO - 1)
                rangeSup = rows;
            exportersRange.put(i, new int[] {rangeInf, rangeSup});
            rangeInf = rangeSup;
            rangeSup = rangeSup + (rows / THREADS_NO);
        }

        for(Integer a : exportersRange.keySet()) {
            int[] array = exportersRange.get(a);
            System.out.println(array[0] + " - " + array[1]);
        }
    }

    @Test
    public void testRangedQueryCreation() {
        int[] range = new int[]{0,50};
        String query = "SELECT USR_LOGIN AS PIPPO_LOGIN, USR_LAST_NAME AS NACHNAME FROM USR ";
        String newQuery;
        query = query.substring(0, query.indexOf(" FROM ")) + ", ROWNUM r " + query.substring(query.indexOf(" FROM "));
        newQuery = "SELECT * FROM ( " + query + " ) WHERE r >= " + range[0] + " and r < " + range[1] + " order by r";
        System.out.println(newQuery);
    }

    @Test
    public void testRangedQuery() {
        ConnectionInstance c1 = new ConnectionInstance(Env.LOCAL_1, "LOCAL_1", false, this.userQuery);

        String query = "SELECT USR_LOGIN AS PIPPO_LOGIN, USR_LAST_NAME AS NACHNAME FROM USR WHERE USR_STATUS != 'Deleted' ORDER BY USR_LOGIN";
        query = query.substring(0, query.indexOf(" FROM ")) + ", rownum r " + query.substring(query.indexOf(" FROM "));
        query = "SELECT * FROM ( " + query + " ) WHERE r >= 31687 and r < 63374 order by r";

        System.out.println(query);

        DbManager dbManager = new DbManager(c1);
        ResultSet results = dbManager.executeQuery(query);
        try {
            while(results.next()) {
                System.out.println(results.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
