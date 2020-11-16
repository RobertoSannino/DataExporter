package run;

import concurrent.Permits;
import concurrent.workers.ExporterWorker;
import connection.ConnectionValidator;
import db.DbManager;
import connection.ConnectionInstance;
import exception.InputValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import static ch.qos.logback.core.util.OptionHelper.isEmpty;
import static config.Const.*;

public class DataService {

    private static final Logger log = LoggerFactory.getLogger(DataService.class);
    private final List<ConnectionInstance> connectionInstances;

    public DataService(List<ConnectionInstance> connectionInstances) {
        this.connectionInstances = connectionInstances;
    }

    public void exportData() throws InputValidationException, InterruptedException {
        log.info("VALIDATING INPUT");
        for (ConnectionInstance connectionInstance : connectionInstances) {
            String errors = ConnectionValidator.validateConnection(connectionInstance);
            if (!isEmpty(errors)) {
                throw new InputValidationException(connectionInstance.getConnectionName() + ": " + errors);
            }
        }

        log.info("STARTING EXPORTS");
        long startTime = System.currentTimeMillis();
        int maxParallelConnections = connectionInstances.stream().map(ConnectionInstance::getMaxParallelConnection).reduce(Integer::sum).orElse(0);

        Permits.setExecPermitsNumber(maxParallelConnections);
        Permits.acquireExecPermits(maxParallelConnections);
        connectionInstances.forEach(ci -> splitExport(ci, getResultSetRows(ci)));
        Permits.acquireExecPermits(maxParallelConnections);

        connectionInstances.forEach(this::mergeFiles);

        log.info("EXPORTS FINISHED IN {} SECONDS",(System.currentTimeMillis() - startTime)/1000f);
    }

    private int getResultSetRows(ConnectionInstance c) {
        long startTime = System.currentTimeMillis();
        DbManager dbManager = new DbManager(c);
        int rows = dbManager.countTable(c.getCountQuery());
        dbManager.releaseConnection();
        log.info("COUNT FOR {} FINISHED IN {} SECONDS", c.getConnectionName(), (System.currentTimeMillis() - startTime)/1000f);
        return rows;
    }

    private void splitExport(ConnectionInstance c, int rows) {
        int threadsNumber = c.getMaxParallelConnection();

        int rangeInf = 0;
        int rangeSup = rows / threadsNumber;
        for (int i = 0; i < threadsNumber; i++ ) {
            rangeSup = (i == threadsNumber - 1) ? rows + 1 : rangeSup;
            int[] newRange = new int[]{rangeInf, rangeSup};
            new Thread(
                    new ExporterWorker(
                            i, c.getConnectionName(), newRange, c.getRangedQuery(newRange), new DbManager(c), c.getProjectionAttributes()
                    )
            ).start();
            rangeInf = rangeSup;
            rangeSup = rangeSup + (rows / threadsNumber);
        }
    }

    private void mergeFiles(ConnectionInstance c) {
        int threadsNo = c.getMaxParallelConnection();

        long startTime = System.currentTimeMillis();
        File file = new File(EXPORT_DIR + c.getConnectionName() + "_" + EX_TIME);
        PrintWriter pw;
        try {
            Files.deleteIfExists(file.toPath());
            pw = new PrintWriter(new FileOutputStream(file, true));
            pw.println(c.getProjectionAttributes().toString().replace("[","").replace("]",""));
            for(int i = 0; i < threadsNo; i++) {
                try (Scanner scanner = new Scanner(new File(EXPORT_DIR + c.getConnectionName() + INTERMEDIATE_FILE_SEPARATOR + i))){
                    while (scanner.hasNextLine()) {
                        pw.println(scanner.nextLine().trim());
                    }
                    //DELETE PARTIAL FILE
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                this.deletePartialFile(c.getConnectionName(), i);
            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("MERGE INTERMEDIATE FILES FOR {} FINISHED IN {} SECONDS",c.getConnectionName(), (System.currentTimeMillis() - startTime)/1000f);
    }

    private void deletePartialFile(String connectionName, int fileNumber) {
        String path = EXPORT_DIR + connectionName + INTERMEDIATE_FILE_SEPARATOR + fileNumber;
        try {
            Files.delete(new File(path).toPath());
        } catch (Exception e) {
            log.error("ERROR OCCURRED WHILE DELETING: {}", path);
            e.printStackTrace();
        }
    }

}
