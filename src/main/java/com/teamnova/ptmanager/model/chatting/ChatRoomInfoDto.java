package com.teamnova.ptmanager.model.chatting;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 채팅방 정보를 담는 dto
 * */
public class ChatRoomInfoDto implements Serializable {
    @SerializedName("chattingRoomId")
    private String chattingRoomId;
    @SerializedName("chattingRoomName")
    private String chattingRoomName;
    @SerializedName("chattingMemberList")
    private ArrayList<ChattingMemberDto> chattingMemberList;

    public ChatRoomInfoDto(String chattingRoomId, String chattingRoomName, ArrayList<ChattingMemberDto> chattingMemberList) {
        this.chattingRoomId = chattingRoomId;
        this.chattingRoomName = chattingRoomName;
        this.chattingMemberList = chattingMemberList;
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

    public ArrayList<ChattingMemberDto> getChattingMemberList() {
        return chattingMemberList;
    }

    public void setChattingMemberList(ArrayList<ChattingMemberDto> chattingMemberList) {
        this.chattingMemberList = chattingMemberList;
    }
}
