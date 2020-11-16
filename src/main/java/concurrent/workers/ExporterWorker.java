package concurrent.workers;

import concurrent.Permits;
import db.DbManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static config.Const.EXPORT_DIR;
import static config.Const.INTERMEDIATE_FILE_SEPARATOR;

public class ExporterWorker implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ExporterWorker.class);
    private final int workerId;

    private final String connectionName;
    private final int[] range;
    private final String rangedQuery;
    private final DbManager dbManager;
    private final ArrayList<String> attrinbutesToExport;

    public ExporterWorker(int workerId, String connectionName, int[] range, String rangedQuery, DbManager dbManager, ArrayList<String> attrinbutesToExport) {
        this.workerId = workerId;
        this.connectionName = connectionName;
        this.range = range;
        this.rangedQuery = rangedQuery;
        this.dbManager = dbManager;
        this.attrinbutesToExport = attrinbutesToExport;
    }

    @Override
    public void run() {
        log.debug("Exporting for: {} range [{},{}]", connectionName, range[0], range[1]);
        try {
            createExport(dbManager.executeQuery(this.rangedQuery));
        } catch (FileNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        Permits.releasePermit(1);
        log.debug("Finished Export for: {} range [{},{}]", connectionName, range[0], range[1]);
    }

    private void createExport(ResultSet resultSet) throws FileNotFoundException, SQLException {
        try (PrintWriter pw = new PrintWriter(EXPORT_DIR + connectionName + INTERMEDIATE_FILE_SEPARATOR + workerId)){
            StringBuilder record = new StringBuilder();
            int i = 1;
            ArrayList<String> attributes = this.attrinbutesToExport;
            while(resultSet.next()) {
                // mantains order
                for (String attribute : attributes) {
                    record.append("\"").append(resultSet.getString(i++)).append("\",");
                }
                i = 1;
                pw.println(record.substring(0,record.length()-1));
                record = new StringBuilder();
            }
            resultSet.close();
            this.dbManager.releaseConnection();
        } catch (FileNotFoundException | SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

}
