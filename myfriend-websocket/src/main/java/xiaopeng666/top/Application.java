package xiaopeng666.top;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import xiaopeng666.top.redis.RedisUtilsBean;
import xiaopeng666.top.utils.redis.RedisUtils;


/**
 * 启动类
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception {

        SpringApplication.run(Application.class, args);
    }
}
