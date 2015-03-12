package data;
import play.Logger;
import play.Play;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Set;

/**
 * Created by mbp-sm on 3/10/15.
 */
public class RedisCache {
    private static JedisPool POOL = null;
    public static String ENV_PREFIX = "";

    public static JedisPool getCache() {
        // TODO: move this prefix code out
        if (Play.application().isTest()) {
            Logger.info("MODE = TEST");
            ENV_PREFIX = Play.application().configuration().getString("jedis.key.prefix.test");
        } else if (Play.application().isDev()) {
            Logger.info("MODE = DEV");
        } else if (Play.application().isProd()) {
            Logger.info("MODE = PROD");
        } else {
            Logger.info("MODE = UNKNOWN");
        }
        Logger.info("Redis key prefix = " + ENV_PREFIX);

        if (POOL == null || POOL.isClosed()) {
            String host = Play.application().configuration().getString("jedis.host");
            Logger.info("Initializting cache:" + host);
            POOL = new JedisPool(new JedisPoolConfig(), host);
            //POOL = new JedisPool(new JedisPoolConfig(), "192.168.59.103");
        }
        return POOL;
    }

    public static void deleteKeys(String pattern) {
        final String DELETE_SCRIPT_IN_LUA = "local keys = redis.call('keys', '%s')" +
                "  for i,k in ipairs(keys) do" +
                "    local res = redis.call('del', k)" +
                "  end";
        Jedis jedis = null;
        try {
            jedis = getCache().getResource();

            if (jedis == null) {
                throw new Exception("Unable to get jedis resource!");
            }
            Set<String> names=jedis.keys(pattern);
            Logger.warn("Found to delete: " + names.size());

            jedis.eval(String.format(DELETE_SCRIPT_IN_LUA, pattern));
        } catch (Exception exc) {
            if (exc instanceof JedisConnectionException && jedis != null) {
                getCache().returnBrokenResource(jedis);
                jedis = null;
            }
            throw new RuntimeException("Unable to delete pattern " + pattern);
        } finally {
            if (jedis != null) {
                getCache().returnResource(jedis);
            }
        }
    }
}
