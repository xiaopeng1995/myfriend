package xiaopeng666.top.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xiaopeng666.top.entity.ResponseMessage;
import xiaopeng666.top.mq.Sender;
import xiaopeng666.top.redis.RedisUtil;
import xiaopeng666.top.utils.TulingApiProcess;

/**
 * 因为跨域问题需重写接口
 */
@CrossOrigin
@RestController
public class SendController extends BasicController {
    // logger
    private static final Logger logger = LoggerFactory.getLogger(SendController.class);
    @Autowired
    Sender sender;
    @Autowired
    RedisUtil redisUtil;

    /**
     * 发验证
     *
     * @param msg
     * @return
     */
    @RequestMapping(value = "/send/msg", method = RequestMethod.GET)
    public ResponseMessage sendMsg(@RequestParam String msg , String id) {
        logger.debug("/sendMsg : msg="+msg);
        try {
            sender.send(msg);
            if(id.equals("tuling"))
            {
                sender.send("机器人回复："+TulingApiProcess.getTulingResult(msg));
            }else {
                redisUtil.setString("name",10,"xiaopeng");
            }
            return successMessage(true);
        }catch (Exception e)
        {
            return failMessage(1001,"rabbitMQ通讯异常");
        }
    }

}
