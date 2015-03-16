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
    public void testGETSimilarMatchOnAll(){
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
        dao.uid = -1L;//create new record that's exactly the same
        dao.save();
        model.customer.CacheDAO doesntMatchOnPhoneDao = Json.fromJson(Json.toJson(doesntMatchOnPhone), model.customer.CacheDAO.class);
        doesntMatchOnPhoneDao.save(); //
        doesntMatchOnPhoneDao.uid = -1L;//create new record that's exactly the same
        doesntMatchOnPhoneDao.save();

        running(testServer(TEST_SERVER_PORT), () -> {
            WSResponse wsResponse = WS.url("http://localhost:" + TEST_SERVER_PORT + "/customers/" + matchUid +"/similar?matchCriteria=companyName,contactName,phoneNumber,customerRefNo").get().get(2000);
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
            assertThat(body.size()).isEqualTo(1);

        });
        RedisUtil.deleteKeys("test:customer*");
    }
    @Test
    public void testGETSimilarMatchOnNamePhone(){
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
                "111-222-3333",
                "Sosa, Sammy",
                "RTY9803-3234"
        );
        model.customer.CacheDAO dao = Json.fromJson(Json.toJson(customer), model.customer.CacheDAO.class);
        dao.save();
        Long matchUid = dao.uid;
        dao.uid = -1L;//create new record that's exactly the same
        dao.save();
        dao.uid = -1L;//create new record that's exactly the same
        dao.save();
        model.customer.CacheDAO doesntMatchOnPhoneDao = Json.fromJson(Json.toJson(doesntMatchOnPhone), model.customer.CacheDAO.class);
        doesntMatchOnPhoneDao.save(); //
        doesntMatchOnPhoneDao.uid = -1L;//create new record that's exactly the same
        doesntMatchOnPhoneDao.save();

        long uid = dao.uid;
        running(testServer(TEST_SERVER_PORT), () -> {
            WSResponse wsResponse = WS.url("http://localhost:" + TEST_SERVER_PORT + "/customers/" + matchUid +"/similar?matchCriteria=companyName,phoneNumber").get().get(2000);
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
        RedisUtil.deleteKeys("test:customer*");
    }
    /**
     * Test with not query string which should default to matching on company name
     */
    @Test
    public void testGETSimilarNoMatchCriteria(){
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
                "Test samename",
                "111-222-3333",
                "Sosa, Sammy",
                "RTY9803-3234"
        );
        model.customer.CacheDAO dao = Json.fromJson(Json.toJson(customer), model.customer.CacheDAO.class);
        dao.save();
        // save the uid to match against
        Long matchUid = dao.uid;
        dao.uid = -1L;//create new record that's exactly the same
         //should match #1
        dao.save();
        model.customer.CacheDAO doesntMatchOnPhoneDao = Json.fromJson(Json.toJson(doesntMatchOnPhone), model.customer.CacheDAO.class);
        //should match #2
        doesntMatchOnPhoneDao.save();
        doesntMatchOnPhoneDao.uid = -1L;//create new record that's exactly the same
        doesntMatchOnPhoneDao.companyName = "Not the same name";
        // shouldn't match
        doesntMatchOnPhoneDao.save();

        running(testServer(TEST_SERVER_PORT), () -> {
            WSResponse wsResponse = WS.url("http://localhost:" + TEST_SERVER_PORT + "/customers/" + matchUid +"/similar").get().get(2000);
            // Assert Response Status
            assertThat(wsResponse.getStatus()).isEqualTo(OK);
            // Assert Content Type isJ JSON
            assertThat(wsResponse.getHeader("Content-Type")).isEqualToIgnoringCase(ContentType.APPLICATION_JSON.toString());
            // Assert Response Body has content
            JsonNode body = Json.parse(wsResponse.getBody());
            Logger.debug("All customers returned:" + body);
            // Assert that a collection of itemds was returned
            assertThat(body.isArray()).isTrue();
            // Shoudl return 2 out of 4 as  matches
            assertThat(body.size()).isEqualTo(2);

        });
        RedisUtil.deleteKeys("test:customer*");
    }
    //TODO: Test Combinations of Query String options
}
