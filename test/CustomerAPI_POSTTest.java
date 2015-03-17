import com.fasterxml.jackson.databind.JsonNode;
import model.customer.Customer;
import org.apache.http.entity.ContentType;
import org.junit.FixMethodOrder;
import org.junit.Test;
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
            assertThat(retCustomer.uid).isGreaterThan(-1);
            // Assert the Location Header was returned with valid URI of the resource
            assertThat(wsResponse.getHeader("Location")).isEqualTo("/customers/"+retCustomer.uid);
        });
    }
    @Test
    public void testPOSTExistingCustomer(){
        Logger.debug("------------------------");
        Logger.debug(new Object() {
        }.getClass().getEnclosingMethod().getName());
        Logger.debug("-------------------------");

        Customer postBody = new Customer(
                -1L,
                "Test Create Customer before update",
                "999-888-9999",
                "Sosa, Sarah",
                "RTY9803-8888"
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
            Logger.debug("customer returned:" + body);
            Customer retCustomer = Json.fromJson(body,Customer.class);
            // Assert object can be created from JSON
            assertThat(retCustomer).isNotNull();
            // Assert valid uid returned
            assertThat(retCustomer.uid).isGreaterThan(-1);
            // Assert the Location Header was returned with valid URI of the resource
            assertThat(wsResponse.getHeader("Location")).isEqualTo("/customers/"+retCustomer.uid);

            // Update the Customer uid with uid returned above
            postBody.uid = retCustomer.uid;
            postBody.companyName = "Test Updated Insert Customer";
            postBody.customerRefNo= "AAA-BBB1234";
            JsonNode update = Json.toJson(postBody);
            // Call update on object using uid of the response above
            wsResponse = WS.url("http://localhost:"+TEST_SERVER_PORT+"/customers").post(update).get(1000);
            // Assert Response Status
            assertThat(wsResponse.getStatus()).isEqualTo(OK);
            // Assert Content Type is JSON
            assertThat(wsResponse.getHeader("Content-Type")).isEqualToIgnoringCase(ContentType.APPLICATION_JSON.toString());
            // Assert Response Body has content
            assertThat(wsResponse.getBody()).isNotEmpty();
            // Deserialize body
            body = Json.parse(wsResponse.getBody());
            Logger.debug("customer updated returned:" + body);
            Customer modifiedCustomer = Json.fromJson(body,Customer.class);
            // Assert object can be created from JSON
            assertThat(modifiedCustomer).isNotNull();
            // Assert valid uid returned
            assertThat(modifiedCustomer.uid).isEqualTo(retCustomer.uid);
            // Assert the Location Header was returned with valid URI of the resource
            assertThat(wsResponse.getHeader("Location")).isEqualTo("/customers/"+retCustomer.uid);
        });
    }
}
