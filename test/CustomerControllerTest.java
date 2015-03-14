import com.fasterxml.jackson.databind.JsonNode;
import controllers.CustomerController;
import org.junit.Test;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import play.test.WithApplication;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.*;
import static play.test.Helpers.contentAsString;

/**
 * Created by mbp-sm on 3/13/15.
 */
public class CustomerControllerTest extends WithApplication {

    @Test
    public void testEndpointGETAll() {
        Logger.debug("-----------------------------------------------");
        Logger.debug(new Object() {
        }.getClass().getEnclosingMethod().getName());
        Logger.debug("-----------------------------------------------");
        Result result = CustomerController.findAll();
        assertThat(status(result)).isEqualTo(OK);
        assertThat(contentType(result)).isEqualTo("application/json");
        String body = contentAsString(result);
        JsonNode node = Json.parse(body);
        assertThat(node.isArray()).isTrue();
        assertThat(node.size()).isGreaterThanOrEqualTo(1);
        Logger.info(body);
    }
}
