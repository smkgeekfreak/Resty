import com.fasterxml.jackson.databind.JsonNode;
import controllers.UserController;
import model.User;
import org.apache.http.entity.ContentType;
import org.junit.*;

import play.Logger;
import play.libs.Json;
import play.libs.ws.WS;
import play.mvc.*;
import play.test.*;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

import data.RedisCache;
import util.*;

/**
 * Simple (JUnit) tests that can call all parts of a play app.
 * If you are interested in mocking a whole application, see the wiki for more details.
 */

@FixMethodOrder
public class UserControllerTest extends WithApplication {


    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
        RedisUtil.deleteKeys("test:user*");
        //POOL.getResource().flushDB();
        RedisUtil.POOL.destroy();
    }

    @Before
    public void setUp() {
        startPlay();
        RedisUtil.POOL = RedisCache.getCache();
    }

    @After
    public void tearDown() {
        stopPlay();
    }

    @Test
    public void testBadRoute() {
        Result result = route(fakeRequest(GET, "/users/invalid"));
        assertThat(status(result)).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void testInvalidInServer() {
        running(testServer(3333), () -> {
            assertThat(
                    WS.url("http://localhost:3333/invalid").get().get(5000).getStatus()
            ).isEqualTo(NOT_FOUND);
        });
    }

    @Test
    public void testInServer() {
        running(testServer(3333), () -> {
            assertThat(
                    WS.url("http://localhost:3333/info").get().get(5000).getStatus()
            ).isEqualTo(OK);
        });
    }

    //    @Test
//    public void testCreateRoute() {
//        Result result = route(fakeRequest(PUT, "/users"));
//        assertThat(result).isNotNull();
//        assertThat(result).isInstanceOf(model.User.class);
//    }
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
//        FakeRequest request = new FakeRequest(POST, "/users").withHeader("Content-Type", ContentType.TEXT_HTML.toString());
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
    public void testEndpointFindById() {
        Logger.debug("-----------------------------------------------");
        Logger.debug(new Object() {
        }.getClass().getEnclosingMethod().getName());
        Logger.debug("-----------------------------------------------");
        Result result = UserController.findById(1);
        assertThat(status(result)).isEqualTo(OK);
        assertThat(contentType(result)).isEqualTo("application/json");
        String body = contentAsString(result);
        JsonNode node = Json.parse(body);
        Logger.debug("Found:" + node);
    }

    @Test
    public void testEndpointFindByIdNotFound() {
        Logger.debug("-----------------------------------------------");
        Logger.debug(new Object() {
        }.getClass().getEnclosingMethod().getName());
        Logger.debug("-----------------------------------------------");
        Result result = UserController.findById(98765);
        assertThat(status(result)).isEqualTo(NOT_FOUND);
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