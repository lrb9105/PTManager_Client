package com.teamnova.ptmanager.model.userInfo;

import com.google.gson.annotations.SerializedName;

/**
 *  유저 정보를 저장하는 클래스, USER_ID 포함
 * */
public class UserInfoDtoWithUserId extends UserInfoDto{
    @SerializedName("userId")
    private String userId;

    public UserInfoDtoWithUserId(int userType, String loginId, String pw, String userName, String phoneNum, String branchOffice, String birth, int gender) {
        super(userType, loginId, pw, userName, phoneNum, branchOffice, birth, gender);
    }

    public UserInfoDtoWithUserId(int userType, String loginId, String pw, String userName, String phoneNum, String branchOffice, String birth, int gender, String userId) {
        super(userType, loginId, pw, userName, phoneNum, branchOffice, birth, gender);
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
