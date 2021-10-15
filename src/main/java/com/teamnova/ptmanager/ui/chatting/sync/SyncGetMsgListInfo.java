package com.teamnova.ptmanager.ui.chatting.sync;

import com.teamnova.ptmanager.model.chatting.ChatMsgInfo;
import com.teamnova.ptmanager.model.chatting.ChatRoomInfoDto;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

/** 특정 채팅방의 메시지리스트 가져오기*/
public class SyncGetMsgListInfo extends Thread{
    private Call<ArrayList<ChatMsgInfo>> call;
    //private Call<String> call;
    private ArrayList<ChatMsgInfo> msgList;

    public SyncGetMsgListInfo(Call<ArrayList<ChatMsgInfo>> call){
        this.call = call;
    }

    /*public SyncGetChatRoomInfo(Call<String> call){
        this.call = call;
    }*/

    @Override
    public void run() {
        try {
            Response response = call.execute();

            /** 채팅방 정보를 가져왔다면*/
            if(!response.toString().equals("[]")){
                msgList = (ArrayList<ChatMsgInfo>)response.body();
            }
            //System.out.println("결과: " + (String)response.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ChatMsgInfo> getMsgList() {
        return msgList;
    }
}

