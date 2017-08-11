package xiaopeng666.top.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import xiaopeng666.top.entity.ResponseMessage;
import xiaopeng666.top.redis.RedisUtilsBean;
import xiaopeng666.top.utils.redis.RedisUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 因为跨域问题需重写接口
 */
@CrossOrigin
@RestController
public class UserController extends BasicController {
    // logger
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    // redis
    private RedisUtils RedisUtils = new RedisUtilsBean().setInit();

    /**
     * 发验证
     *
     * @param name
     * @return
     */
    @RequestMapping(value = "/login/visitor", method = RequestMethod.GET)
    public ResponseMessage login(@RequestParam String name) {
        logger.debug("/login : id=" + name);
        try {
            String token = UUID.randomUUID().toString().replace("-", "");
            Map<String, String> data = new HashMap<>();
            data.put("token", token);
            RedisUtils.setex(token,name,7200);
            return successMessage(data);
        } catch (Exception e) {

            return failMessage(1001, "rabbitMQ通讯异常");
        }
    }
}
