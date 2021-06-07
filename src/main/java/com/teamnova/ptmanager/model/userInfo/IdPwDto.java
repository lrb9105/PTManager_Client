package com.teamnova.ptmanager.model.userInfo;

import com.google.gson.annotations.SerializedName;

public class IdPwDto {
    @SerializedName("loginId")
    private String loginId;
    @SerializedName("pw")
    private String pw;

    public IdPwDto(String loginId, String pw) {
        this.loginId = loginId;
        this.pw = pw;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }
}
