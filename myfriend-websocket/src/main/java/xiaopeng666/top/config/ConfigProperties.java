package xiaopeng666.top.config;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.stereotype.Repository;

/**
 * Created by xiaopeng on 2017/5/23.
 */
@Repository
public class ConfigProperties {
    /**
     * 获取配置信息application.properties
     *
     * @return 配置微信信息
     */
    public static PropertiesConfiguration getPropertiesConfiguration() {
        PropertiesConfiguration propertiesConfiguration = null;
        try {
            propertiesConfiguration = new PropertiesConfiguration("application.properties");

        } catch (Exception e) {
            e.printStackTrace();

        }
        return propertiesConfiguration;
    }
}
