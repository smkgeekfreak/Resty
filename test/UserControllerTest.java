import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import model.User;
import org.apache.http.entity.ContentType;
import org.junit.*;

import org.junit.experimental.categories.Category;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.api.Play;
import play.api.http.ContentTypeOf;
import play.libs.Json;
import play.libs.ws.WS;
import play.mvc.*;
import play.test.*;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

import controllers.UserController;

import redis.clients.jedis.*;
import data.RedisCache;
import redis.clients.jedis.exceptions.JedisConnectionException;


/**
 * Simple (JUnit) tests that can call all parts of a play app.
 * If you are interested in mocking a whole application, see the wiki for more details.
 */

@FixMethodOrder
public class UserControllerTest extends WithApplication {

    /**
     * Utility method for cleaning up test keys in redis cache
     *
     * @param pattern
     */
    private static void deleteKeys(String pattern) {
        final String DELETE_SCRIPT_IN_LUA = "local keys = redis.call('keys', '%s')" +
                "  for i,k in ipairs(keys) do" +
                "    local res = redis.call('del', k)" +
                "  end";
        Jedis jedis = null;
        try {
            jedis = pool.getResource();

            if (jedis == null) {
                throw new Exception("Unable to get jedis resource!");
            }
            Set<String> names = jedis.keys(pattern);
            Logger.warn("Found to delete: " + names.size());

            jedis.eval(String.format(DELETE_SCRIPT_IN_LUA, pattern));
        } catch (Exception exc) {
            if (exc instanceof JedisConnectionException && jedis != null) {
                pool.returnBrokenResource(jedis);
                jedis = null;
            }
            throw new RuntimeException("Unable to delete pattern " + pattern);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    static JedisPool pool;

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
        deleteKeys("test:*");
        //pool.getResource().flushDB();
        pool.destroy();
    }

    @Before
    public void setUp() {
        startPlay();
        pool = RedisCache.getCache();
    }

    @After
    public void tearDown() {
        stopPlay();
    }

    @Test
    public void testBadRoute() {
        Result result = route(fakeRequest(GET, "/users/all"));
        assertThat(result).isNull();
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
//        Result result = route(fakeRequest(PUT, "/user"));
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
        FakeRequest request = new FakeRequest(POST, "/user").withHeader("Content-Type", ContentType.APPLICATION_JSON.toString()).withJsonBody(json);
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
        FakeRequest request = new FakeRequest(POST, "/user").withHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
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
        FakeRequest request = new FakeRequest(POST, "/user").withHeader("Content-Type", ContentType.APPLICATION_JSON.toString()).withJsonBody(json);
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
        assertThat(header("Location", result)).isEqualTo("/user/" + user.id);
    }

    @Test
    public void testPOSTWithUnknownId() {
        Logger.debug("-----------------------------------------------");
        Logger.debug(new Object() {
        }.getClass().getEnclosingMethod().getName());
        Logger.debug("-----------------------------------------------");
        String body = "{\"name\":\"resty\", \"id\":5550}";
        JsonNode json = Json.parse(body);
        FakeRequest request = new FakeRequest(POST, "/user").withHeader("Content-Type", ContentType.APPLICATION_JSON.toString()).withJsonBody(json);
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
        assertThat(header("Location", result)).isEqualTo("/user/" + user.id);
    }

    @Test
    public void testPOSTWithExistingId() {
        Logger.debug("-----------------------------------------------");
        Logger.debug(new Object() {
        }.getClass().getEnclosingMethod().getName());
        Logger.debug("-----------------------------------------------");
        String body = "{\"name\":\"resty\", \"id\":1}";
        JsonNode json = Json.parse(body);
        FakeRequest request = new FakeRequest(POST, "/user").withHeader("Content-Type", ContentType.APPLICATION_JSON.toString()).withJsonBody(json);
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
        assertThat(header("Location", result)).isEqualTo("/user/" + user.id);
    }

    @Test
    public void testEndpoint_all() {
        Logger.debug("-----------------------------------------------");
        Logger.debug(new Object() {
        }.getClass().getEnclosingMethod().getName());
        Logger.debug("-----------------------------------------------");
        Result result = UserController.all();
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