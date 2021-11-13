package com.teamnova.ptmanager.ui.chatting.sync;

import android.util.Log;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/** 새로운 사용자를 초대한 경우 그들의 정보를 저장한다.*/
public class SyncInsertMemberList extends Thread{
    private Call<String>call;

    public SyncInsertMemberList(Call<String> call){
        this.call = call;
        Log.e("채팅방에서 사용자 초대 12.  SyncInsertMemberList 생성", "true");

    }

    @Override
    public void run() {
        try {
            Response response = call.execute();
            Log.e("채팅방에서 사용자 초대 13. 새로운 사용자 초대 후 저장 완료!", (String) response.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

