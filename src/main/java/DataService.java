import concurrent.workers.DiffWorker;
import concurrent.workers.ExporterWorker;
import concurrent.Permits;
import db.DbManager;
import connection.ConnectionInstance;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;

import static config.Const.*;

public class DataService {

    private ExecutorService exporterPool;

    private ConnectionInstance c1;
    private ConnectionInstance c2;


    public DataService(ConnectionInstance c1, ConnectionInstance c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    /* main */
    public void substractQueryResults() {
        exportData();

        List<String> files = new ArrayList<>();
        files.add(EXPORT_DIR + c1.getConnectionName() + "_" + EX_TIME);
        files.add(EXPORT_DIR + c2.getConnectionName() + "_" + EX_TIME);

        diff(files);

        System.out.println("*** SUBSTRACT FINISHED ***");
    }

    private void diff(List<String> files) {
        System.out.println("*** STARTING COMPUTING DIFFERENCES ***");
        long startTime = System.currentTimeMillis();
        Permits.acquireExecPermits(2);

        new Thread(new DiffWorker(0, c1.getConnectionName(), files)).start();
        new Thread(new DiffWorker(1, c2.getConnectionName(), files)).start();

        Permits.acquireExecPermits(2);
        System.out.println("*** DIFFERENCES COMPUTATION FINISHED IN " + (System.currentTimeMillis() - startTime)/1000f + " SECONDS ***");
    }

    public void exportData() {
        System.out.println("*** STARTING EXPORTS ***");
        long startTime = System.currentTimeMillis();

        exporterPool = Executors.newCachedThreadPool();
        splitExport(this.c1, getResultSetRows(this.c1));
        splitExport(this.c2, getResultSetRows(this.c2));

        exporterPool.shutdown();
        while(!exporterPool.isTerminated()) {
            try {
                exporterPool.awaitTermination(10, TimeUnit.SECONDS);
                System.out.println("*** WAITING EXPORTS ***");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        mergeFiles(c1);
        mergeFiles(c2);

        System.out.println("*** EXPORTS FINISHED IN " + (System.currentTimeMillis() - startTime)/1000f + " SECONDS ***");
    }

    private int getResultSetRows(ConnectionInstance c) {
        long startTime = System.currentTimeMillis();
        DbManager dbManager = new DbManager(c);
        int rows = dbManager.countTable(c.getCountQuery());
        dbManager.releaseConnection();
        System.out.println("*** COUNT FOR " + c.getConnectionName() + " FINISHED IN " + (System.currentTimeMillis() - startTime)/1000f + " SECONDS ***");
        return rows;
    }

    private void splitExport(ConnectionInstance c, int rows) {
        int THREADS_NO = c.getMaxParallelConnection();

        int rangeInf = 0; int rangeSup = rows / THREADS_NO;
        for (int i = 0; i < THREADS_NO; i++ ) {
            if (i == THREADS_NO - 1)
                rangeSup = rows + 1;
            exporterPool.submit(
                    new ExporterWorker(
                            i, c.getConnectionName(), new int[]{rangeInf, rangeSup}, c.getQuery(), new DbManager(c), c.getProjectionAttributes()
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
                        System.out.println("*** ERROR OCCURRED WHILE DELETING: " + EXPORT_DIR + c.getConnectionName() + INTERMEDIATE_FILE_SEPARATOR + i);
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

        System.out.println("*** MERGE FOR " + c.getConnectionName() + " FINISHED IN " + (System.currentTimeMillis() - startTime)/1000f + " SECONDS ***");
    }

}
