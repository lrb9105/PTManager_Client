package com.teamnova.ptmanager.network.chatting;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.teamnova.ptmanager.model.chatting.ChatMsgInfo;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.friend.FriendService;
import com.teamnova.ptmanager.ui.chatting.sync.SyncGetChatRoomInfo;
import com.teamnova.ptmanager.ui.chatting.sync.SyncGetMsgListInfo;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChattingApiClient {
    // api 서버와 통신을 하기위한 통신객체
    private Retrofit retrofit;

    public ChattingApiClient() {
        retrofit = RetrofitInstance.getRetroClient();
    }

    // 친구목록 가져오기
    public ArrayList<ChatMsgInfo> getMsgListInfo(Handler handler, String roomId){
        return null;
    }
}
