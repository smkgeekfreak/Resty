import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.*;

import java.util.*;
import redis.clients.jedis.*;

public class Global extends GlobalSettings {
//    public JedisPool pool ;

    public void onStart(Application app) {

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
}
