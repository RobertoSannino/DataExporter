package config;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Const {
    private static final String DATE_FORMAT = PropertiesLoader.getInstance().getProperty("date.format");

    public static final String EXPORT_DIR;
    public static final String QUERY_DIR = PropertiesLoader.getInstance().getProperty("dir.query");

    public static final String INTERMEDIATE_FILE_SEPARATOR = "---";

    public static String EX_TIME;

    static {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        EX_TIME = format.format(new Date());
        EXPORT_DIR = PropertiesLoader.getInstance().getProperty("dir.export") + EX_TIME + "/";
        new File(EXPORT_DIR).mkdir();
    }
}
