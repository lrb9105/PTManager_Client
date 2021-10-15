package com.teamnova.ptmanager.ui.chatting.sync;

import com.teamnova.ptmanager.model.chatting.ChatRoomInfoDto;
import com.teamnova.ptmanager.model.chatting.ChatRoomInfoForListDto;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

/** 사용자가 포함된 채팅방 리스트 가져오기*/
public class SyncGetChatRoomList extends Thread{
    private Call<ArrayList<ChatRoomInfoForListDto>> call;
    //private Call<String> call;
    private ArrayList<ChatRoomInfoForListDto> chatRoomList;

    public SyncGetChatRoomList(Call<ArrayList<ChatRoomInfoForListDto>> call){
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
                chatRoomList = (ArrayList<ChatRoomInfoForListDto>)response.body();
            }
            //System.out.println("결과: " + (String)response.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ChatRoomInfoForListDto> getChatRoomList() {
        return chatRoomList;
    }
}

