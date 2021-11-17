package com.teamnova.ptmanager.ui.chatting.sync;

import android.util.Log;

import com.teamnova.mylibrary.WeekViewEvent;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.model.lesson.LessonSchInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Response;

public class SyncInsertChatRoomInfo extends Thread{
    private Call<String>call;
    private String roomId;

    public SyncInsertChatRoomInfo(String roomId, Call<String> call){
        this.roomId = roomId;
        this.call = call;
    }

    @Override
    public void run() {
        try {
            Response response = call.execute();
            Log.e("채팅방정보 저장 ", "5. 채팅방 정보 서버에 저장! => " + " call.execute()");

            roomId = (String)response.body();
            Log.e("채팅방정보 저장 ", "6. 저장 완료 시 새로 생성된 채팅방 아이디를 가져온다 => " + roomId);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRoomId(){
        return roomId;
    }
}

