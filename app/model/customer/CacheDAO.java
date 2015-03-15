package model.customer;

import data.RedisCache;
import org.h2.util.StringUtils;
import play.Logger;
import play.libs.Json;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by mbp-sm on 3/14/15.
 */
public class CacheDAO extends Customer {
    private static String UID_SEQ = RedisCache.ENV_PREFIX + "customer_seq";
    private static String UID_KEY = RedisCache.ENV_PREFIX + "customer_uid:";
    //

    public static Customer find(Long key) {
        Jedis jedis = RedisCache.getCache().getResource();
        Logger.debug("Requesting cache:" + UID_KEY + key);
        String foundData = jedis.get(UID_KEY + key);
        if (StringUtils.isNullOrEmpty(foundData)) {
            return null;
        }
        Logger.debug("Found:str " + foundData);
        Customer foundObj = Json.fromJson(Json.parse(foundData), Customer.class);
        Logger.debug("Found:obj " + Json.toJson(foundObj));
        RedisCache.getCache().returnResource(jedis);
        return foundObj;
    }

    public static List<Customer> findAll() {
        List<Customer> objList = new ArrayList<>();
        Jedis jedis = RedisCache.getCache().getResource();
        Logger.debug("Requesting finding all " + UID_KEY);
        Set<String> keys = jedis.keys(UID_KEY + "*");
        Logger.debug("Found: " + keys.size());

        for (String key : keys) {
            Logger.debug("key: " + key);
            String foundData = jedis.get(key);
            Customer foundObj = Json.fromJson(Json.parse(foundData), Customer.class);
            Logger.debug("Found:obj " + Json.toJson(foundObj));
            objList.add(foundObj);
        }
        RedisCache.getCache().returnResource(jedis);
        return objList;
    }

    public static List<Customer> match(Long uid, List criteria) {
        List<Customer> objList = new ArrayList<>();
        Jedis jedis = RedisCache.getCache().getResource();
        // Get the record to match values against
        String matchData = jedis.get(UID_KEY + uid);
        if (StringUtils.isNullOrEmpty(matchData)) {
            return null;
        }
        Customer matchObj = Json.fromJson(Json.parse(matchData), Customer.class);
        // Get all records for these set of keys
        Logger.debug("Requesting finding all " + UID_KEY);
        Set<String> keys = jedis.keys(UID_KEY + "*");
        //
        Logger.debug("Found: " + keys.size());
        //
        // Iterate through all records
        for (String key : keys) {
            Logger.debug("key: " + key);
            String foundData = jedis.get(key);
            Customer foundObj = Json.fromJson(Json.parse(foundData), Customer.class);
            Logger.debug("Found:obj " + Json.toJson(foundObj));
            // Skip the record to match
            if (foundObj.uid == matchObj.uid) {
                Logger.debug("Skipping " + foundObj.uid);
                continue;
            } else if (foundObj.companyName.equalsIgnoreCase(matchObj.companyName)) {
                objList.add(foundObj);
                Logger.debug("Added" + Json.toJson(foundObj));
            }
        }
        RedisCache.getCache().returnResource(jedis);
        return objList;
    }

    public static List<Customer> variadicMatch(Long uid, List criteria) {
        List<Customer> objList = new ArrayList<>();
        Jedis jedis = RedisCache.getCache().getResource();
        // Get the record to match values against
        String matchData = jedis.get(UID_KEY + uid);
        if (StringUtils.isNullOrEmpty(matchData)) {
            return null;
        }
        Customer matchObj = Json.fromJson(Json.parse(matchData), Customer.class);
        // Get all records for these set of keys
        Logger.debug("Requesting finding all " + UID_KEY);
        Set<String> keys = jedis.keys(UID_KEY + "*");
        //
        Logger.debug("Found: " + keys.size());
        //
        // Iterate through all records
//        List<Customer> newList criteria.stream().filter(c -> c.uid().equals(matchObj.uid)).collect(toList());
//        keys.stream().filter(c.)
        keys.forEach((key) -> {
            Logger.debug("key: " + key);
            String foundData = jedis.get(key);
            Customer foundObj = Json.fromJson(Json.parse(foundData), Customer.class);
            Logger.debug("Found:obj " + Json.toJson(foundObj));
            if (foundObj.uid != matchObj.uid) {
                Logger.debug("Skipping " + foundObj.uid);
                objList.add(foundObj);
            }
        });

        //TODO: iterate over criteria and filter for each one
        List<Customer> filteredList = objList.stream().filter(c -> c.companyName.equalsIgnoreCase(matchObj.companyName)).collect(toList());

        RedisCache.getCache().returnResource(jedis);
        return filteredList;
    }

    /**
     * Save current state to cache. If the uid is null a new uid will be generated from the
     * sequence and used to create a new record. If a record with the id already exists it
     * will be updated with the current state of all information.
     *
     * @return false if already existed, true if new record was created
     */
    public boolean save() {
        Jedis jedis = RedisCache.getCache().getResource();
        boolean createNew = !(jedis.exists(UID_KEY + this.uid));
        Logger.debug("Requesting cache:" + this.companyName);
        if (this.uid == null || createNew) {
            this.uid = (jedis.incr(UID_SEQ)).longValue();
            Logger.info("Created new id: " + this.uid);
        } else {
            Logger.info("Didn't create new id: already had: " + this.uid);
        }

        jedis.set(UID_KEY + this.uid, Json.toJson(this).toString());
        String retStr = jedis.get(UID_KEY + this.uid);
        Logger.debug("Test:str " + retStr); //TODO: remove
        Customer retObj = Json.fromJson(Json.parse(retStr), Customer.class);
        Logger.debug("Test:obj " + Json.toJson(retObj)); //TODO: remove
        RedisCache.getCache().returnResource(jedis);
        return createNew;
    }
}
