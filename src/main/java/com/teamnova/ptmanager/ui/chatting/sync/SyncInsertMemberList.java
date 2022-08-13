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

    }

    @Override
    public void run() {
        try {
            Response response = call.execute();
            Log.e("서버에 채팅 참여자 추가 ", "5. 서버에 채팅참여자 추가 결과 => " + response.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

