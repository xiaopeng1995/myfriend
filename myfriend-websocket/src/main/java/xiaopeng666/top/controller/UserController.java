package xiaopeng666.top.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xiaopeng666.top.entity.ResponseMessage;
import xiaopeng666.top.redis.RedisUtil;

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
    @Autowired
    RedisUtil redisUtil;

    /**
     * 发验证
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ResponseMessage sendMsg(@RequestParam String id) {
        logger.debug("/login : id=" + id);
        try {
            String token = UUID.randomUUID().toString().replace("-", "");
            redisUtil.setString(id, 7200, token);
            Map<String, String> data = new HashMap<>();
            data.put("token", token);
            return successMessage(data);
        } catch (Exception e) {
            return failMessage(1001, "rabbitMQ通讯异常");
        }
    }
}
