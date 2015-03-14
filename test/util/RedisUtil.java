package util;

import play.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Set;

/**
 * Created by mbp-sm on 3/13/15.
 */
public class RedisUtil {

    public static JedisPool POOL;
    /**
     * Utility method for cleaning up test keys in redis cache
     *
     * @param pattern
     */
    public static void deleteKeys(String pattern) {
        final String DELETE_SCRIPT_IN_LUA = "local keys = redis.call('keys', '%s')" +
                "  for i,k in ipairs(keys) do" +
                "    local res = redis.call('del', k)" +
                "  end";
        Jedis jedis = null;
        try {
            jedis = POOL.getResource();

            if (jedis == null) {
                throw new Exception("Unable to get jedis resource!");
            }
            Set<String> names = jedis.keys(pattern);
            Logger.warn("Found to delete: " + names.size());

            jedis.eval(String.format(DELETE_SCRIPT_IN_LUA, pattern));
        } catch (Exception exc) {
            if (exc instanceof JedisConnectionException && jedis != null) {
                POOL.returnBrokenResource(jedis);
                jedis = null;
            }
            throw new RuntimeException("Unable to delete pattern " + pattern);
        } finally {
            if (jedis != null) {
                POOL.returnResource(jedis);
            }
        }
    }

}
