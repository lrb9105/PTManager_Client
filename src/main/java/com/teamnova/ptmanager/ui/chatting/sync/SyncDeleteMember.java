package com.teamnova.ptmanager.ui.chatting.sync;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/** 채팅방에서 나간 회원의 삭제를 요청하는 쓰레드*/
public class SyncDeleteMember extends Thread{
    private Call<String>call;

    public SyncDeleteMember(Call<String> call){
        this.call = call;
    }

    @Override
    public void run() {
        try {
            Response response = call.execute();

            System.out.println("채팅방을 나간 회원 삭제 결과:" + response.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

