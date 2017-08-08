package xiaopeng666.top.utils.redis;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @Description:redis工具类
 * @ClassName:
 * @date 2016年10月31日 上午11:25:06
 */

public class RedisUtils {
    private static final String AUTH = "";          // 密码(原始默认是没有密码)
    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);
    private static int MAX_ACTIVE = 1024;       // 最大连接数
    private static int MAX_IDLE = 200;          // 设置最大空闲数
    private static int MAX_WAIT = 10000;        // 最大连接时间
    private static int TIMEOUT = 10000;         // 超时时间
    private static boolean BORROW = true;         // 在borrow一个事例时是否提前进行validate操作
    private static JedisPool pool = null;

    /**
     * 获取连接
     */
    public static synchronized Jedis getJedis() {
        try {
            if (pool != null) {
                return pool.getResource();
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.info("连接池连接异常");
            return null;
        }

    }


    /**
     * @param @param  key
     * @param @param  seconds
     * @param @return
     * @return boolean 返回类型
     * @Description:设置失效时间
     */
    public static void disableTime(String key, int seconds) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.expire(key, seconds);

        } catch (Exception e) {
            logger.debug("设置失效失败.");
        } finally {
            getColse(jedis);
        }
    }

    /**
     * @param @param key
     * @param @param value
     * @return void 返回类型
     * @Description:存储key~value
     */

    public static boolean addValue(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            String code = jedis.set(key, value);
            if (code.equals("ok")) {
                return true;
            }
        } catch (Exception e) {
            logger.debug("插入数据有异常.");
            return false;
        } finally {
            getColse(jedis);
        }
        return false;
    }

    /**
     * @param @param  key
     * @param @return
     * @return boolean 返回类型
     * @Description:删除key
     */
    public static boolean delKey(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            Long code = jedis.del(key);
            if (code > 1) {
                return true;
            }
        } catch (Exception e) {
            logger.debug("删除key异常.");
            return false;
        } finally {
            getColse(jedis);
        }
        return false;
    }

    /**
     * @param @param jedis
     * @return void 返回类型
     * @Description: 关闭连接
     */

    public static void getColse(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    /**
     * Parse address string to a List of host:port String
     *
     * @param address Address String
     * @return List of host:port String
     */
    public static List<String> parseRedisAddress(String address, int defaultPort) {
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
    public static void insertWareKeys(String str, Map<String, String> map) {
        getJedis().hmset(str, map);
    }

    public static String getWareHouseKey(String agentId, int modbusId, String flag) {
        return agentId + ":" + modbusId + ":" + flag;
    }

    /**
     * 根据msgId查询设备上发上来的信息
     */
    public static Map<String, String> getWareValuesByKey(String key) {
        Jedis jedis = getJedis();
        if (jedis.exists(new String[]{key}) > 0) {
            Map<String, String> res = jedis.hgetAll(key);
            jedis.close();
            return res;
        }
        return null;
    }

    /**
     * 根据msgId删除设备上发上来的信息
     */
    public static boolean removeWareValuesByKey(String key) {
        Jedis jedis = getJedis();
        if (jedis.exists(new String[]{key}) > 0) {
            Long count = jedis.del(key);
            jedis.close();
            return count > 0;
        }
        return false;
    }

    /**
     * 根据msgId查询Quick Read 设备上发上来的信息
     */
    public static String getQuickReadValueByKey(String key) {
        Jedis jedis = getJedis();
        if (jedis.exists(key)) {
            String res = jedis.get(key);
            jedis.close();
            return res;
        }
        return null;
    }

    /**
     * 初始化线程池
     */
    public void init(PropertiesConfiguration propertiesConfiguration) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(MAX_ACTIVE);
        config.setMaxIdle(MAX_IDLE);
        config.setMaxWaitMillis(MAX_WAIT);
        config.setTestOnBorrow(BORROW);
        pool = new JedisPool(config, propertiesConfiguration.getString("redis.address"), propertiesConfiguration.getInt("redis.port"), TIMEOUT);
    }

    public void init(String ip, int p) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(MAX_ACTIVE);
        config.setMaxIdle(MAX_IDLE);
        config.setMaxWaitMillis(MAX_WAIT);
        config.setTestOnBorrow(BORROW);
        pool = new JedisPool(config, ip, p, TIMEOUT);
    }

    /**
     * <p>设置key value并制定这个键值的有效期</p>
     *
     * @param key
     * @param value
     * @param seconds 单位:秒
     * @return 成功返回OK 失败和异常返回null
     */
    public String setex(String key, String value, int seconds) {
        Jedis jedis = null;
        String res = null;
        try {
            jedis = pool.getResource();
            res = jedis.setex(key, seconds, value);
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            getColse(jedis);
        }
        return res;
    }
    public String setex(String key, String value) {
        Jedis jedis = null;
        String res = null;
        try {
            jedis = pool.getResource();
            res = jedis.set(key,value);
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            getColse(jedis);
        }
        return res;
    }
    /**
     * <p>通过key获取储存在redis中的value</p>
     * <p>并释放连接</p>
     *
     * @param key
     * @return 成功返回value 失败返回null
     */
    public String get(String key) {
        Jedis jedis = null;
        String value = null;
        try {
            jedis = pool.getResource();
            value = jedis.get(key);
        } catch (Exception e) {

            logger.error(e.getMessage());
        } finally {
            getColse(jedis);
        }
        return value;
    }

    /**
     * <p>判断key是否存在</p>
     *
     * @param key
     * @return true OR false
     */
    public Boolean exists(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.exists(key);
        } catch (Exception e) {

            logger.error(e.getMessage());
            return false;
        } finally {
            getColse(jedis);
        }
    }
}