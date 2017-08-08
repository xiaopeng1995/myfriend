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

    /**
     * 发验证
     *
     * @param msg
     * @return
     */
    @RequestMapping(value = "/send/msg", method = RequestMethod.GET)
    public ResponseMessage sendMsg(@RequestParam String msg ,@RequestParam String token, @RequestParam String sendType) {
        logger.debug("/sendMsg : msg="+msg);
        if(token==null||token.length()<20)
        {
            return failMessage(1002,"用户验证失败请重新登录！");
        }
        try {

            if(sendType.equals("tuling"))
            {
                sender.send(msg);
                sender.send("机器人回复："+TulingApiProcess.getTulingResult(msg));
            }else if(sendType.equals("open")) {
                sender.send(msg);
            }else {
                return failMessage(1003,"参数类型错误");
            }
            return successMessage(true);
        }catch (Exception e)
        {
            return failMessage(1001,"rabbitMQ通讯异常");
        }
    }

}
