package model.customer;

import data.RedisCache;
import org.h2.util.StringUtils;
import play.Logger;
import play.libs.Json;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by mbp-sm on 3/14/15.
 */
public class CacheDAO extends Customer {
    private static String UID_SEQ = RedisCache.ENV_PREFIX + "customer_seq";
    private static String UID_KEY = RedisCache.ENV_PREFIX +"customer_uid:";
    //

    /**
     * Save current state to cache. If the uid is null a new uid will be generated from the
     * sequence and used to create a new record. If a record with the id already exists it
     * will be updated with the current state of all information.
     * @return true if already existed, false if new record was created
     */
    public boolean save() {
        Jedis jedis = RedisCache.getCache().getResource();
        //Integer newId = this.id;
        boolean createNew = !(jedis.exists(UID_KEY + this.uid));
        Logger.debug("Requesting cache:" + this.companyName);
        if (this.uid == null || createNew ) {
            this.uid = (jedis.incr(UID_SEQ)).longValue();
            Logger.info("Created new id: " + this.uid);
        } else {
            Logger.info("Didn't create new id: already had -" + this.uid);
        }

        jedis.set(UID_KEY + this.uid, Json.toJson(this).toString());
        String retStr = jedis.get(UID_KEY + this.uid);
        Logger.debug("Test:str " + retStr); //TODO: remove
        Customer retObj = Json.fromJson(Json.parse(retStr), Customer.class);
        Logger.debug("Test:obj " + Json.toJson(retObj)); //TODO: remove
        RedisCache.getCache().returnResource(jedis);
        return createNew;
    }

    public static Customer find (Integer key) {
        Jedis jedis = RedisCache.getCache().getResource();
        Logger.debug("Requesting cache:" + UID_KEY + key);
        String foundData = jedis.get(UID_KEY + key);
        if (StringUtils.isNullOrEmpty(foundData)){
            return null;
        }
        Logger.debug("Found:str " + foundData);
        Customer foundObj = Json.fromJson(Json.parse(foundData), Customer.class);
        Logger.debug("Found:obj " + Json.toJson(foundObj));
        RedisCache.getCache().returnResource(jedis);
        return foundObj;
    }

//    public static User find (String name) {
//        Jedis jedis = RedisCache.getCache().getResource();
//        Logger.debug("Requesting cache:" + NAME_KEY + name);
//        String retStr = jedis.get(NAME_KEY + name);
//        Logger.debug("Found:str " + retStr);
//        if (StringUtils.isNullOrEmpty(retStr)){
//            return null;
//        }
//        User foundUser = Json.fromJson(Json.parse(retStr), User.class);
//        Logger.debug("Found:obj " + Json.toJson(foundUser));
//        RedisCache.getCache().returnResource(jedis);
//        return foundUser;
//    }

    public static List<Customer> findAll () {
        List<Customer> objList = new ArrayList<>();
        Jedis jedis = RedisCache.getCache().getResource();
        Logger.debug("Requesting finding all " + UID_KEY);
        Set<String> keys=jedis.keys(UID_KEY +"*");
        Logger.debug("Found: " + keys.size());

       for (String key : keys) {
            Logger.debug("key: " + key);
            String foundData = jedis.get( key);
            Logger.debug("Found:str " + foundData); //TODO: remove
            Customer foundObj = Json.fromJson(Json.parse(foundData), Customer.class);
            Logger.debug("Found:obj " + Json.toJson(foundObj));
            objList.add(foundObj);
        }
        RedisCache.getCache().returnResource(jedis);
        return objList;
    }
}
