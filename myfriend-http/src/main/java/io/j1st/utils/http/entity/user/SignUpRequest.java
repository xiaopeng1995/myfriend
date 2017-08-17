package io.j1st.utils.http.entity.user;


import xiaopeng666.top.entity.UserRole;
import xiaopeng666.top.entity.VerifyType;

import javax.validation.constraints.NotNull;

/**
 * SignUp Request
 */
public class SignUpRequest {
    @NotNull
    private String name;
    @NotNull
    private String vcode;//激活码
    @NotNull
    private String password;
    @NotNull
    private String email;
    private String mobile;
    @NotNull
    private UserRole role;

    private String timezone;

    private String pid;

    @NotNull
    private VerifyType type;// 1 : 注册 2：找回密码

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password.toLowerCase();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public String toString() {
        return "SignUpRequest{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", role=" + role +
                ", vcode=" + vcode +
                '}';
    }

    public String getVcode() {
        return vcode;
    }

    public void setVcode(String vcode) {
        this.vcode = vcode;
    }

    public VerifyType getType() {
        return type;
    }

    public void setType(VerifyType type) {
        this.type = type;
    }
}
