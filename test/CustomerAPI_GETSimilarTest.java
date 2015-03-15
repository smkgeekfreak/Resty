import org.apache.http.entity.ContentType;
import org.junit.FixMethodOrder;
import org.junit.Test;
import play.Logger;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.NOT_FOUND;
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

        long uid = 1;
        running(testServer(TEST_SERVER_PORT), () -> {
            WSResponse wsResponse = WS.url("http://localhost:" + TEST_SERVER_PORT + "/customers/" + uid +"/similar?matchCriteria=companyName,contactName,phoneNumber,customerRefNo").get().get(2000);
            // Assert Response Status
            assertThat(wsResponse.getStatus()).isEqualTo(NOT_FOUND);
//            // Assert Content Type is HTML
//            assertThat(wsResponse.getHeader("Content-Type")).isEqualToIgnoringCase(ContentType.TEXT_PLAIN.withCharset("UTF-8").toString());
//            // Assert Response Body has content
//            assertThat(wsResponse.getBody()).contains("Customer could not be found");

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
    //TODO: Test Combinations of Querty String options
}
