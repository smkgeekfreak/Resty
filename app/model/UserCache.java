package model;

import data.RedisCache;
import org.h2.util.StringUtils;
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

    /**
     * Save current state to cache. If the id is null a new id will be generated from the
     * sequence and used to create a new record. If a record with the id already exists it
     * will be update with the current state of all debugrmation.
     * @return true if already existed, false if new record was created
     */
    public boolean save() {
        Jedis jedis = RedisCache.getCache().getResource();
        //Integer newId = this.id;
        boolean createNew = !(jedis.exists(ID_KEY + this.id));
        Logger.debug("Requesting cache:" + this.name);
        if (this.id == null || createNew ) {
            this.id = (jedis.incr(ID_SEQ)).intValue();
            Logger.info("Created new id: " + this.id);
            //this.id = newId;
        } else {
            Logger.info("Didn't create new id: already had -" + this.id);
        }

        jedis.set(ID_KEY + this.id, Json.toJson(this).toString());
        String retStr = jedis.get(ID_KEY + this.id);
        Logger.debug("Test:str " + retStr);
        User retUser = Json.fromJson(Json.parse(retStr), User.class);
        Logger.debug("Test:obj " + Json.toJson(retUser));
        RedisCache.getCache().returnResource(jedis);
        return createNew;
    }

    public static User find (Integer key) {
        Jedis jedis = RedisCache.getCache().getResource();
        Logger.debug("Requesting cache:" + ID_KEY + key);
        String retStr = jedis.get(ID_KEY + key);
        if (StringUtils.isNullOrEmpty(retStr)){
            return null;
        }
        Logger.debug("Found:str " + retStr);
        User foundUser = Json.fromJson(Json.parse(retStr), User.class);
        Logger.debug("Found:obj " + Json.toJson(foundUser));
        RedisCache.getCache().returnResource(jedis);
        return foundUser;
    }

    public static User find (String name) {
        Jedis jedis = RedisCache.getCache().getResource();
        Logger.debug("Requesting cache:" + NAME_KEY + name);
        String retStr = jedis.get(NAME_KEY + name);
        Logger.debug("Found:str " + retStr);
        if (StringUtils.isNullOrEmpty(retStr)){
            return null;
        }
        User foundUser = Json.fromJson(Json.parse(retStr), User.class);
        Logger.debug("Found:obj " + Json.toJson(foundUser));
        RedisCache.getCache().returnResource(jedis);
        return foundUser;
    }

    public static List<User> findAll () {
        List<User> users = new ArrayList<>();
        Jedis jedis = RedisCache.getCache().getResource();
        Logger.debug("Requesting finding all " + ID_KEY);
        Set<String> names=jedis.keys(ID_KEY +"*");
        Logger.debug("Found: " + names.size());

       for (String key : names) {
            Logger.debug("key: " + key);
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
