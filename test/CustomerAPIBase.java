import data.RedisCache;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import play.test.WithApplication;
import util.RedisUtil;

/**
 * Created by mbp-sm on 3/14/15.
 */
public class CustomerAPIBase extends WithApplication {
    protected static final int TEST_SERVER_PORT = 3334;
    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
        RedisUtil.deleteKeys("test:customer*");
        //RedisUtil.POOL.getResource().flushDB();
        RedisUtil.POOL.destroy();
    }

    @Before
    public void setUp() {
        startPlay();
        RedisUtil.POOL = RedisCache.getCache();
    }

    @After
    public void tearDown() {
        stopPlay();
    }
}
