package com.teamnova.ptmanager.ui.chatting.sync;

import android.util.Log;

import com.teamnova.ptmanager.model.chatting.ChatRoomInfoDto;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class SyncGetExistedChatRoomId extends Thread{
    private Call<String> call;
    private String chatRoomId;

    public SyncGetExistedChatRoomId(Call<String> call){
        this.call = call;
    }

    @Override
    public void run() {
        try {
            Response response = call.execute();
            Log.e("멤버리스트로 채팅방 아이디 가져오기 ", "3. 서버에서 멤버리스트 전부가 속해있는 채팅방 아이디 가져오기 => " + response);

            /** 채팅방 정보를 가져왔다면*/
            if(response.body() != null){
                // 해당하는 id가 없으면 "null"이 들어있음!
                chatRoomId = (String)response.body();
            }

            Log.e("멤버리스트로 채팅방 아이디 가져오기 ", "4. 서버에서 가져온 채팅방 아이디 멤버리스트 전부가 속해있는 채팅방이 없다면 null 반환 => " + response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getChatRoomId() {
        return chatRoomId;
    }
}

