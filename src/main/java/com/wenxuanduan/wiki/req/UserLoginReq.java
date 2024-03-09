package com.wenxuanduan.wiki.req;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public class UserLoginReq {

    @NotEmpty(message = "Login Name could not be NULL")
    private String loginName;

    @NotEmpty(message = "Password could not be NULL")
    // @Length(min = 6, max = 20, message = "Password:6~20 characters")
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,32}$", message = "Incorrect Password!")
    private String password;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", loginName=").append(loginName);
        sb.append(", password=").append(password);
        sb.append("]");
        return sb.toString();
    }
}