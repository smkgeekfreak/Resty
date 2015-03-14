import com.fasterxml.jackson.databind.JsonNode;
import model.customer.Customer;
import org.apache.http.entity.ContentType;
import org.junit.*;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

/**
 * Created by mbp-sm on 3/13/15.
 */
@FixMethodOrder
public class CustomerAPITest extends CustomerAPIBase{

    @Test
    public void testGETAllCustomers(){
        Logger.debug("------------------------");
        Logger.debug(new Object() {
        }.getClass().getEnclosingMethod().getName());
        Logger.debug("-------------------------");

        Customer postBody = new Customer(
                -1L,
                "Test Insert Customer",
                "888-888-9999",
                "Sosa, Sammy",
                "RTY9803-3234"
        );
        model.customer.CacheDAO dao = Json.fromJson(Json.toJson(postBody), model.customer.CacheDAO.class);
        dao.save();

        running(testServer(TEST_SERVER_PORT), () -> {
            WSResponse wsResponse =WS.url("http://localhost:"+TEST_SERVER_PORT+"/customers").get().get(2000);
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
