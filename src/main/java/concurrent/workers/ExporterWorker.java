package concurrent.workers;

import db.DbManager;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static config.Const.EXPORT_DIR;
import static config.Const.INTERMEDIATE_FILE_SEPARATOR;

public class ExporterWorker implements Runnable {

    private int workerId;

    private String connectionName;
    private int[] range;
    private String query;
    private DbManager dbManager;
    private ArrayList<String> attrinbutesToExport;

    public ExporterWorker(int workerId, String connectionName, int[] range, String query, DbManager dbManager, ArrayList<String> attrinbutesToExport) {
        this.workerId = workerId;
        this.connectionName = connectionName;
        this.range = range;
        this.query = query;
        this.dbManager = dbManager;
        this.attrinbutesToExport = attrinbutesToExport;
    }

    @Override
    public void run() {
        System.out.println("\t\t||| Exporting for: " + connectionName + " range {" + range[0] + "," + range[1] + "}");
        createExport(dbManager.executeQuery(getRangedQuery(this.query, this.range)));
        System.out.println("\t\t||| Finished Export for: " + connectionName + " range {" + range[0] + "," + range[1] + "}");
    }

    private void createExport(ResultSet set) {
        PrintWriter pw;
        try {
            pw = new PrintWriter(EXPORT_DIR + connectionName + INTERMEDIATE_FILE_SEPARATOR + workerId);
            StringBuilder record = new StringBuilder();
            int i = 1;
            ArrayList<String> attributes = this.attrinbutesToExport;
            while(set.next()) {
                // they are ordered...in theory
                for (String attribute : attributes) {
                    record.append("\"").append(set.getString(i++)).append("\",");
                }
                i = 1;
                pw.println(record.substring(0,record.length()-1));
                record = new StringBuilder();
            }
            set.close();
            this.dbManager.releaseConnection();
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getRangedQuery(String query, int[] range) {
        query = query.substring(0, query.indexOf(" FROM ")) + ", ROWNUM r " + query.substring(query.indexOf(" FROM "));
        return "SELECT * FROM ( " + query + " ) WHERE r >= " + range[0] + " and r < " + range[1] + " order by r";
    }

}
