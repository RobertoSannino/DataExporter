package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

    private static final String PROPERTIES_FILE = "properties.properties";
    private static Properties properties;
    private static PropertiesLoader instance = new PropertiesLoader(PROPERTIES_FILE);

    private PropertiesLoader(String propertiesFile) {
        loadPropertiesFile(propertiesFile);
    }

    public static synchronized PropertiesLoader getInstance() {
        if(instance == null)
            instance = new PropertiesLoader(PROPERTIES_FILE);
        return instance;
    }

    public String getProperty(String key) {
        return getProperties().getProperty(key);
    }

    public synchronized String getProperty(String key, Env env) {
        return getProperties().getProperty(key+"."+env.getConnPropName());
    }

    private Properties getProperties(){
        return properties;
    }

    private static void loadPropertiesFile(String propertiesFile) {
        properties = new Properties();
        try (InputStream input = PropertiesLoader.class.getClassLoader().getResourceAsStream(propertiesFile)) {
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
