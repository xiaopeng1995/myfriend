package io.j1st.utils.http.entity.user;

import xiaopeng666.top.entity.UserRole;

/**
 * mail request
 */
public class MailRequest {

    private String email;
    private UserRole role;
    private Integer type;//1注册 2找回密码

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}

