package com.teamnova.ptmanager.model.userInfo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 회원정보를 저장하는 클래스
 * */
public class FriendInfoDto implements Serializable {
    @SerializedName("userId")
    private String userId;
    @SerializedName("profileId")
    private String profileId;
    @SerializedName("userType")
    private int userType;
    @SerializedName("loginId")
    private String loginId;
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
    @SerializedName("totalCnt")
    private int totalCnt;
    @SerializedName("usedCnt")
    private int usedCnt;
    @SerializedName("lectureName")
    private String lectureName;
    @SerializedName("lecturePassId")
    private String lecturePassId;
    @SerializedName("isConnected")
    private String isConnected;
    @SerializedName("trainerId")
    private String trainerId;
    private String check;

    public FriendInfoDto(String userId, String profileId, int userType, String loginId, String userName, String phoneNum, String branchOffice, String birth, int gender, int totalCnt, int usedCnt, String lectureName) {
        this.userId = userId;
        this.profileId = profileId;
        this.userType = userType;
        this.loginId = loginId;
        this.userName = userName;
        this.phoneNum = phoneNum;
        this.branchOffice = branchOffice;
        this.birth = birth;
        this.gender = gender;
        this.totalCnt = totalCnt;
        this.usedCnt = usedCnt;
        this.lectureName = lectureName;
    }

    public FriendInfoDto(String userId, String profileId, int userType, String loginId, String userName, String phoneNum, String branchOffice, String birth, int gender, int totalCnt, int usedCnt, String lectureName, String lecturePassId, String isConnected, String trainerId) {
        this.userId = userId;
        this.profileId = profileId;
        this.userType = userType;
        this.loginId = loginId;
        this.userName = userName;
        this.phoneNum = phoneNum;
        this.branchOffice = branchOffice;
        this.birth = birth;
        this.gender = gender;
        this.totalCnt = totalCnt;
        this.usedCnt = usedCnt;
        this.lectureName = lectureName;
        this.lecturePassId = lecturePassId;
        this.isConnected = isConnected;
        this.trainerId = trainerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
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

    public int getTotalCnt() {
        return totalCnt;
    }

    public void setTotalCnt(int totalCnt) {
        this.totalCnt = totalCnt;
    }

    public int getUsedCnt() {
        return usedCnt;
    }

    public void setUsedCnt(int usedCnt) {
        this.usedCnt = usedCnt;
    }

    public String getLectureName() {
        return lectureName;
    }

    public void setLectureName(String lectureName) {
        this.lectureName = lectureName;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public String getLecturePassId() {
        return lecturePassId;
    }

    public void setLecturePassId(String lecturePassId) {
        this.lecturePassId = lecturePassId;
    }

    public String getIsConnected() {
        return isConnected;
    }

    public void setIsConnected(String isConnected) {
        this.isConnected = isConnected;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(String trainerId) {
        this.trainerId = trainerId;
    }

    @Override
    public String toString() {
        return "FriendInfoDto{" +
                "userId='" + userId + '\'' +
                ", profileId='" + profileId + '\'' +
                '}';
    }
}
