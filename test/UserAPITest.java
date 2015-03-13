import com.fasterxml.jackson.databind.JsonNode;
import controllers.UserController;
import data.RedisCache;
import model.User;
import org.apache.http.entity.ContentType;
import org.junit.*;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WS;
import play.mvc.Result;
import play.test.FakeRequest;
import play.test.WithApplication;
import util.Redis;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.*;
import static play.mvc.Http.Status.CREATED;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

/**
 * Created by mbp-sm on 3/13/15.
 */
@FixMethodOrder
public class UserAPITest extends WithApplication{

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
        Redis.deleteKeys("test:*");
        //POOL.getResource().flushDB();
        Redis.POOL.destroy();
    }

    @Before
    public void setUp() {
        startPlay();
        Redis.POOL = RedisCache.getCache();
    }

    @After
    public void tearDown() {
        stopPlay();
    }


    @Test
    public void testPOSTInvalidData() {
        Logger.debug("-----------------------------------------------");
        Logger.debug(new Object() {
        }.getClass().getEnclosingMethod().getName());
        Logger.debug("-----------------------------------------------");
        String body = "{\"nooonooo\":\"yesyes\"}";
        JsonNode json = Json.parse(body);
        FakeRequest request = new FakeRequest(POST, "/users").withHeader("Content-Type", ContentType.APPLICATION_JSON.toString()).withJsonBody(json);
        Result result = route(request);
        assertThat(status(result)).isEqualTo(BAD_REQUEST);
    }

//    @Test
//    public void testBadContentType() {
//        Logger.debug("-----------------------------------------------");
//        Logger.debug(new Object() {
//        }.getClass().getEnclosingMethod().getName());
//        Logger.debug("-----------------------------------------------");
//        FakeRequest request = new FakeRequest(POST, "/user").withHeader("Content-Type", ContentType.TEXT_HTML.toString());
//        Result result = route(request);
//        assertThat(status(result)).isEqualTo(OK);
//        JsonNode node = Json.parse(contentAsString(result));
//        assertThat(node).isNull();
//        Logger.debug("response:" + Json.stringify(node));
//        assertThat(contentType(result)).isEqualTo("application/json");
//        assertThat(!headers(result).containsKey("Location"));
//    }

    @Test
    @Ignore
    public void testPOSTNoData() {
        Logger.debug("-----------------------------------------------");
        Logger.debug(new Object() {
        }.getClass().getEnclosingMethod().getName());
        Logger.debug("-----------------------------------------------");
        FakeRequest request = new FakeRequest(POST, "/users").withHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
        Result result = route(request);
        assertThat(status(result)).isEqualTo(BAD_REQUEST);
        JsonNode node = Json.parse(contentAsString(result));
        assertThat(node).isNull();
        Logger.debug("response:" + Json.stringify(node));
        assertThat(contentType(result)).isEqualTo("application/json");
        assertThat(!headers(result).containsKey("Location"));
    }

    @Test
    public void testPOST() {
        Logger.debug("-----------------------------------------------");
        Logger.debug(new Object() {
        }.getClass().getEnclosingMethod().getName());
        Logger.debug("-----------------------------------------------");
        String body = "{\"name\":\"resty\"}";
        JsonNode json = Json.parse(body);
        FakeRequest request = new FakeRequest(POST, "/users").withHeader("Content-Type", ContentType.APPLICATION_JSON.toString()).withJsonBody(json);
        Result result = route(request);
        assertThat(status(result)).isEqualTo(CREATED);
        JsonNode node = Json.parse(contentAsString(result));
        User user = Json.fromJson(node, User.class);
        assertThat(user).isNotNull();
        Logger.debug("response:" + Json.stringify(node));
        assertThat(contentType(result)).isEqualTo("application/json");
        assertThat(user.name).isEqualTo("resty");
        assertThat(headers(result).containsKey("Location"));
        Logger.info("Location header =" + header("Location", result));
        assertThat(header("Location", result)).isEqualTo("/users/" + user.id);
    }

    @Test
    public void testPOSTWithUnknownId() {
        Logger.debug("-----------------------------------------------");
        Logger.debug(new Object() {
        }.getClass().getEnclosingMethod().getName());
        Logger.debug("-----------------------------------------------");
        String body = "{\"name\":\"resty\", \"id\":5550}";
        JsonNode json = Json.parse(body);
        FakeRequest request = new FakeRequest(POST, "/users").withHeader("Content-Type", ContentType.APPLICATION_JSON.toString()).withJsonBody(json);
        Result result = route(request);
        assertThat(status(result)).isEqualTo(CREATED);
        JsonNode node = Json.parse(contentAsString(result));
        User user = Json.fromJson(node, User.class);
        assertThat(user).isNotNull();
        Logger.debug("response:" + Json.stringify(node));
        assertThat(contentType(result)).isEqualTo("application/json");
        assertThat(user.id).isNotEqualTo(5550);
        assertThat(user.name).isEqualTo("resty");
        assertThat(headers(result).containsKey("Location"));
        Logger.info("Location header =" + header("Location", result));
        assertThat(header("Location", result)).isEqualTo("/users/" + user.id);
    }

    @Test
    public void testPOSTWithExistingId() {
        Logger.debug("-----------------------------------------------");
        Logger.debug(new Object() {
        }.getClass().getEnclosingMethod().getName());
        Logger.debug("-----------------------------------------------");
        String body = "{\"name\":\"resty\", \"id\":1}";
        JsonNode json = Json.parse(body);
        FakeRequest request = new FakeRequest(POST, "/users").withHeader("Content-Type", ContentType.APPLICATION_JSON.toString()).withJsonBody(json);
        Result result = route(request);
        assertThat(status(result)).isEqualTo(OK);
        JsonNode node = Json.parse(contentAsString(result));
        User user = Json.fromJson(node, User.class);
        assertThat(user).isNotNull();
        Logger.debug("response:" + Json.stringify(node));
        assertThat(contentType(result)).isEqualTo("application/json");
        assertThat(user.id).isEqualTo(1);
        assertThat(user.name).isEqualTo("resty");
        assertThat(headers(result).containsKey("Location"));
        Logger.info("Location header =" + header("Location", result));
        assertThat(header("Location", result)).isEqualTo("/users/" + user.id);
    }

    @Test
    public void testEndpoint_all() {
        Logger.debug("-----------------------------------------------");
        Logger.debug(new Object() {
        }.getClass().getEnclosingMethod().getName());
        Logger.debug("-----------------------------------------------");
        Result result = UserController.findAll();
        assertThat(status(result)).isEqualTo(OK);
        assertThat(contentType(result)).isEqualTo("application/json");
        String body = contentAsString(result);
        JsonNode node = Json.parse(body);
        assertThat(node.isArray()).isTrue();
        assertThat(node.size()).isGreaterThanOrEqualTo(1);
        Logger.info(body);
    }

    @Test
    public void testHttpPost() {
        Logger.debug("-----------------------------------------------");
        Logger.debug(new Object() {
        }.getClass().getEnclosingMethod().getName());
        Logger.debug("-----------------------------------------------");
        running(testServer(3333), () -> {
            assertThat(
                    WS.url("http://localhost:3333/users").get().get(5000).getStatus()
            ).isEqualTo(OK);
        });
    }

}