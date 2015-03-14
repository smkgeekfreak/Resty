import com.fasterxml.jackson.databind.JsonNode;
import data.RedisCache;
import model.customer.Customer;
import org.apache.http.entity.ContentType;
import org.junit.*;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.test.WithApplication;
import util.Redis;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

/**
 * Created by mbp-sm on 3/13/15.
 */
public class CustomerAPITest extends WithApplication{

//    @BeforeClass
//    public static void setUpClass() {
//    }
//
//    @AfterClass
//    public static void tearDownClass() {
//        Redis.deleteKeys("test:*");
//        //POOL.getResource().flushDB();
//        Redis.POOL.destroy();
//    }
//
//    @Before
//    public void setUp() {
//        startPlay();
//        Redis.POOL = RedisCache.getCache();
//    }
//
//    @After
//    public void tearDown() {
//        stopPlay();
//    }

    @Test
    public void testGETAllCustomers(){
        Logger.debug("------------------------");
        Logger.debug(new Object() {
        }.getClass().getEnclosingMethod().getName());
        Logger.debug("-------------------------");

        running(testServer(3333), () -> {
            WSResponse wsResponse =WS.url("http://localhost:3333/customers").get().get(2000);
            assertThat(wsResponse.getStatus()).isEqualTo(OK);
            assertThat(wsResponse.getBody()).isNotEmpty();
            assertThat(wsResponse.getHeader("Content-Type")).isEqualToIgnoringCase(ContentType.APPLICATION_JSON.toString());
            JsonNode body = Json.parse(wsResponse.getBody());
            Logger.debug("All customers returned:" + body);
//            assertThat(contentType(result)).isEqualTo("application/json");
            assertThat(body.isArray()).isTrue();
            assertThat(body.size()).isGreaterThanOrEqualTo(1);
        });
    }
}
