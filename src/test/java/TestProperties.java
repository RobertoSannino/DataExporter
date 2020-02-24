import config.Env;
import config.PropertiesLoader;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class TestProperties {

    @Test
    public void testReadProperties() {
        assertEquals ("10.21.5.14", PropertiesLoader.getInstance().getProperty("db.host", Env.LOCAL_1));
        assertEquals ("10.21.5.49", PropertiesLoader.getInstance().getProperty("db.host", Env.LOCAL_2));
        assertEquals(System.getProperty("user.dir")+"\\queries\\", PropertiesLoader.getInstance().getProperty("dir.query"));
    }
}
