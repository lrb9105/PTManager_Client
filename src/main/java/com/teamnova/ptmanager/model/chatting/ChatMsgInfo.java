package com.teamnova.ptmanager.model.chatting;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/** 채팅 메시지 정보를 담는 객체*/
public class ChatMsgInfo implements Serializable {
    @SerializedName("chattingMsgId")
    private String chattingMsgId;
    @SerializedName("chattingMemberId") //userId임!!!!
    private String chattingMemberId;
    @SerializedName("chattingMemberName") //userId임!!!!
    private String chattingMemberName;
    @SerializedName("chattingRoomId")
    private String chattingRoomId;
    @SerializedName("msg")
    private String msg;
    @SerializedName("creDatetime")
    private String creDatetime;

    public ChatMsgInfo(String chattingMsgId, String chattingMemberId, String chattingMemberName, String chattingRoomId, String msg, String creDatetime) {
        this.chattingMsgId = chattingMsgId;
        this.chattingMemberId = chattingMemberId;
        this.chattingMemberName = chattingMemberName;
        this.chattingRoomId = chattingRoomId;
        this.msg = msg;
        this.creDatetime = creDatetime;
    }

    public String getChattingMsgId() {
        return chattingMsgId;
    }

    public void setChattingMsgId(String chattingMsgId) {
        this.chattingMsgId = chattingMsgId;
    }

    public String getChattingMemberId() {
        return chattingMemberId;
    }

    public void setChattingMemberId(String chattingMemberId) {
        this.chattingMemberId = chattingMemberId;
    }

    public String getChattingMemberName() {
        return chattingMemberName;
    }

    public void setChattingMemberName(String chattingMemberName) {
        this.chattingMemberName = chattingMemberName;
    }

    public String getChattingRoomId() {
        return chattingRoomId;
    }

    public void setChattingRoomId(String chattingRoomId) {
        this.chattingRoomId = chattingRoomId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCreDatetime() {
        return creDatetime;
    }

    public void setCreDatetime(String creDatetime) {
        this.creDatetime = creDatetime;
    }
}
