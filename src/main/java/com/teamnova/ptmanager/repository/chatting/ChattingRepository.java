package com.teamnova.ptmanager.repository.chatting;

import android.os.Handler;

import com.teamnova.ptmanager.model.chatting.ChatMsgInfo;
import com.teamnova.ptmanager.network.changehistory.eyebody.EyeBodyApiClient;
import com.teamnova.ptmanager.network.chatting.ChattingApiClient;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 눈바디 저장소 객체
 * */
public class ChattingRepository {
    // 서버와 통신을 담당하는 객체
    private ChattingApiClient chattingApiClient;

    // 초기화
    public ChattingRepository() {
        chattingApiClient = new ChattingApiClient();
    }

    // 메시지 리스트 가져오기
    public ArrayList<ChatMsgInfo> getMsgListInfo(Handler handler, String roomId){
        return chattingApiClient.getMsgListInfo(handler, roomId);
    }

}
