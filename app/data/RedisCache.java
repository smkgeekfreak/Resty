package data;
import play.Logger;
import play.Play;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Set;

/**
 * Helper class to interact with Redis Pool.
 */
public class RedisCache {

    private static JedisPool POOL = null;
    /**
     * Prefix is used to segregate keys for easier
     * search and delete. Also used for distinguishing keys for
     * testing and development.
     */
    public static String ENV_PREFIX = "";

    public static JedisPool getCache() {
        // TODO: move this prefix code out
        if (Play.application().isTest()) {
            ENV_PREFIX = Play.application().configuration().getString("jedis.key.prefix.test");
        } else if (Play.application().isDev()) {
            Logger.debug("CACHE MODE = DEV");
        } else if (Play.application().isProd()) {
            Logger.debug("CACHE MODE = PROD");
        } else {
            Logger.debug("MODE = UNKNOWN");
        }
        Logger.debug("Redis key prefix = " + ENV_PREFIX);

        if (POOL == null || POOL.isClosed()) {
            String host = Play.application().configuration().getString("jedis.host");
            int port = Play.application().configuration().getInt("jedis.port");
            Logger.info("Initializting cache:" + host +":" + port);
            POOL = new JedisPool(new JedisPoolConfig(), host, port );
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
            Logger.debug("Found "+ names.size()+") to delete" );

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
