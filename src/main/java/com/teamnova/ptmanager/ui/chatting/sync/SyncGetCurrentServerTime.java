package com.teamnova.ptmanager.ui.chatting.sync;

import com.teamnova.ptmanager.model.chatting.ChatRoomInfoDto;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class SyncGetCurrentServerTime extends Thread{
    private String serverTime;
    private Call<String> call;


    public SyncGetCurrentServerTime(Call<String> call){
        this.call = call;
    }

    /*public SyncGetChatRoomInfo(Call<String> call){
        this.call = call;
    }*/

    @Override
    public void run() {
        try {
            Response response = call.execute();

            serverTime = (String)response.body();

            System.out.println("결과: " + (String)response.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getServerTime() {
        return Long.parseLong(serverTime);
    }
}

