import config.PropertiesLoader;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class TestProperties {

    @Test
    public void testReadProperties() {
        assertEquals ("localhost", PropertiesLoader.getInstance().getProperty("db.host", "local.1"));
        assertEquals(System.getProperty("user.dir")+"\\queries\\", PropertiesLoader.getInstance().getProperty("dir.query"));
    }
}
