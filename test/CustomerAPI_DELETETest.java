import com.fasterxml.jackson.databind.JsonNode;
import model.customer.Customer;
import org.apache.http.entity.ContentType;
import org.junit.Test;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import util.RedisUtil;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.NO_CONTENT;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

/**
 * Created by mbp-sm on 3/15/15.
 */
public class CustomerAPI_DELETETest extends CustomerAPIBase{
    @Test
    public void testDELETEExisting() {
        Logger.info("------------------------");
        Logger.info(new Object() {
        }.getClass().getEnclosingMethod().getName());
        Logger.info("-------------------------");

        Customer customer= new Customer(
                -1L,
                "Test Samename",
                "888-888-9999",
                "Sosa, Sammy",
                "RTY9803-3234"
        );
        Customer doesntMatchOnPhone = new Customer(
                -1L,
                "Test Samename",
                "111-111-1111",
                "Sosa, Sally",
                "1111AAA-3234"
        );
        model.customer.CacheDAO dao = Json.fromJson(Json.toJson(customer), model.customer.CacheDAO.class);
        dao.save();
        Long matchUid = dao.uid;
        assertThat(dao.findAll().size()).isEqualTo(1);
        running(testServer(TEST_SERVER_PORT), () -> {
            WSResponse wsResponse = WS.url("http://localhost:" + TEST_SERVER_PORT + "/customers/" + matchUid).delete().get(2000);
            // Assert Response Status
            assertThat(wsResponse.getStatus()).isEqualTo(NO_CONTENT);
            // Assert Content Type HTML
            assertThat(dao.findAll().size()).isEqualTo(0);

        });
        RedisUtil.deleteKeys("test:customer*");
    }
    @Test
    public void testDELETENotExists() {
        Logger.info("------------------------");
        Logger.info(new Object() {
        }.getClass().getEnclosingMethod().getName());
        Logger.info("-------------------------");

        Long deleteUid = 1L;
        RedisUtil.deleteKeys("test:customer*");
        running(testServer(TEST_SERVER_PORT), () -> {
            WSResponse wsResponse = WS.url("http://localhost:" + TEST_SERVER_PORT + "/customers/" + deleteUid).delete().get(2000);
            // Assert Response Status
            assertThat(wsResponse.getStatus()).isEqualTo(NOT_FOUND);
        });
    }
}
