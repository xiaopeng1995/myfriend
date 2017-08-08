package xiaopeng666.top.utils.redis;


import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.lang.StringUtils;
import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.RedissonClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Ware House Redis Storage
 */
public class WareHouseRedisStorage {

    // Main infrastructure class allows to get access to all Redisson objects on top of Redis server
    protected RedissonClient redisson;

    // A scalable thread-safe Redis client. Multiple threads may share one connection if they avoid
    // blocking and transactional operations such as BLPOP and MULTI/EXEC.
    private RedisClient lettuce;
    // A thread-safe connection to a redis server. Multiple threads may share one StatefulRedisConnection
    private StatefulRedisConnection<String, String> lettuceConn;

    public static String getStorageKey(String agentId, int modbusId, String flag) {
        return agentId + ":" + modbusId + ":" + flag;
    }

    public static String getWareHouseKey(String agentId, int modbusId, String flag) {
        return agentId + ":" + modbusId + ":" + flag;
    }

    public void init(AbstractConfiguration config) {
        List<String> address = parseRedisAddress(config.getString("redis.address"), 6379);
        int databaseNumber = config.getInt("redis.database", 0);
        String password = StringUtils.isNotEmpty(config.getString("redis.password")) ? config.getString("redis.password") + "@" : "";

        // lettuce
        RedisURI lettuceURI = RedisURI.create("redis://" + password + address.get(0) + "/" + databaseNumber);
        this.lettuce = RedisClient.create(lettuceURI);
        this.lettuceConn = this.lettuce.connect();

        // redisson
        Config redissonConfig = new Config();
        redissonConfig.useSingleServer()
                .setAddress(address.get(0))
                .setDatabase(databaseNumber)
                .setPassword(StringUtils.isNotEmpty(password) ? password : null);
        this.redisson = Redisson.create(redissonConfig);
    }

    public void destroy() {
        // shutdown this client and close all open connections
        if (this.lettuceConn != null) this.lettuceConn.close();
        if (this.lettuce != null) this.lettuce.shutdown();
        if (this.redisson != null) this.redisson.shutdown();
    }

    /**
     * Parse address string to a List of host:port String
     *
     * @param address Address String
     * @return List of host:port String
     */
    protected List<String> parseRedisAddress(String address, int defaultPort) {
        List<String> list = new ArrayList<>();
        String[] array = address.split(",");
        for (String s : array) {
            if (!s.contains(":"))
                s = s + ":" + defaultPort;
            list.add(s);
        }
        return list;
    }


    /**
     * 保存上发上来的设备信息
     */
    public void insertWareKeys(String str, String value) {
        this.lettuceConn.sync().set(str, value);
    }

    /**
     * 保存上发上来的设备信息
     */
    public void insertWareKeys(String str, Map<String, String> map) {
        this.lettuceConn.sync().hmset(str, map);
    }


    /**
     * 根据msgId查询设备上发上来的信息
     */
    public Map<String, String> getWareValuesByKey(String key) {
        if (this.lettuceConn.sync().exists(new String[]{key}) > 0) {
            Map<String, String> res = this.lettuceConn.sync().hgetall(key);
            return res;
        }
        return null;
    }


    /**
     * 根据msgId删除设备上发上来的信息
     */
    public boolean removeWareValuesByKey(String key) {
        if (this.lettuceConn.sync().exists(new String[]{key}) > 0) {
            Long count = this.lettuceConn.sync().del(key);
            this.lettuceConn.sync().close();
            return count > 0;
        }
        return false;
    }

}
