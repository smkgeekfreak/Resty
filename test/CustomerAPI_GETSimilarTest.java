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
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

/**
 * Created by mbp-sm on 3/14/15.
 */
@FixMethodOrder
public class CustomerAPI_GETSimilarTest extends CustomerAPIBase{
    @Test
    public void testGETSimilar(){
        Logger.debug("------------------------");
        Logger.debug(new Object() {
        }.getClass().getEnclosingMethod().getName());
        Logger.debug("-------------------------");

        Customer customer= new Customer(
                -1L,
                "Test Samename",
                "888-888-9999",
                "Sosa, Sammy",
                "RTY9803-3234"
        );
        Customer similarCustomer1= new Customer(
                -1L,
                "Test Samename",
                "111-111-1111",
                "Sosa, Sally",
                "1111AAA-3234"
        );
        model.customer.CacheDAO dao = Json.fromJson(Json.toJson(customer), model.customer.CacheDAO.class);
        dao.save();
        model.customer.CacheDAO similarDao = Json.fromJson(Json.toJson(similarCustomer1), model.customer.CacheDAO.class);
        similarDao.save();
        similarDao.uid = -1L;
        similarDao.save();

        long uid = dao.uid;
        running(testServer(TEST_SERVER_PORT), () -> {
            WSResponse wsResponse = WS.url("http://localhost:" + TEST_SERVER_PORT + "/customers/" + uid +"/similar?matchCriteria=companyName,contactName,phoneNumber,customerRefNo").get().get(2000);
            // Assert Response Status
            assertThat(wsResponse.getStatus()).isEqualTo(OK);
            // Assert Content Type isJ JSON
            assertThat(wsResponse.getHeader("Content-Type")).isEqualToIgnoringCase(ContentType.APPLICATION_JSON.toString());
            // Assert Response Body has content
            JsonNode body = Json.parse(wsResponse.getBody());
            Logger.debug("All customers returned:" + body);
            // Assert that a collection of itemds was returned
            assertThat(body.isArray()).isTrue();
            // Shoudl return 2 matches
            assertThat(body.size()).isEqualTo(2);

        });
    }
    //TODO: Test No Query String
    @Test
    public void testGETSimilarNoMatchCriteria(){
        Logger.debug("------------------------");
        Logger.debug(new Object() {
        }.getClass().getEnclosingMethod().getName());
        Logger.debug("-------------------------");

        long uid = 1;
        running(testServer(TEST_SERVER_PORT), () -> {
            WSResponse wsResponse = WS.url("http://localhost:" + TEST_SERVER_PORT + "/customers/" + uid +"/similar").get().get(2000);
            // Assert Response Status
            assertThat(wsResponse.getStatus()).isEqualTo(NOT_FOUND);
//            // Assert Content Type is HTML
//            assertThat(wsResponse.getHeader("Content-Type")).isEqualToIgnoringCase(ContentType.TEXT_PLAIN.withCharset("UTF-8").toString());
//            // Assert Response Body has content
//            assertThat(wsResponse.getBody()).contains("Customer could not be found");

        });
    }
    //TODO: Test Combinations of Query String options
}
