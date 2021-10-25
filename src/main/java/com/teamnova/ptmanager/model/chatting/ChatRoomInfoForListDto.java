package com.teamnova.ptmanager.model.chatting;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * 채팅방리스트에서 사용할 정보를 담는 dto
 * */
public class ChatRoomInfoForListDto implements Serializable {
    @SerializedName("chattingRoomId")
    private String chattingRoomId;
    @SerializedName("chattingRoomName")
    private String chattingRoomName;
    @SerializedName("latestMsg")
    private String latestMsg;
    @SerializedName("latestMsgTime")
    private String latestMsgTime;
    @SerializedName("userCount")
    private int userCount;


    public ChatRoomInfoForListDto(String chattingRoomId, String chattingRoomName, String latestMsg, String latestMsgTime, int userCount) {
        this.chattingRoomId = chattingRoomId;
        this.chattingRoomName = chattingRoomName;
        this.latestMsg = latestMsg;
        this.latestMsgTime = latestMsgTime;
        this.userCount = userCount;
    }

    public String getChattingRoomId() {
        return chattingRoomId;
    }

    public void setChattingRoomId(String chattingRoomId) {
        this.chattingRoomId = chattingRoomId;
    }

    public String getChattingRoomName() {
        return chattingRoomName;
    }

    public void setChattingRoomName(String chattingRoomName) {
        this.chattingRoomName = chattingRoomName;
    }

    public String getLatestMsg() {
        return latestMsg;
    }

    public void setLatestMsg(String latestMsg) {
        this.latestMsg = latestMsg;
    }

    public String getLatestMsgTime() {
        return latestMsgTime;
    }

    public void setLatestMsgTime(String latestMsgTime) {
        this.latestMsgTime = latestMsgTime;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatRoomInfoForListDto that = (ChatRoomInfoForListDto) o;
        return Objects.equals(chattingRoomId, that.chattingRoomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chattingRoomId);
    }
}
