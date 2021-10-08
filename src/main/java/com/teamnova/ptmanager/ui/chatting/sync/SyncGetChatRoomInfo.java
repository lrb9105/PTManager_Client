package com.teamnova.ptmanager.ui.chatting.sync;

import com.teamnova.ptmanager.model.chatting.ChatRoomInfoDto;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class SyncGetChatRoomInfo extends Thread{
    private Call<ChatRoomInfoDto> call;
    //private Call<String> call;
    private ChatRoomInfoDto chatRoomInfoDto;

    public SyncGetChatRoomInfo(Call<ChatRoomInfoDto> call){
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
                chatRoomInfoDto = (ChatRoomInfoDto)response.body();
            }
            //System.out.println("결과: " + (String)response.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ChatRoomInfoDto getChatRoomInfoDto() {
        return chatRoomInfoDto;
    }
}

