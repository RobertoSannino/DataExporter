package concurrent.workers;

import concurrent.Permits;
import org.apache.commons.collections4.CollectionUtils;
import util.FileUtils;

import java.util.ArrayList;
import java.util.List;

import static config.Const.EXPORT_DIR;
import static config.Const.EX_TIME;

public class DiffWorker implements Runnable{

    private List<String> files;
    private int workerId;
    private String connectionName;
    private FileUtils fileUtils;

    public DiffWorker (int workerId, String connectionName, List<String> files) {
        this.workerId = workerId;
        this.files = files;
        this.connectionName = connectionName;
        this.fileUtils = new FileUtils();
    }

    @Override
    public void run() {
        List<String> diff = diff(files.get(workerId), files.get( (workerId - 1) * -1) );
        System.out.println("\t\t\t\t --- Founded " + diff.size() + " entries in Connection " + connectionName + " that are not present in the other Connection ");

        this.fileUtils.writeContentToFile(diff, EXPORT_DIR + connectionName + "_EntriesDiff_" + EX_TIME);
        Permits.releaseExecPermits(1);
    }

    public List<String> diff(String f1, String f2) {
        List<String> l1 = this.fileUtils.getListFileContent(f1);
        List<String> l2 = this.fileUtils.getListFileContent(f2);

        return new ArrayList<>(CollectionUtils.subtract(l1, l2));
    }

}
