package io.j1st.utils.http.resource;

import com.google.common.base.Optional;
import com.mongodb.MongoException;
import io.dropwizard.auth.Auth;
import io.j1st.utils.http.entity.ResultEntity;
import io.j1st.utils.http.entity.user.MailRequest;
import io.j1st.utils.http.entity.user.SignUpRequest;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xiaopeng666.top.entity.Event_Type;
import xiaopeng666.top.entity.User;
import xiaopeng666.top.entity.UserRole;
import xiaopeng666.top.entity.VerifyType;
import xiaopeng666.top.entity.error.ErrorCode;
import xiaopeng666.top.utils.EncryptionUtils;
import xiaopeng666.top.utils.RandomNumberUtils;
import xiaopeng666.top.utils.SendMailUtil;
import xiaopeng666.top.utils.event.EventLogUtils;
import xiaopeng666.top.utils.mongo.MongoStorage;
import xiaopeng666.top.utils.redis.RedisUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

/**
 * User related resource
 */
@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource extends AbstractResource {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(UserResource.class);
    RedisUtils radisUtils;

    public UserResource(MongoStorage mongo, RedisUtils radisUtils) {
        super(mongo);
        this.radisUtils = radisUtils;
    }


    /**
     * 验证用户注册信息是否存在
     *
     * @param lang
     * @param name
     * @param email
     * @param mobile
     * @param role
     * @return
     */
    @GET
    public ResultEntity exist(@HeaderParam("Accept-Language") @DefaultValue("zh") String lang,
                              @QueryParam("name") Optional<String> name,
                              @QueryParam("email") Optional<String> email,
                              @QueryParam("mobile") Optional<String> mobile,
                              @QueryParam("role") Optional<UserRole> role) {
        logger.debug("Process exist request with params: name {} email {} mobile {}", name.orNull(), email.orNull(), mobile.orNull());
        //取前两位代表
        if (lang.length() > 2) {
            lang = lang.substring(0, 2);
        }
        // validate
        if (!name.isPresent() && !email.isPresent() && !mobile.isPresent()) {
            return new ResultEntity(lang, ErrorCode.PARAMS_VALID);
        }

        // TODO: validate name mobile email format

        Map<String, Boolean> r = new HashMap<>();
        if (name.isPresent()) r.put("name", this.mongo.isUserNameExist(name.get()));
        if (role.isPresent()) {
            if (role.get().value() > 0) {
                if (email.isPresent()) r.put("email", this.mongo.isUserEmailExist(email.get(), role.get()));
                if (mobile.isPresent()) r.put("mobile", this.mongo.isUserMobileExist(mobile.get(), role.get()));
            } else {
                return new ResultEntity(lang, ErrorCode.PARAMS_VALID);
            }
        }

        return new ResultEntity<>(r);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public ResultEntity signUp(@HeaderParam("Accept-Language") @DefaultValue("zh") String lang,
                               @Valid SignUpRequest signUp, @Context HttpServletRequest req) {
        logger.debug("Process signUp request: {}", signUp);
        User u = new User();
        //判断是什么操作（type:1注册，2：找回密码）
        if (signUp.getType() == VerifyType.FINDPWD) {
            logger.debug("找回密码");
        } else {
            //判断该角色下的用户邮箱是否存在
            if (signUp.getEmail() != null && this.mongo.isUserEmailExist(signUp.getEmail(), signUp.getRole())) {
                return new ResultEntity(lang, ErrorCode.EMAIL_IS_EXISTS);
            }
            //判断该角色下的用户手机号是否存在
            if (signUp.getMobile() != null && this.mongo.isUserMobileExist(signUp.getMobile(), signUp.getRole())) {
                return new ResultEntity(lang, ErrorCode.MOBILE_IS_EXISTS);
            }
        }
        //取前两位代表
        if (lang.length() > 2) {
            lang = lang.substring(0, 2);
        }

        //有用户名
        if (signUp.getName() != null) {
            u.setName(signUp.getName());
        } else {
            switch (signUp.getRole().value()) {
                case 0:
                    u.setName("(A)" + signUp.getEmail());
                    break;
                case 1:
                    u.setName("(D)" + signUp.getEmail());
                    break;
                case 2:
                    u.setName("(P)" + signUp.getEmail());
                    break;
                case 3:
                    u.setName("(O)" + signUp.getEmail());
                    break;
                case 4:
                    u.setName("(O)" + signUp.getEmail());
                    break;
            }

        }

        //激活码效验
//        }
        if (!this.mongo.isSmsVerifyCodeValid(signUp.getEmail(), signUp.getType(), signUp.getVcode())) {
            return new ResultEntity(lang, ErrorCode.CODE_ERROR);
        }

        try {
            u.setPassword(EncryptionUtils.encryptPassword(signUp.getPassword()));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new InternalException("");
        }
        u.setEmail(signUp.getEmail());
        u.setMobile(signUp.getMobile());
        u.setRole(signUp.getRole());
        u.setUpdatedAt(new Date());

        try {

            if (signUp.getType() == VerifyType.FINDPWD) {
                this.mongo.updateUserPwd(u);
                return new ResultEntity<>(true);
            } else {
                u = this.mongo.insertUser(u);
            }
        } catch (MongoException e) {
            return new ResultEntity(lang, ErrorCode.INTERNAL);
        }
        //保存添加日志
        String description = u.getName() + " create " + u.getRole() + " account";
        EventLogUtils.insertInfoEventLog(mongo, u.getId(), Event_Type.ADD, description);
        return new ResultEntity<>(true);
    }

    /**
     * 发送激活码注册使用
     *
     * @param lang
     * @param info
     * @return
     */
    @Path("/email/send")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public ResultEntity creatuser(@HeaderParam("Accept-Language") @DefaultValue("zh") String lang,
                                  @Valid MailRequest info) {
        logger.debug("Ready to send the activation code");
        //取前两位代表
        if (lang.length() > 2) {
            lang = lang.substring(0, 2);
        }

        Map infoRe = new HashMap();
        //是否发送成功
        boolean b = false;
        //创建激活码
        try {
            String code = RandomNumberUtils.getRandom().toLowerCase();
            //储存激活码
            logger.debug("save vcode {} ,type:{}", info.getEmail(), info.getRole());


            //判断中英文
            if (info.getType() != 2) {//注册
                this.mongo.updateSmsVerifyCode(info.getEmail(), VerifyType.REGISTER, code);
                SendMailUtil.sendEmail("欢迎注册Myfriend",
                        info.getEmail(), "您的激活码是："+code);

                b = true;
            } else {//忘记密码
                this.mongo.updateSmsVerifyCode(info.getEmail(), VerifyType.FINDPWD, code);
                SendMailUtil.sendEmail("找回Myfriend密码",
                        info.getEmail(), "您的激活码是："+code);

            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        infoRe.put(info.getEmail(), b);
        return new ResultEntity<>(infoRe);
    }
//
//    /**
//     * 验证激活码找回密码
//     *
//     * @return
//     */
//    @Path("/email/pwd/validation")
//    @GET
//    @Consumes(MediaType.APPLICATION_JSON)
//    public ResultEntity validationPwd(@HeaderParam("Accept-Language") @DefaultValue("zh") String lang,
//                                      @QueryParam("email") Optional<String> email,
//                                      @QueryParam("role") Optional<String> role,
//                                      @QueryParam("vcode") Optional<String> vcode) {
//        String token = null;
//        //激活码效验
//        if (vcode != null) {
//            if (!this.mongo.isSmsVerifyCodeValid(email.get(), VerifyType.valueOf(getRoleStringToInt(role.get())), vcode.get())) {
//                return new ResultEntity(lang, ErrorCode.CODE_ERROR);
//            } else {
//                token = mongo.getUserTokenByMail(email.get(), UserRole.valueOf(role.get()));
//            }
//        } else {
//            return new ResultEntity(lang, ErrorCode.PARAMS_VALID);
//        }
//        if (token == null) {
//            return new ResultEntity(lang, ErrorCode.USER_NOT_EXISTS);
//        }
//        Map m = new HashMap();
//        m.put("token", token);
//        return new ResultEntity<>(m);
//    }
//
//    /**
//     * 发送激活码找回密码
//     *
//     * @param lang
//     * @return
//     */
//    @Path("/email/pwd/find")
//    @GET
//    @Consumes(MediaType.APPLICATION_JSON)
//    public ResultEntity findPwd(@HeaderParam("Accept-Language") @DefaultValue("zh") String lang,
//                                @QueryParam("email") Optional<String> email,
//                                @QueryParam("role") Optional<UserRole> role) {
//        logger.debug("Ready to send the activation code");
//        if (!this.mongo.isUserEmailExist(email.get(), role.get())) {
//            return new ResultEntity(lang, ErrorCode.USER_NOT_EXISTS);
//        }
//        //取前两位代表
//        if (lang.length() > 2) {
//            lang = lang.substring(0, 2);
//        }
//        //是否发送成功
//        boolean b = false;
//        //创建激活码
//        try {
//            String code = RandomNumberUtils.getRandom().toLowerCase();
//            //储存激活码
//            logger.debug("email:{}pwd/find role{}", email.get(), role.get().name());
//            this.mongo.updateSmsVerifyCode(email.get(), VerifyType.valueOf(getRoleStringToInt(role.get().name())), code);
//            //发邮件
//            //判断中英文
//            if (lang.startsWith("en")) {
//                SendMailUtil.findPasswordEmail("en", "Are recovered J1ST. IO (" + role.get().name() + ") account password", email.get(), code);
//            } else {
//                SendMailUtil.findPasswordEmail("zh", "正在找回J1ST.IO（" + role.get().name() + "）账户密码？", email.get(), code);
//            }
//
//            b = true;
//
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//        }
//        return new ResultEntity<>(b);
//    }
//
//    /**
//     * 查询邮箱状态
//     *
//     * @param lang
//     * @param infos
//     * @return 邮箱状态
//     */
//    @Path("/email/state")
//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
//    public ResultEntity findEmailState(@HeaderParam("Accept-Language") @DefaultValue("zh") String lang,
//                                       @Valid MailActivationCodeRequest infos) {
//        logger.debug("Ready to send the activation code");
//        //取前两位代表
//        if (lang.length() > 2) {
//            lang = lang.substring(0, 2);
//        }
//        List<Map> infosRe = new ArrayList<>();
//        for (MailRequest info : infos.getMailRequestList()) {
//            //格式错误
//            if (info.getEmail() == null || info.getRole() == null) {
//                return new ResultEntity(lang, ErrorCode.PARAMS_VALID);
//            }
//            Map infoRe = new HashMap();
//            //是否发送成功
//            int state = 0;
//            String email = info.getEmail();
//            String role = info.getRole();
//            //创建激活码
//            try {
//                if (this.mongo.isUserEmailExist(email, UserRole.valueOf(role))) {
//                    state = 2;
//                } else if (radisUtils.exists(email + role)) {
//                    state = 1;
//                }
//            } catch (Exception e) {
//                logger.error(e.getMessage());
//            }
//            infoRe.put(info.getEmail(), state);
//            infosRe.add(infoRe);
//        }
//
//        return new ResultEntity<>(infosRe);
//    }
//
//    /**
//     * 更新 用户信息
//     *
//     * @param lang    中英文
//     * @param user    验证用户
//     * @param profile 用户信息配置文件
//     * @return 是否更新成功
//     */
//    @PUT
//    @Consumes(MediaType.APPLICATION_JSON)
//    public ResultEntity UpdateUserProfile(@HeaderParam("Accept-Language") @DefaultValue("zh") String lang,
//                                          @Auth UserPrincipal user, @Context HttpServletRequest req,
//                                          @Valid UpdateProfileRequest profile) {
//        logger.debug("Process Update User Profile request: {}", profile);
//        User u = new User();
//        u.setId(new ObjectId(user.getId()));
//        if (user.getSubId() != null) {
//            u.setId(new ObjectId(user.getSubId()));
//        }
//        u.setName(profile.getName());
//        u.setEmail(profile.getEmail());
//        u.setMobile(profile.getMobile());
//        if (profile.getPassword() != null) {
//            try {
//                u.setPassword(EncryptionUtils.encryptPassword(profile.getPassword()));
//            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
//                throw new InternalException();
//            }
//        }
//        if (!StringUtils.isBlank(profile.getTimezone())) {
//            DateTimeZone timezone = DateTimeZone.forID(profile.getTimezone());
//            u.setTimezone(timezone);
//        }
//
//        u.setCompany(profile.getCompany());
//        Boolean result = this.mongo.updateUserProfile(u);
//
//        //保存添加日志
//
//        //保存添加日志
//        String description = u.getName() + " update " + profile.toString();
//        EventLogUtils.insertInfoEventLog(mongo, u.getId(), u.getSubId(), description, getSuper(req));
//        return new ResultEntity<>(result);
//    }
//
//    /**
//     * @param lang     中英文
//     * @param user     验证用户
//     * @param password 用户登录密码
//     * @return 密码 是否正确
//     */
//    @Path("/verifyPwd")
//    @GET
//    @Consumes(MediaType.APPLICATION_JSON)
//    public ResultEntity verifyUserPwd(@HeaderParam("Accept-Language") @DefaultValue("zh") String lang,
//                                      @Auth UserPrincipal user,
//                                      @QueryParam("password") String password) {
//        logger.debug("Process verify User Password Is Correct request: {}", password);
//        User u = new User();
//        u.setId(new ObjectId(user.getId()));
//        if (user.getSubId() != null) {
//            u.setId(new ObjectId(user.getSubId()));
//        }
//        if (password == null || password.length() < 6) {
//            return new ResultEntity(lang, ErrorCode.PARAMS_VALID);
//        }
//        try {
//            u.setPassword(EncryptionUtils.encryptPassword(password.toLowerCase()));
//        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
//            throw new InternalException();
//        }
//        Boolean result = this.mongo.isUserPasswordCorrect(u.getId(), u.getPassword());
//        return new ResultEntity<>(result);
//    }
//
//    private Integer getRoleStringToInt(String role) {
//        Integer v = null;
//        switch (role) {
//            case "DEVELOPER":
//                v = VerifyType.EMAIL_DEVELOPER_CODE.value();
//                break;
//            case "OPERATOR":
//                v = VerifyType.EMAIL_OPERRATOR_CODE.value();
//                break;
//            case "INSTALLER":
//                v = VerifyType.EMAIL_INSTALLER_CODE.value();
//                break;
//        }
//        return v;
//    }

}
