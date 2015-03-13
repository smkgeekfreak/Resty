import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Play;
import play.libs.*;
import play.mvc.*;

import java.util.*;

import play.mvc.Http;
import play.mvc.Result;
import redis.clients.jedis.*;

public class Global extends GlobalSettings {
//    public JedisPool pool ;

    public void onStart(Application app) {

        String prefix = "";
        if (Play.application().isTest()) {
            Logger.info("MODE = TEST");
            prefix = Play.application().configuration().getString("jedis.key.prefix.test");
        } else if (Play.application().isDev()) {
            Logger.info("MODE = DEV");
        } else if (Play.application().isProd()) {
            Logger.info("MODE = PROD");
        } else {
            Logger.info("MODE = UNKNOWN");
        }
        Logger.info("Redis key prefix = " + prefix);

//        pool = new JedisPool(new JedisPoolConfig(), "192.168.59.103");
//        Jedis jedis = pool.getResource();
////        Jedis jedis = new Jedis("192.168.59.103");
//        Logger.info("Global Redis Connection Pool started");
//        //check whether server is running or not
//        Logger.info("Gloabal Server is running: "+jedis.ping());
//        System.out.println("Server is running: " + jedis.ping());
//        pool.returnResource(jedis);
    }
    public void onStop(Application app) {
//        Logger.info("Global Redis Connection stop");
//        pool.destroy();
    }

//    @Override
//    public F.Promise<Result> onHandlerNotFound(Http.RequestHeader requestHeader) {
//        Logger.info(requestHeader.toString());
//        System.out.println("no handler found for... " + requestHeader.path() + " for " + requestHeader.uri());
//        return F.Promise.pure(controllers.EdgeProxy.serviceRequest());
//    }
    @Override
    public F.Promise<Result> onBadRequest(Http.RequestHeader requestHeader, String error) {
        Logger.info("Bad request:" + requestHeader.toString());
        return F.Promise.pure(Results.badRequest(requestHeader.toString()));
    }
}
