package com.teamnova.ptmanager.ui.chatting.async;

import android.os.AsyncTask;
import android.util.Log;

import com.teamnova.ptmanager.model.chatting.ChatRoomInfoDto;

import java.io.IOException;

import retrofit2.Call;

/** 채팅방 정보를 저장하는 AsyncTask*/
public class InsertChatRoomInfo extends AsyncTask<Call, Void, String> {
    private retrofit2.Response<String> response;
    private String chatRoomId;

    public InsertChatRoomInfo(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d("onPreExecute", "11");
    }

    @Override
    protected String  doInBackground(Call[] params) {
        try {
            Call<String> call = params[0];
            response = call.execute();

            return response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        /** 채팅방 정보를 저장했다면 채팅방 id를 가져옴*/
        if(!result.equals("")){
            chatRoomId = response.body();
        }
    }
}
