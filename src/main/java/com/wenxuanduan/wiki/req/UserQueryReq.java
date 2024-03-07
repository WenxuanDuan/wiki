package com.wenxuanduan.wiki.req;

public class UserQueryReq extends PageReq {

    private String LoginName;

    public String getLoginName() {
        return LoginName;
    }

    public void setLoginName(String loginName) {
        LoginName = loginName;
    }

    @Override
    public String toString() {
        return "UserQueryReq{" +
                "LoginName='" + LoginName + '\'' +
                "} " + super.toString();
    }
}
