package model;

import data.RedisCache;
import play.Application;
import play.Logger;
import play.libs.Json;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by mbp-sm on 3/10/15.
 */
public class UserCache extends User {
    //
    public void save() {
        Jedis jedis = RedisCache.getJedisCache().getResource();

        Logger.info("Requesting cache:" + this.name);
        if (this.id == null) {
            Integer id = (jedis.incr("user")).intValue();
            this.id = id;
        }
        jedis.set("user:" + id, Json.toJson((User)this).toString());
        String retStr = jedis.get("user:" + id);
        Logger.info("Test:str " + retStr);
        User retUser = Json.fromJson(Json.parse(retStr), User.class);
        Logger.info("Test:obj " + Json.toJson(retUser));
        RedisCache.getJedisCache().returnResource(jedis);
    }

    public static User find (Integer id) {
        Jedis jedis = RedisCache.getJedisCache().getResource();
        Logger.info("Requesting cache:" + "user:"+id);
        String retStr = jedis.get("user:" + id);
        Logger.info("Found:str " + retStr);
        User foundUser = Json.fromJson(Json.parse(retStr), User.class);
        Logger.info("Found:obj " + Json.toJson(foundUser));
        RedisCache.getJedisCache().returnResource(jedis);
        return foundUser;
    }

    public static List<User> findAll () {

        List<User> users = new ArrayList<>();
        Jedis jedis = RedisCache.getJedisCache().getResource();
        Logger.info("Requesting finding all \"user:\"");
        Set<String> names=jedis.keys("user:*");
        Logger.info("Found: " + names.size());

//        Iterator<String> it = names.iterator();
       for (String key : names) {
//            String key = it.next();
            Logger.debug("key: " + key);
            String retStr = jedis.get( key);
            Logger.debug("Found:str " + retStr);
            User foundUser = Json.fromJson(Json.parse(retStr), User.class);
            Logger.debug("Found:obj " + Json.toJson(foundUser));
            users.add(foundUser);
        }
        RedisCache.getJedisCache().returnResource(jedis);
        return users;
    }
}
