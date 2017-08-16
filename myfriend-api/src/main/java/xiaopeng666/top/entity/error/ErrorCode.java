package xiaopeng666.top.entity.error;


/**
 * Error Code
 */
@SuppressWarnings("unused")
public class ErrorCode {

    // authorize
    public static final Integer UNAUTHORIZED = 401;     // 未授权
    public static final Integer FORBIDDEN = 403;        // 禁止访问

    // validate
    public static final Integer CONFLICT = 409;
    public static final Integer INVALID = 422;          // 验证失败

    // other
    public static final Integer INTERNAL = 500;         // 500=服务器内部错误

    public static final Integer DATA_UPDATE_FAIL = 999; // 数据操作失败


    //公用错误码
    public static final Integer PARAMS_VALID = 1001;        // 1001=参数类型或格式错误
    public static final Integer UNAUTHORIZED_VALID = 1002;  // 1002=授权失效，请重新登录
    public static final Integer AUTH_VALID = 1003;          // 1003=无操作权限


    //用户相关
    public static final Integer USERNAME_VALID = 2001;      // 2001=用户名格式不正确
    public static final Integer MOBILE_VALID = 2002;        // 2002=手机号格式错误
    public static final Integer EMAIL_VALID = 2003;         // 2003=邮箱格式错误
    public static final Integer PASSWORD_VALID = 2004;      // 2004=密码格式不正确
    public static final Integer USERNAME_IS_EXISTS = 2005;  // 2005=注册失败，用户名已被注册
    public static final Integer MOBILE_IS_EXISTS = 2006;    // 2006=注册失败，手机号已存在
    public static final Integer EMAIL_IS_EXISTS = 2007;     // 2007=注册失败，邮箱已存在
    public static final Integer VALID_CODE_ERROR = 2009;    // 2009=验证码错误
    public static final Integer USER_NOT_EXISTS = 2010;     // 2010=登录失败，用户不存在
    public static final Integer PASSWORD_ERROR = 2011;      // 2011=登录失败，密码错误
    public static final Integer OLD_PASS_ERROR = 2012;      // 2012=用户旧密码错误
    public static final Integer PASSWORD_SAME = 2013;       // 2013=新密码与旧密码相同
    public static final Integer SMS_CODE_SEND_FAIL = 2014;  // 2014=短信验证码发送失败
    public static final Integer VALID_TIMEOUT = 2015;       // 2015=验证超时
    public static final Integer LOGIN_OR_PASSWORD_VALID = 2016;     // 2016=登录名或密码错误
    public static final Integer LOGIN_OR_EMAIL_NOT_BUILD = 2017;   // 2017=该邮箱未注册
    public static final Integer CODE_ERROR = 2019;          // 2019=激活码错误.或者失效
    public static final Integer SN_NOT_FOUND = 2020;          // 2020=SN not found
    public static final Integer ADD_EMAIL_IS_EXISTS = 2021;          // 2021=添加失败邮箱已存在
    public static final Integer ADD_USERNAME_IS_EXISTS = 2022;          // 2022=添加失败用户名已存在
    public static final Integer OPERATOR_USER_VALID = 4011;             //用户验证失败

    public static final Integer SYSTEM_AGENT_ALL_DISCONNECTED = 4008;   // agent全部断开连接

    //pong Game相关错误码
    public static final Integer GAME_STATUS_STARTED = 10001;            // 10001 游戏进行中
    public static final Integer GAME_IS_FULL = 10002;                   // 10002 游戏满员
    public static final Integer GAME_NOT_USER = 10003;                  // 10003 用户未参加游戏

    //服务相关类错误码
    public static final Integer SERVICE_TYPE_IS_NULL = 50001;           //服务类型为空
    public static final Integer SERVICE_PACKAGE_NOT_EXIST = 50002;      //所选服务包不存在
    public static final Integer SERVICE_STATUS_UPDATED_FAILED = 50003;  //更新用户所购买的服务状态失败
    public static final Integer SERVICE_EXPIRY_DATE_NOT_EXIST = 50004;  //所选服务包有效期不明确


    private ErrorCode() {
    }

}