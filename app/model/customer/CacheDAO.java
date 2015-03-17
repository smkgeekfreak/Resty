package model.customer;

import data.RedisCache;
import org.h2.util.StringUtils;
import play.Logger;
import play.libs.Json;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

/**
 * Data access layer implementation for Customer based on a
 * Redis Cache data store.
 */
public class CacheDAO extends Customer {
    /**
     * Key used for generating new Customer uid's
     */
    private static String UID_SEQ = RedisCache.ENV_PREFIX + "customer_seq";
    /**
     * Key prefix used to identify customer data indexed by uid
     */
    private static String UID_KEY = RedisCache.ENV_PREFIX + "customer_uid:";

    /**
     * Find a customer based on
     * @param uid
     * @return
     */
    public static Customer find(Long uid) {
        Jedis jedis = RedisCache.getCache().getResource();
        Logger.debug("Requesting cache:" + UID_KEY + uid);
        String foundData = jedis.get(UID_KEY + uid);
        if (StringUtils.isNullOrEmpty(foundData)) {
            return null;
        }
        Customer foundObj = Json.fromJson(Json.parse(foundData), Customer.class);
        RedisCache.getCache().returnResource(jedis);
        return foundObj;
    }

    /**
     *
     * @return
     */
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

    /**
     * Match Customer records based on a variable list of criteria for comparison against a given Customer record.
     * @param uid Identifies the Customer to compare criteria against
     * @param criteria List of criteria fields as strings. Currently supports any combination of "companyName", "phoneNumber", "contactName", "customerRefNo"
     * @return
     */
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
        Set<String> keys = jedis.keys(UID_KEY + "*");
        // Iterate through all records and create a full list
        keys.forEach((key) -> {
            String foundData = jedis.get(key);
            Customer foundObj = Json.fromJson(Json.parse(foundData), Customer.class);
            // Exclude the record being matched against the other records
            if (foundObj.uid != matchObj.uid) {
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
     * Filter a list based on specific criteria properties to compare with the provided match object's values.
     * Currently supports
     * @param list List of customers to compare against the "match" Customer
     * @param matchObj Customer to match criteria from other Customers against
     * @param criteria List of criteria strings. Currently supports any combination of "companyName", "phoneNumber", "contactName", "customerRefNo"
     * @return
     */
    private static List<Customer> filter(List<Customer> list, Customer matchObj, String criteria) {
        // TODO: Can this logic be moved from a case statement to a dynamic model based on the criteria/field name convention?
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
     * will be updated with the current state of all information. This method is not static and
     * depends on an existing instance of a Customer model for the values to save.
     *
     * @return false if already existed, true if new record was created
     */
    public boolean save() {
        Jedis jedis = RedisCache.getCache().getResource();
        boolean createNew = !(jedis.exists(UID_KEY + this.uid));
        Logger.debug("Requesting save:" + this.companyName);
        if (this.uid == null || createNew) {
            this.uid = jedis.incr(UID_SEQ);
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

    /**
     * Delete a customer by uid from the Cache
     * @param uid Unique identify for a customer
     * @return true if deleted, false if not found
     */
    public static boolean delete(Long uid) {
        Jedis jedis = RedisCache.getCache().getResource();
        boolean exists =  jedis.exists(UID_KEY + uid);
        Logger.debug("Requesting delete :" + UID_KEY + uid);
        if (exists)
            jedis.del(UID_KEY + uid);

        RedisCache.getCache().returnResource(jedis);
        return exists;
    }
}
