package com.teamnova.ptmanager.model.userInfo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 *  유저 정보를 저장하는 클래스
 * */
public class UserInfoDto  implements Serializable {
    @SerializedName("userType")
    private int userType;
    @SerializedName("loginId")
    private String loginId;
    @SerializedName("profileId")
    private String profileId;
    @SerializedName("pw")
    private String pw;
    @SerializedName("userName")
    private String userName;
    @SerializedName("phoneNum")
    private String phoneNum;
    @SerializedName("branchOffice")
    private String branchOffice;
    @SerializedName("birth")
    private String birth;
    @SerializedName("gender")
    private int gender;

    public UserInfoDto(int userType, String loginId, String pw, String userName, String phoneNum, String branchOffice, String birth, int gender) {
        this.userType = userType;
        this.loginId = loginId;
        this.pw = pw;
        this.userName = userName;
        this.phoneNum = phoneNum;
        this.branchOffice = branchOffice;
        this.birth = birth;
        this.gender = gender;
    }

    public UserInfoDto(int userType, String profileId, String loginId, String pw, String userName, String phoneNum, String branchOffice, String birth, int gender) {
        this.userType = userType;
        this.profileId = profileId;
        this.loginId = loginId;
        this.pw = pw;
        this.userName = userName;
        this.phoneNum = phoneNum;
        this.branchOffice = branchOffice;
        this.birth = birth;
        this.gender = gender;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getBranchOffice() {
        return branchOffice;
    }

    public void setBranchOffice(String branchOffice) {
        this.branchOffice = branchOffice;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    @Override
    public String toString() {
        return
                "loginId='" + loginId + '\n' +
                ", userName='" + userName + '\n' +
                ", phoneNum='" + phoneNum + '\n' ;
    }
}
