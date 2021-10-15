package com.teamnova.ptmanager.ui.chatting.sync;

import com.teamnova.ptmanager.model.chatting.ChatRoomInfoForListDto;
import com.teamnova.ptmanager.model.chatting.ChattingMemberDto;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

/** 채팅방 멤버 리스트 가져오기*/
public class SyncGetChatMemberList extends Thread{
    private Call<ArrayList<ChattingMemberDto>> call;
    //private Call<String> call;
    private ArrayList<ChattingMemberDto> chatMemberList;

    public SyncGetChatMemberList(Call<ArrayList<ChattingMemberDto>> call){
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
                chatMemberList = (ArrayList<ChattingMemberDto>)response.body();
            }
            //System.out.println("결과: " + (String)response.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ChattingMemberDto> getChatMemberList() {
        return chatMemberList;
    }
}

