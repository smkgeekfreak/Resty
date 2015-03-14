import com.fasterxml.jackson.databind.JsonNode;
import model.customer.Customer;
import org.apache.http.entity.ContentType;
import org.junit.*;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

/**
 * Created by mbp-sm on 3/13/15.
 */
@FixMethodOrder
public class CustomerAPI_POSTTest extends CustomerAPIBase{

    @Test
    public void testPOSTNewCustomer(){
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
        running(testServer(TEST_SERVER_PORT), () -> {
            JsonNode json = Json.toJson(postBody);
            WSResponse wsResponse = WS.url("http://localhost:"+TEST_SERVER_PORT+"/customers").post(json).get(1000);
            // Assert Response Status
            assertThat(wsResponse.getStatus()).isEqualTo(CREATED);
            // Assert Content Type is JSON
            assertThat(wsResponse.getHeader("Content-Type")).isEqualToIgnoringCase(ContentType.APPLICATION_JSON.toString());
            // Assert Response Body has content
            assertThat(wsResponse.getBody()).isNotEmpty();
            // Deserialize body
            JsonNode body = Json.parse(wsResponse.getBody());
            Logger.debug("All customers returned:" + body);
            Customer retCustomer = Json.fromJson(body,Customer.class);
            // Assert object can be created from JSON
            assertThat(retCustomer).isNotNull();
            // Assert valid uid returned
            assertThat(retCustomer.uid).isGreaterThan(0);
            // Assert the Location Header was returned with valid URI of the resource
            assertThat(wsResponse.getHeader("Location")).isEqualTo("/customers/"+retCustomer.uid);
        });
    }
}
