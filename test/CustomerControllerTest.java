import org.junit.Test;
import play.Logger;
import play.libs.ws.WS;
import play.test.WithApplication;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

/**
 * Created by mbp-sm on 3/13/15.
 */
public class CustomerControllerTest extends WithApplication {

    @Test
    public void testGETAllCustomers(){
        Logger.debug("------------------------");
        Logger.debug(new Object() {
        }.getClass().getEnclosingMethod().getName());
        Logger.debug("-------------------------");

        running(testServer(3333), () -> {
            assertThat(
                    WS.url("http://localhost:3333/customers").get().get(1000).getStatus()
            ).isEqualTo(OK);
        });
    }
}
