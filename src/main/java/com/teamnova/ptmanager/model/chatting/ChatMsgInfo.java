package com.teamnova.ptmanager.model.chatting;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/** 채팅 메시지 정보를 담는 객체*/
public class ChatMsgInfo implements Serializable {
    @SerializedName("chattingMsgId")
    private String chattingMsgId;
    @SerializedName("chattingMemberId") //userId임!!!!
    private String chattingMemberId;
    @SerializedName("chattingMemberName")
    private String chattingMemberName;
    @SerializedName("chattingRoomId")
    private String chattingRoomId;
    @SerializedName("msg")
    private String msg;
    @SerializedName("creDatetime")
    private String creDatetime;
    @SerializedName("notReadUserCount")
    private int notReadUserCount;
    @SerializedName("msgIdx")
    private int msgIdx;
    @SerializedName("savePath")
    private String savePath;
    @SerializedName("isDateVisible")
    private String isDateVisible;
    @SerializedName("isDbSelect")
    private String isDbSelect;
    private Bitmap saveImgBitmap;

    public ChatMsgInfo(String chattingMsgId, String chattingMemberId, String chattingMemberName, String chattingRoomId, String msg, String creDatetime, int notReadUserCount, int msgIdx, String savePath,String isDateVisible, String isDbSelect) {
        this.chattingMsgId = chattingMsgId;
        this.chattingMemberId = chattingMemberId;
        this.chattingMemberName = chattingMemberName;
        this.chattingRoomId = chattingRoomId;
        this.msg = msg;
        this.creDatetime = creDatetime;
        this.notReadUserCount = notReadUserCount;
        this.msgIdx = msgIdx;
        this.savePath = savePath;
        this.isDateVisible = isDateVisible;
        this.isDbSelect = isDbSelect;
    }

    public ChatMsgInfo(String chattingMsgId, String chattingMemberId, String chattingMemberName, String chattingRoomId, String msg, String creDatetime, int notReadUserCount, int msgIdx, Bitmap saveImgBitmap, String isDbSelect) {
        this.chattingMsgId = chattingMsgId;
        this.chattingMemberId = chattingMemberId;
        this.chattingMemberName = chattingMemberName;
        this.chattingRoomId = chattingRoomId;
        this.msg = msg;
        this.creDatetime = creDatetime;
        this.notReadUserCount = notReadUserCount;
        this.msgIdx = msgIdx;
        this.saveImgBitmap = saveImgBitmap;
        this.isDbSelect = isDbSelect;
    }

    public ChatMsgInfo(String chattingRoomId, int msgIdx) {
        this.chattingRoomId = chattingRoomId;
        this.msgIdx = msgIdx;
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

    public int getNotReadUserCount() {
        return notReadUserCount;
    }

    public void setNotReadUserCount(int notReadUserCount) {
        this.notReadUserCount = notReadUserCount;
    }

    public int getMsgIdx() {
        return msgIdx;
    }

    public void setMsgIdx(int msgIdx) {
        this.msgIdx = msgIdx;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public Bitmap getSaveImgBitmap() {
        return saveImgBitmap;
    }

    public void setSaveImgBitmap(Bitmap saveImgBitmap) {
        this.saveImgBitmap = saveImgBitmap;
    }

    public String getIsDbSelect() {
        return isDbSelect;
    }

    public void setIsDbSelect(String isDbSelect) {
        this.isDbSelect = isDbSelect;
    }

    public String getIsDateVisible() {
        return isDateVisible;
    }

    public void setIsDateVisible(String isDateVisible) {
        this.isDateVisible = isDateVisible;
    }

    /** 읽지 않은 메시지 갯수 계산 - 채팅방리스트에서 사용!
     * 입력: 특정 채팅방의 마지막 인덱스
     * 출력: 해당 메시지 객체의 인덱스 - 특정 채팅방의 마지막 인덱스
     * */
    public int calculateNotReadMsgCount(int lastMsgIdx){
        Log.e("읽지 않은 메시지 갯수 계산 1. 해당 메시지 객체의 인덱스 - 특정 채팅방의 마지막 인덱스","" + (this.msgIdx - lastMsgIdx));

        return this.msgIdx - lastMsgIdx;
    }
}
