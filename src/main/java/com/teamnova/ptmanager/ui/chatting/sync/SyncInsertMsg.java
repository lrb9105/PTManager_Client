package com.teamnova.ptmanager.ui.chatting.sync;

import android.util.Log;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/** 메시지 저장*/
public class SyncInsertMsg extends Thread{
    private Call<String>call;

    public SyncInsertMsg(Call<String> call){
        this.call = call;
    }

    @Override
    public void run() {
        try {
            Response response = call.execute();
            Log.e("채팅방정보 저장 ", "5. 채팅방 정보 서버에 저장! => " + " call.execute()");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

