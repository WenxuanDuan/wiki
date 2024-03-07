package com.wenxuanduan.wiki.req;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class UserSaveReq {
    private Long id;

    @NotNull(message = "Login Name could not be NULL")
    private String loginName;

    @NotNull(message = "Name could not be NULL")
    private String name;

    @NotNull(message = "Password could not be NULL")
    // @Length(min = 6, max = 20, message = "Password:6~20 characters")
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$", message = "Only contain digits and English letters with length 6~20")
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        sb.append(", id=").append(id);
        sb.append(", loginName=").append(loginName);
        sb.append(", name=").append(name);
        sb.append(", password=").append(password);
        sb.append("]");
        return sb.toString();
    }
}