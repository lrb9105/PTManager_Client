package com.teamnova.ptmanager.ui.chatting.sync;

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

            System.out.println("새로운 사용자 초대 후 저장!: " + response.body());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

