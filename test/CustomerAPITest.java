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
    @Test
    public void testGETCustomerExists(){
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
            WSResponse wsResponse =WS.url("http://localhost:"+TEST_SERVER_PORT+"/customers/"+dao.uid).get().get(2000);
            // Assert Response Status
            assertThat(wsResponse.getStatus()).isEqualTo(OK);
            // Assert Content Type is JSON
            assertThat(wsResponse.getHeader("Content-Type")).isEqualToIgnoringCase(ContentType.APPLICATION_JSON.toString());
            // Assert Response Body has content
            assertThat(wsResponse.getBody()).isNotEmpty();
            // Deserialize body
            JsonNode body = Json.parse(wsResponse.getBody());
            Logger.debug("Customer returned:" + body);
            Customer retCustomer = Json.fromJson(body,Customer.class);
            // Assert object can be created from JSON
            assertThat(retCustomer).isNotNull();
            // Assert valid uid returned
            assertThat(retCustomer.uid).isEqualTo(dao.uid);
        });
    }

    @Test
    public void testGETCustomerNotExists(){
        Logger.debug("------------------------");
        Logger.debug(new Object() {
        }.getClass().getEnclosingMethod().getName());
        Logger.debug("-------------------------");

        long invalidUid = 132131424L;
        running(testServer(TEST_SERVER_PORT), () -> {
            WSResponse wsResponse =WS.url("http://localhost:"+TEST_SERVER_PORT+"/customers/"+invalidUid).get().get(2000);
            // Assert Response Status
            assertThat(wsResponse.getStatus()).isEqualTo(NOT_FOUND);
            // Assert Content Type is HTML
            assertThat(wsResponse.getHeader("Content-Type")).isEqualToIgnoringCase(ContentType.TEXT_PLAIN.withCharset("UTF-8").toString());
            // Assert Response Body has content
            assertThat(wsResponse.getBody()).contains("Customer could not be found");

        });
    }
}
