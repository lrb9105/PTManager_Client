package com.teamnova.ptmanager.repository.chatting;

import android.os.Handler;

import com.teamnova.ptmanager.model.chatting.ChatMsgInfo;
import com.teamnova.ptmanager.network.chatting.ChattingApiClient;
import com.teamnova.ptmanager.network.chatting.ChattingMsgApiClient;

import java.util.ArrayList;

/**
 * 눈바디 저장소 객체
 * */
public class ChattingMsgRepository {
    // 서버와 통신을 담당하는 객체
    private ChattingMsgApiClient chattingMsgApiClient;

    // 초기화
    public ChattingMsgRepository() {
        chattingMsgApiClient = new ChattingMsgApiClient();
    }

    // 메시지 리스트 가져오기
    public ArrayList<ChatMsgInfo> getMsgListInfo(String roomId, String userId, int limit, int pageNo){
        return chattingMsgApiClient.getMsgListInfo(roomId, userId, limit, pageNo);
    }

}
