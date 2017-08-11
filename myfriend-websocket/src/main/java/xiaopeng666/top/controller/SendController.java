package xiaopeng666.top.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xiaopeng666.top.entity.MsgInfo;
import xiaopeng666.top.entity.MsgType;
import xiaopeng666.top.entity.ResponseMessage;
import xiaopeng666.top.mq.Sender;
import xiaopeng666.top.redis.RedisUtilsBean;
import xiaopeng666.top.utils.JsonUtils;
import xiaopeng666.top.utils.TulingApiProcess;
import xiaopeng666.top.utils.redis.RedisUtils;

import java.util.Date;

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
    // redis
    private xiaopeng666.top.utils.redis.RedisUtils RedisUtils = new RedisUtilsBean().setInit();

    /**
     * 发验证
     *
     * @param msg
     * @return
     */
    @RequestMapping(value = "/send/msg", method = RequestMethod.GET)
    public ResponseMessage sendMsg(@RequestParam String msg, @RequestParam String token, @RequestParam String sendType) {
        logger.debug("/sendMsg : msg=" + msg + "token=" + token + "sendType=" + sendType);
        if (token == null || token.length() < 20) {
            return failMessage(1002, "用户验证失败请重新登录！");
        }
        try {
            String name = RedisUtils.get(token);
            if (name == null) {
                return failMessage(1002, "用户验证失败请重新登录");
            }
            MsgInfo msgInfo = new MsgInfo();
            msgInfo.setInfo(msg);
            msgInfo.setName(name);
            msgInfo.setTime(new Date().getTime());
            msgInfo.setType(MsgType.MSMINFO.value());
            switch (sendType) {
                case "tuling":
                    msgInfo.setRinfo(TulingApiProcess.getTulingResult(msg));
                    sender.send(JsonUtils.Mapper.writeValueAsString(msgInfo));
                    break;
                case "open":
                    sender.send(JsonUtils.Mapper.writeValueAsString(msgInfo));
                    break;
                default:
                    return failMessage(1003, "参数类型错误");
            }
            return successMessage(true);
        } catch (Exception e) {
            return failMessage(1001, "rabbitMQ通讯异常");
        }
    }


}
