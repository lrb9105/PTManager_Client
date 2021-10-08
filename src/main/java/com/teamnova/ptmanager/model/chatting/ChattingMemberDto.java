package com.teamnova.ptmanager.model.chatting;

import com.google.gson.annotations.SerializedName;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.model.userInfo.UserInfoDto;

import java.io.Serializable;

/** 채팅멤버 정보를 담는 dto*/
public class ChattingMemberDto implements Serializable {
    @SerializedName("chattingMemberId")
    private String chattingMemberId;
    @SerializedName("chatRoomId")
    private String chatRoomId;
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

    public ChattingMemberDto(String chattingMemberId, String chatRoomId, String userId, String profileId, int userType, String loginId, String userName, String phoneNum, String branchOffice, String birth, int gender) {
        this.chattingMemberId = chattingMemberId;
        this.chatRoomId = chatRoomId;
        this.userId = userId;
        this.profileId = profileId;
        this.userType = userType;
        this.loginId = loginId;
        this.userName = userName;
        this.phoneNum = phoneNum;
        this.branchOffice = branchOffice;
        this.birth = birth;
        this.gender = gender;
    }

    public String getChattingMemberId() {
        return chattingMemberId;
    }

    public void setChattingMemberId(String chattingMemberId) {
        this.chattingMemberId = chattingMemberId;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
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

    // 트레이너의 채팅정보 생성
    public static ChattingMemberDto makeChatMemberInfo(UserInfoDto trainerInfo){
        String chattingMemberId = null;
        String chatRoomId = null;
        String userId = trainerInfo.getUserId();
        String profileId = trainerInfo.getProfileId();
        int userType = trainerInfo.getUserType();
        String loginId = trainerInfo.getLoginId();
        String userName = trainerInfo.getUserName();
        String phoneNum = trainerInfo.getPhoneNum();
        String branchOffice = trainerInfo.getBranchOffice();
        String birth = trainerInfo.getBirth();
        int gender = trainerInfo.getGender();

        return new ChattingMemberDto(chattingMemberId, chatRoomId, userId, profileId, userType, loginId, userName, phoneNum, branchOffice, birth, gender);
    }
    
    // 회원의 채팅정보 생성
    public static ChattingMemberDto makeChatMemberInfo(FriendInfoDto memberInfo){
        String chattingMemberId = null;
        String chatRoomId = null;
        String userId = memberInfo.getUserId();
        String profileId = memberInfo.getProfileId();
        int userType = memberInfo.getUserType();
        String loginId = memberInfo.getLoginId();
        String userName = memberInfo.getUserName();
        String phoneNum = memberInfo.getPhoneNum();
        String branchOffice = memberInfo.getBranchOffice();
        String birth = memberInfo.getBirth();
        int gender = memberInfo.getGender();

        return new ChattingMemberDto(chattingMemberId, chatRoomId, userId, profileId, userType, loginId, userName, phoneNum, branchOffice, birth, gender);
    }
}
