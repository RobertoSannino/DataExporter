import config.Env;
import config.PropertiesLoader;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class TestProperties {

    @Test
    public void testReadProperties() {
        assertEquals ("localhost", PropertiesLoader.getInstance().getProperty("db.host", Env.LOCAL_1));
        assertEquals(System.getProperty("user.dir")+"\\queries\\", PropertiesLoader.getInstance().getProperty("dir.query"));
    }
}
