package xiaopeng666.top.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redicache 工具类
 */
@SuppressWarnings("unchecked")
@Component
public class RedisUtil {
    @SuppressWarnings("rawtypes")
    @Autowired
    private RedisTemplate redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    /**
     * 批量删除对应的value
     *
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 批量删除key
     *
     * @param pattern
     */
    public void removePattern(final String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0)
            redisTemplate.delete(keys);
    }

    /**
     * 删除对应的value
     *
     * @param key
     */
    public void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public String getString(final String key) {
        logger.debug("调用取RedisUtil取" + key + "值");
        Object result;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            result = operations.get(key);
        } catch (Exception e) {
            result = null;
            logger.error("Redis读取异常:", e);
        }
        return (String) result;
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public Object get(final String key) {
        logger.debug("调用取RedisUtil取" + key + "值");
        Object result;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            result = operations.get(key);
        } catch (Exception e) {
            result = null;
            logger.error("Redis读取异常:", e);
        }
        return result;
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean setString(final String key, String value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean setString(final String key, int expireTime, String value) {
        logger.debug("Redis写入:" + key + "有效期:" + expireTime + "值:" + value);
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            logger.error("Redis写入异常:", e);
        }
        return result;
    }

    /**
     * 给key设置过期时间
     *
     * @param key
     * @return
     */
    public boolean setString(final String key, int expireTime) {
        logger.debug("Redis写入:" + key + "有效期:" + expireTime );
        boolean result = false;
        try {
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            logger.error("Redis写入异常:", e);
        }
        return result;
    }
}
