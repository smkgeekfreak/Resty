import com.fasterxml.jackson.databind.JsonNode;
import model.customer.Customer;
import org.apache.http.entity.ContentType;
import org.junit.FixMethodOrder;
import org.junit.Test;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import util.RedisUtil;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

/**
 * Created by mbp-sm on 3/13/15.
 */
@FixMethodOrder
public class CustomerAPI_PUTTest extends CustomerAPIBase {

    /**
     * Test PUT Method for updating a customer
     */
    @Test
    public void testPUTExistingCustomer(){
        Logger.debug("------------------------");
        Logger.debug(new Object() {
        }.getClass().getEnclosingMethod().getName());
        Logger.debug("-------------------------");

        // Create a new customer through dao save
        Customer newCustomer= new Customer(
                -1L,
                "Test Insert Customer",
                "888-888-9999",
                "Sosa, Sammy",
                "RTY9803-3234"
        );
        model.customer.CacheDAO dao = Json.fromJson(Json.toJson(newCustomer), model.customer.CacheDAO.class);
        dao.save();
        // Get uid for created customer
        Long createdUid = dao.uid;

        Customer putData = new Customer(
                createdUid,
                "Update via PUT",
                "000-111-2222",
                "Kaplan,Jim ",
                "PUT-12312"
        );
        running(testServer(TEST_SERVER_PORT), () -> {
            JsonNode putJson = Json.toJson(putData);
            WSResponse wsResponse = WS.url("http://localhost:"+TEST_SERVER_PORT+"/customers/"+createdUid).put(putJson).get(1000);
            // Assert Response Status
            assertThat(wsResponse.getStatus()).isEqualTo(OK);
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
            assertThat(retCustomer.uid).isEqualTo(createdUid);
            assertThat(retCustomer.companyName).isEqualTo(Json.fromJson(putJson,Customer.class).companyName);
        });
        RedisUtil.deleteKeys("test:customer*");
    }

    /**
     * Test PUT Method for a customer that does not exist
     */
    @Test
    public void testPUTCustomerNotExists(){
        Logger.debug("------------------------");
        Logger.debug(new Object() {
        }.getClass().getEnclosingMethod().getName());
        Logger.debug("-------------------------");

        // Get uid for created customer
        Long doesNotExistId = 43242123L;

        Customer putData = new Customer(
                doesNotExistId,
                "Should not Update via PUT",
                "000-111-2222",
                "Kaplan,Jim ",
                "PUT-12312"
        );
        running(testServer(TEST_SERVER_PORT), () -> {
            JsonNode putJson = Json.toJson(putData);
            WSResponse wsResponse = WS.url("http://localhost:"+TEST_SERVER_PORT+"/customers/"+doesNotExistId).put(putJson).get(1000);
            // Assert Response Status
            assertThat(wsResponse.getStatus()).isEqualTo(NOT_FOUND);
            // Assert object can be created from JSON
        });
    }

    /**
     * Test PUT Method for updating a customer does not contain a company name
     */
    @Test
    public void testPUTCustomerNoCompanyName(){
        Logger.debug("------------------------");
        Logger.debug(new Object() {
        }.getClass().getEnclosingMethod().getName());
        Logger.debug("-------------------------");

        // Create a new customer through dao save
        Customer newCustomer= new Customer(
                -1L,
                "Test Insert Customer",
                "888-888-9999",
                "Sosa, Sammy",
                "RTY9803-3234"
        );
        model.customer.CacheDAO dao = Json.fromJson(Json.toJson(newCustomer), model.customer.CacheDAO.class);
        dao.save();
        // Get uid for created customer
        Long createdUid = dao.uid;

        Customer putData = new Customer(
                createdUid,
                "",
                "000-111-2222",
                "Kaplan,Jim ",
                "PUT-12312"
        );
        running(testServer(TEST_SERVER_PORT), () -> {
            JsonNode putJson = Json.toJson(putData);
            WSResponse wsResponse = WS.url("http://localhost:"+TEST_SERVER_PORT+"/customers/"+createdUid).put(putJson).get(1000);
            // Assert Response Status
            assertThat(wsResponse.getStatus()).isEqualTo(BAD_REQUEST);
            // Assert Content Type is JSON
            assertThat(wsResponse.getHeader("Content-Type")).isEqualToIgnoringCase(ContentType.TEXT_PLAIN.withCharset("UTF-8").toString());
            // Assert Response Body has content
            assertThat(wsResponse.getBody()).contains("Company name not provided");
        });
        RedisUtil.deleteKeys("test:customer*");
    }
}

