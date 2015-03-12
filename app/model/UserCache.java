package model;

import data.RedisCache;
import play.Logger;
import play.libs.Json;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by mbp-sm on 3/10/15.
 */
public class UserCache extends User {
    static String ID_SEQ = RedisCache.ENV_PREFIX + "user_seq";
    static String ID_KEY = RedisCache.ENV_PREFIX +"user_id:";
    static String NAME_KEY = RedisCache.ENV_PREFIX +"user_name:";
    //

    public void save() {
        Jedis jedis = RedisCache.getCache().getResource();

        Logger.info("Requesting cache:" + this.name);
        if (this.id == null) {
            Integer id = (jedis.incr(ID_SEQ)).intValue();
            this.id = id;
        }

        jedis.set(ID_KEY + id, Json.toJson(this).toString());
        String retStr = jedis.get(ID_KEY + id);
        Logger.info("Test:str " + retStr);
        User retUser = Json.fromJson(Json.parse(retStr), User.class);
        Logger.info("Test:obj " + Json.toJson(retUser));
        RedisCache.getCache().returnResource(jedis);
    }

    public static User find (Integer key) {
        Jedis jedis = RedisCache.getCache().getResource();
        Logger.info("Requesting cache:" + ID_KEY+key);
        String retStr = jedis.get(ID_KEY + key);
        Logger.info("Found:str " + retStr);
        User foundUser = Json.fromJson(Json.parse(retStr), User.class);
        Logger.info("Found:obj " + Json.toJson(foundUser));
        RedisCache.getCache().returnResource(jedis);
        return foundUser;
    }

    public static User find (String name) {
        Jedis jedis = RedisCache.getCache().getResource();
        Logger.info("Requesting cache:" + NAME_KEY+name);
        String retStr = jedis.get(NAME_KEY + name);
        Logger.info("Found:str " + retStr);
        User foundUser = Json.fromJson(Json.parse(retStr), User.class);
        Logger.info("Found:obj " + Json.toJson(foundUser));
        RedisCache.getCache().returnResource(jedis);
        return foundUser;
    }

    public static List<User> findAll () {

        List<User> users = new ArrayList<>();
        Jedis jedis = RedisCache.getCache().getResource();
        Logger.info("Requesting finding all " + ID_KEY);
        Set<String> names=jedis.keys(ID_KEY +"*");
        Logger.info("Found: " + names.size());

//        Iterator<String> it = names.iterator();
       for (String key : names) {
//            String key = it.next();
            Logger.info("key: " + key);
            String retStr = jedis.get( key);
            Logger.debug("Found:str " + retStr);
            User foundUser = Json.fromJson(Json.parse(retStr), User.class);
            Logger.debug("Found:obj " + Json.toJson(foundUser));
            users.add(foundUser);
        }
        RedisCache.getCache().returnResource(jedis);
        return users;
    }
}
