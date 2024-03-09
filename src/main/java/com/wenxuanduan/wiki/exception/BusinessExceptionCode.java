package com.wenxuanduan.wiki.exception;

public enum BusinessExceptionCode {

    USER_LOGIN_NAME_EXIST("LoginName already exists!"),
    LOGIN_USER_ERROR("LoginName doesn't exist or password incorrect"),
    ;

    private String desc;

    BusinessExceptionCode(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}