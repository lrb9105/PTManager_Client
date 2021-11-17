package com.teamnova.ptmanager.ui.chatting.sync;

import android.util.Log;

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

    @Override
    public void run() {
        try {
            Response response = call.execute();
            Log.e("서버에서 채팅방리스트 가져와서 뷰모델에 세팅", " 5. call.execute() 호출 후 response 반환 => " + response);

            /** 채팅방 정보를 가져왔다면*/
            Log.e("서버에서 채팅방리스트 가져와서 뷰모델에 세팅", " 6. response.toString()가 []이면 데이터 못가져 온것! => " + response.toString());
            if(!response.toString().equals("[]")){
                chatRoomList = (ArrayList<ChatRoomInfoForListDto>)response.body();
                Log.e("서버에서 채팅방리스트 가져와서 뷰모델에 세팅", " 7. 서버에서 가져온 채팅방리스트의 사이즈 => " + chatRoomList.size());

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

