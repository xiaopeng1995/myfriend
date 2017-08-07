package xiaopeng666.top.controller;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import xiaopeng666.top.entity.ResponseMessage;
import xiaopeng666.top.entity.exception.WebsiteHttpException;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller 基础类
 */
@RequestMapping("/app")
@Controller
class BasicController {

    // SLF4J日志
    private static final Logger logger = LoggerFactory.getLogger(BasicController.class);

    /**
     * 系统层-异常处理
     *
     * @return ResponseMessage
     */
    @ExceptionHandler
    public ResponseMessage exception(Exception ex) {
        if (ex instanceof WebsiteHttpException) {
            return new ResponseMessage(ResponseMessage.RESPONSE_STATUS_ERROR, "HTTP请求出现错误", ExceptionUtils.getMessage(ex), 30000);
        } else {
            return new ResponseMessage(ResponseMessage.RESPONSE_STATUS_ERROR, "内部错误", ExceptionUtils.getMessage(ex), -10000);
        }
    }

    // 无明确失败原因的标识为服务器内部错误，无返回数据
    public ResponseMessage failMessage() {
        return failMessage(-10000);
    }

    // 无明确失败原因的标识为服务器内部错误，有返回数据
    public ResponseMessage failMessage(Object object) {
        return failMessage(-10000, object);
    }

    // 无失败附加
    public ResponseMessage failMessage(int code) {
        return failMessage(code, null);
    }

    // 验证类失败
    public ResponseMessage illegalMessage(String field, String message) {
        Map<String, String> illegal = new HashMap<>();
        illegal.put("field", field);
        illegal.put("illegal", message);
        return failMessage(20000, illegal);
    }

    /**
     * 业务层-执行结果成功
     *
     * @param data Object data
     * @return ResponseMessage
     */
    public ResponseMessage successMessage(Object data) {
        return new ResponseMessage(ResponseMessage.RESPONSE_STATUS_SUCCESS, null, data, null);
    }

    /**
     * 验证框架-返回验证结果
     */
    public Map<String, Boolean> validMessageMessage(Boolean valid) {
        Map<String, Boolean> ret = new HashMap<>();
        ret.put("valid", valid);
        return ret;
    }

    /**
     * 业务层-执行结果失败
     *
     * @param code 失败码
     * @param data 失败后返回内容
     * @return ResponseMessage
     */
    public ResponseMessage failMessage(int code, Object data) {
        return new ResponseMessage(ResponseMessage.RESPONSE_STATUS_FAIL, "10000", data, code);
    }


}
