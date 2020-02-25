import concurrent.workers.DiffWorker;
import concurrent.workers.ExporterWorker;
import concurrent.Permits;
import db.DbManager;
import connection.ConnectionInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;

import static config.Const.*;

public class DataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataService.class);
    private ExecutorService exporterPool;
    private List<ConnectionInstance> connectionInstances;


    public DataService(List<ConnectionInstance> connectionInstances) {
        this.connectionInstances = connectionInstances;
        Permits.setExecPermitsNumber(connectionInstances.size());
    }

    public void substractQueryResults() {
        LOGGER.warn("SUBSTRACT STARTED");

        if(connectionInstances.size() != 2) {
            LOGGER.error("DIFF OPERATION CAN ONLY BE PERFORMED BETWEEN TWO CONNECTIONS");
            return;
        }

        exportData();

        List<String> files = new ArrayList<>();
        connectionInstances.forEach(ci -> files.add(EXPORT_DIR + ci.getConnectionName() + "_" + EX_TIME));

        diff(files);

        LOGGER.warn("SUBSTRACT FINISHED");
    }

    public void exportData() {
        LOGGER.warn("STARTING EXPORTS");
        long startTime = System.currentTimeMillis();

        exporterPool = Executors.newCachedThreadPool();
        connectionInstances.forEach(ci -> splitExport(ci, getResultSetRows(ci)));

        exporterPool.shutdown();
        while(!exporterPool.isTerminated()) {
            try {
                exporterPool.awaitTermination(10, TimeUnit.SECONDS);
                LOGGER.warn("WAITING EXPORTS TO FINISH");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        connectionInstances.forEach(this::mergeFiles);

        LOGGER.warn("EXPORTS FINISHED IN " + (System.currentTimeMillis() - startTime)/1000f + " SECONDS");
    }

    private void diff(List<String> files) {
        LOGGER.warn("STARTING COMPUTING DIFFERENCES");

        long startTime = System.currentTimeMillis();
        Permits.acquireExecPermits(connectionInstances.size());

        new Thread(new DiffWorker(0, connectionInstances.get(0).getConnectionName(), files)).start();
        new Thread(new DiffWorker(1, connectionInstances.get(1).getConnectionName(), files)).start();

        Permits.acquireExecPermits(connectionInstances.size());
        LOGGER.warn("DIFFERENCES COMPUTATION FINISHED IN " + (System.currentTimeMillis() - startTime)/1000f + " SECONDS");
    }

    private int getResultSetRows(ConnectionInstance c) {
        long startTime = System.currentTimeMillis();
        DbManager dbManager = new DbManager(c);
        int rows = dbManager.countTable(c.getCountQuery());
        dbManager.releaseConnection();
        LOGGER.warn("COUNT FOR " + c.getConnectionName() + " FINISHED IN " + (System.currentTimeMillis() - startTime)/1000f + " SECONDS");
        return rows;
    }

    private void splitExport(ConnectionInstance c, int rows) {
        int THREADS_NO = c.getMaxParallelConnection();

        int rangeInf = 0; int rangeSup = rows / THREADS_NO;
        for (int i = 0; i < THREADS_NO; i++ ) {
            if (i == THREADS_NO - 1)
                rangeSup = rows + 1;

            int[] newRange = new int[]{rangeInf, rangeSup};
            exporterPool.submit(
                    new ExporterWorker(
                            i, c.getConnectionName(), newRange, c.getRangedQuery(newRange), new DbManager(c), c.getProjectionAttributes()
                    )
            );
            rangeInf = rangeSup;
            rangeSup = rangeSup + (rows / THREADS_NO);
        }
    }

    private void mergeFiles(ConnectionInstance c) {
        int THREADS_NO = c.getMaxParallelConnection();

        long startTime = System.currentTimeMillis();
        File file = new File(EXPORT_DIR + c.getConnectionName() + "_" + EX_TIME);
        PrintWriter pw;
        try {
            Files.deleteIfExists(file.toPath());
            pw = new PrintWriter(new FileOutputStream(file, true));
            pw.println(c.getProjectionAttributes().toString().replace("[","").replace("]",""));
            for(int i = 0; i < THREADS_NO; i++) {
                try {
                    Scanner scanner = new Scanner(new File(EXPORT_DIR + c.getConnectionName() + INTERMEDIATE_FILE_SEPARATOR + i));
                    while (scanner.hasNextLine()) {
                        pw.println(scanner.nextLine().trim());
                    }
                    scanner.close();
                    //DELETE PARTIAL FILE
                    try {
                        new File(EXPORT_DIR + c.getConnectionName() + INTERMEDIATE_FILE_SEPARATOR + i).delete();
                    } catch (Exception e) {
                        LOGGER.error("ERROR OCCURRED WHILE DELETING: " + EXPORT_DIR + c.getConnectionName() + INTERMEDIATE_FILE_SEPARATOR + i);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        LOGGER.warn("MERGE INTERMEDIATE FILES FOR " + c.getConnectionName() + " FINISHED IN " + (System.currentTimeMillis() - startTime)/1000f + " SECONDS");
    }

}
