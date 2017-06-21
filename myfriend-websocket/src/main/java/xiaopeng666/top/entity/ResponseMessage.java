package xiaopeng666.top.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;

/**
 * HTTP REST 接口返回消息 返回消息的格式遵循JSend标准: http://labs.omniti.com/labs/jsend
 */
@SuppressWarnings("unused")
@Component
public class ResponseMessage {

    // 返回的状态 Key (JSON里）
    public static final String STATUS = "status";

    // 返回的失败信息 Key (JSON里）
    public static final String ERROR_MESSAGE = "message";

    // 返回的数据 Key (JSON里）
    public static final String DATA = "data";

    // 返回的数据 Key (JSON里）
    public static final String CODE = "code";

    // 返回的状态的常量
    public static final String RESPONSE_STATUS_SUCCESS = "success";
    public static final String RESPONSE_STATUS_FAIL = "fail";//业务执行失败
    public static final String RESPONSE_STATUS_ERROR = "error";//业务执行过程出现错误

    // 返回的状态 参考JSend标准
    @JsonProperty(STATUS)
    private final String status;

    // 返回的错误信息 (Error的情况下)
    @JsonProperty(ERROR_MESSAGE)
    private final String message;

    // 返回的数据 (Fail的情况下是错误的字段和信息)
    @JsonProperty(DATA)
    private final Object data;

    // 返回的错误码
    @JsonProperty(CODE)
    private final Integer code;

    /**
     * 构造器
     */
    public ResponseMessage() {
        this.status = null;
        this.message = null;
        this.data = null;
        this.code = null;
    }

    /**
     * 构造器
     *
     * @param status  返回的状态
     * @param message 返回的错误信息
     * @param data    返回的数据
     * @param code    返回的错误码
     */
    public ResponseMessage(String status, String message, Object data, Integer code) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public Integer getCode() {
        return code;
    }
}