import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import model.User;
import org.apache.http.entity.ContentType;
import org.junit.*;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.api.http.ContentTypeOf;
import play.db.DB;
import play.libs.Json;
import play.libs.ws.WS;
import play.mvc.*;
import play.test.*;
import play.data.DynamicForm;
import play.data.validation.ValidationError;
import play.data.validation.Constraints.RequiredValidator;
import play.i18n.Lang;
import play.libs.F;
import play.libs.F.*;
import play.twirl.api.Content;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

import controllers.UserController;

import redis.clients.jedis.*;


/**
 * Simple (JUnit) tests that can call all parts of a play app.
 * If you are interested in mocking a whole application, see the wiki for more details.
 */
public class UserControllerTest extends WithApplication {
    //    FakeApplication fakeApp = Helpers.fakeApplication();
//
//    FakeApplication fakeAppWithGlobal = fakeApplication(new GlobalSettings() {
//        @Override
//        public void onStart(Application app) {
//            System.out.println("Starting FakeApplication");
//        }
//    });
//
//    FakeApplication fakeAppWithMemoryDb = fakeApplication(inMemoryDatabase("test"));
    static JedisPool pool = new JedisPool(new JedisPoolConfig(), "192.168.59.103");

    @BeforeClass
    public static void setUp() {
//        jedis = new Jedis("192.168.59.103");

//        Jedis jedis = pool.getResource();

    }

    @AfterClass
    public static void tearDown() {
        pool.destroy();

    }

    @Test
    public void simpleCheck() {
        int a = 1 + 1;
        assertThat(a).isEqualTo(2);
    }

    @Test
    public void testEndpoint_all() {
        Result result = UserController.all();
        assertThat(status(result)).isEqualTo(OK);
        assertThat(contentType(result)).isEqualTo("application/json");
        String body = contentAsString(result);
        JsonNode node = Json.parse(body);
        assertThat(node.isArray()).isTrue();
        assertThat(node.size()).isGreaterThan(1);
//        assertThat(body).contains("");
        Logger.info(body);
        //assertThat(contentAsString(result)).contains("return info.");
    }

    //    @Test
//    public void test_all_json() {
//        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
//            public void invoke(TestBrowser browser) {
//                browser.goTo("http://localhost:3333");
//                assertThat(browser.pageSource()).contains("Your new application is ready");
//            }
//        });
//    }
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
    public void testPUT() {
//        String body = "{\"name\":\"resty\", \"id\": 123}";
        String body = "{\"name\":\"resty\"}";
        JsonNode json = Json.parse(body);
        FakeRequest request = new FakeRequest(PUT, "/user").withHeader("Content-Type", ContentType.APPLICATION_JSON.toString()).withJsonBody(json);
        Result result = route(request);
        assertThat(status(result)).isEqualTo(CREATED);
//        String respBody = contentAsString(result);
        JsonNode node = Json.parse(contentAsString(result));
        User user = Json.fromJson(node, User.class);
        assertThat(user).isNotNull();
        Logger.info("test PUT response:" + user);
        assertThat(contentType(result)).isEqualTo("application/json");
        assertThat(user.name).isEqualTo("resty");
//        assertThat(user.id).isEqualTo(123);
//        dassertThat(respBody).contains("123");
//        assertThat(node).isInstanceOf(model.User.class);

    }

//    @Test
//    public void testRedisList() {
//        Jedis jedis = pool.getResource();
//        System.out.println("Testing Redis List");
//        jedis.lpush("tutorial-list", "Redis");
//        jedis.lpush("tutorial-list", "Mongodb");
//        jedis.lpush("tutorial-list", "Mysql");
//        // Get the stored data and print it
//        List<String> list = jedis.lrange("tutorial-list", 0, 10);
//        for (int i = 0; i < list.size(); i++) {
//            System.out.println("Testing Stored string in redis:: " + list.get(i));
//        }
//        pool.returnResource(jedis);
//    }

//    @Test
//    public void testRedisHash() {
//        Jedis jedis = pool.getResource();
//        System.out.println("Testing Redis Hash UserJson");
//        Integer id = ((Long)jedis.incr("userid")).intValue();
//        User u = new User(id, "test me " + id);
//        jedis.set("user:" + id, Json.toJson(u).toString());
//        String retStr= jedis.get("user:" + id);
//        Logger.debug("Test:str " + retStr) ;
//        User retUser = Json.fromJson(Json.parse(retStr),User.class);
//        Logger.debug("Test:obj " + Json.toJson(retUser)) ;
//
//        pool.returnResource(jedis);
//    }

}