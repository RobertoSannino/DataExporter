import connection.OracleConnectionInstance;
import connection.PostgresConnectionInstance;
import db.DbManager;
import connection.ConnectionInstance;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static config.Const.QUERY_DIR;

public class TestNoAssertions {

    private String query;

    @Before
    public void init() {
        this.query = QUERY_DIR + "q_select_descr_random_1";
    }

    @Test
    public void testConnection() {
        ConnectionInstance c1 = new PostgresConnectionInstance("postgres", "PG_1", this.query);
        System.out.println(c1.getConnectionString());
        DbManager dbManager = new DbManager(c1);
        dbManager.executeQuery("SELECT * FROM public.t_random_1 WHERE id = 1");
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
    public void testOracleRangedQueryCreation() {
        int[] range = new int[]{0,50};
        String query = "SELECT USR_LOGIN AS PIPPO_LOGIN, USR_LAST_NAME AS NACHNAME FROM USR ";
        String newQuery;
        query = query.substring(0, query.indexOf(" FROM ")) + ", ROWNUM r " + query.substring(query.indexOf(" FROM "));
        newQuery = "SELECT * FROM ( " + query + " ) WHERE r >= " + range[0] + " and r < " + range[1] + " order by r";
        System.out.println(newQuery);
    }

    @Test
    public void testMySqlRangedQueryCreation() {
        int[] range = new int[]{0,50};
        String query = "SELECT USR_LOGIN AS PIPPO_LOGIN, USR_LAST_NAME AS NACHNAME FROM USR ";
        String newQuery = query + " LIMIT " + range[0] + "," + range[1];
        System.out.println(newQuery);
    }

    @Test
    public void testOracleRangedQuery() {
        ConnectionInstance c1 = new OracleConnectionInstance("local.1", "LOCAL_1", false, this.query);

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
