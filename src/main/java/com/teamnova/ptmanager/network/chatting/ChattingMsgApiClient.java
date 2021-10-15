package com.teamnova.ptmanager.network.chatting;

import android.os.Handler;

import com.teamnova.ptmanager.model.chatting.ChatMsgInfo;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.ui.chatting.sync.SyncGetMsgListInfo;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;

public class ChattingMsgApiClient {
    // api 서버와 통신을 하기위한 통신객체
    private Retrofit retrofit;

    public ChattingMsgApiClient() {
        retrofit = RetrofitInstance.getRetroClient();
    }

    // 친구목록 가져오기
    public ArrayList<ChatMsgInfo> getMsgListInfo(String roomId, String userId){
        // 웹서비스 구현체 생성
        ChattingService service = retrofit.create(ChattingService.class);

        // http request 객체 생성
        Call<ArrayList<ChatMsgInfo>> call = service.getMsgListInfo(roomId, userId);

        // 서버에서 데이터를 가져오는 동기 함수의 쓰레드
        SyncGetMsgListInfo t = new SyncGetMsgListInfo(call);
        t.start();

        try {
            // 쓰레드에서 데이터를 저장할 때 main쓰레드는 중지를 시켜야 하므로 join()사용
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return t.getMsgList();
    }
}
