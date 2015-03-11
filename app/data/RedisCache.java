package data;
import play.Logger;
import play.Play;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by mbp-sm on 3/10/15.
 */
public class RedisCache {
    static JedisPool pool = null;

    public static JedisPool getJedisCache() {
        if (pool == null || pool.isClosed()) {
            String host = Play.application().configuration().getString("jedis.host");
            Logger.info("Initializting cache:" + host);
            pool = new JedisPool(new JedisPoolConfig(), host);
            //pool = new JedisPool(new JedisPoolConfig(), "192.168.59.103");
        }
        return pool;
    }
}
