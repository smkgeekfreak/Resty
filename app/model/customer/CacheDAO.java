package model.customer;

import data.RedisCache;
import org.h2.util.StringUtils;
import play.Logger;
import play.libs.Json;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import static java.util.stream.Collectors.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

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

    public static List<Customer> variadicMatch(Long uid, List<String> criteria) {
        List<Customer> objList = new ArrayList<>();
        Jedis jedis = RedisCache.getCache().getResource();
        // Get the record to match values against
        String matchData = jedis.get(UID_KEY + uid);
        if (StringUtils.isNullOrEmpty(matchData)) {
            return null;
        }
        Customer matchObj = Json.fromJson(Json.parse(matchData), Customer.class);
        // Get all records for these set of keys
//        Logger.debug("Requesting finding all " + UID_KEY);
        Set<String> keys = jedis.keys(UID_KEY + "*");
//        Logger.debug("Found: " + keys.size()); //TODO: remove
        // Iterate through all records and create a full list
        keys.forEach((key) -> {
//            Logger.debug("key: " + key);
            String foundData = jedis.get(key);
            Customer foundObj = Json.fromJson(Json.parse(foundData), Customer.class);
//            Logger.debug("Found:obj " + Json.toJson(foundObj));
            // Exclude the record being matched against the other records
            if (foundObj.uid != matchObj.uid) {
//                Logger.debug("Adding" + foundObj.uid);
                objList.add(foundObj);
            }
        });
        //
        // Iterate over criteria and filter
        List<Customer> filteredList = objList;
        for (String item : criteria) {
            filteredList = filter(filteredList,matchObj,item);
            Logger.debug("new list (" + filteredList.size()+ ")" +Json.toJson(filteredList));
        };

        RedisCache.getCache().returnResource(jedis);
        return filteredList;
    }

    /**
     * Filter a list based on specific criteria properites to compare with the provided match object's values.
     * @param list
     * @param matchObj
     * @param criteria
     * @return
     */
    private static List<Customer> filter(List<Customer> list, Customer matchObj, String criteria) {
        try {
            Object matchVal = matchObj.getClass().getDeclaredField(criteria).get(matchObj);
            Logger.info("exclusion critera : " + criteria + "=" + matchVal);
//            if (cust.getClass().getDeclaredField(criteria).get(cust).equals(matchVal)) {
//
//            } else {
//                Logger.info("skipped: " + Json.toJson(cust));
//            }
        } catch (IllegalAccessException iae) {

        } catch (NoSuchFieldException nsfe) {

        }
        switch (criteria) {
            case "companyName": {
                return list.stream().filter(c -> c.companyName.equalsIgnoreCase(matchObj.companyName)).collect(toList());
            }
            case "phoneNumber": {
                return list.stream().filter(c -> c.phoneNumber.equalsIgnoreCase(matchObj.phoneNumber)).collect(toList());
            }
            case "contactName": {
                return list.stream().filter(c -> c.contactName.equalsIgnoreCase(matchObj.contactName)).collect(toList());
            }
            case "customerRefNo": {
                return list.stream().filter(c -> c.customerRefNo.equalsIgnoreCase(matchObj.customerRefNo)).collect(toList());
            }
            default:
                return null;
        }
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
            Logger.debug("Created new id: " + this.uid);
        } else {
            Logger.debug("Didn't create new id: already had: " + this.uid);
        }

        jedis.set(UID_KEY + this.uid, Json.toJson(this).toString());
        String retStr = jedis.get(UID_KEY + this.uid);
        Logger.debug("Test:str " + retStr); //TODO: remove
        Customer retObj = Json.fromJson(Json.parse(retStr), Customer.class);
        Logger.debug("Test:obj " + Json.toJson(retObj)); //TODO: remove
        RedisCache.getCache().returnResource(jedis);
        return createNew;
    }

    public static boolean delete(Long key) {
        Jedis jedis = RedisCache.getCache().getResource();
        Logger.debug("Requesting delete :" + UID_KEY + key);
        String foundData = jedis.get(UID_KEY + key);
        if (StringUtils.isNullOrEmpty(foundData)) {
            return false;
        }
        jedis.del(UID_KEY + key);
        RedisCache.getCache().returnResource(jedis);
        return true;
    }
}
