package xiaopeng666.top.redis;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import xiaopeng666.top.utils.redis.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Created by xiaopeng on 2017/8/8.
 */
@Component
public class RedisUtilsBean implements  RedisDao{
    private static final Logger logger = LoggerFactory.getLogger(RedisUtilsBean.class);
    //初始化redis
    @Override
    public RedisUtils setInit() {
        RedisUtils radisUtils = new RedisUtils();
        PropertiesConfiguration propertiesConfiguration = null;
        try {
            propertiesConfiguration = new PropertiesConfiguration("application.properties");
        }catch (Exception e)
        {
            logger.error("读取配置文件出错");
        }
        radisUtils.init(propertiesConfiguration);
        return radisUtils;
    }
}
