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

            roomId = (String)response.body();
            System.out.println("데이터 저장하고 가져온 룸아이디: " + roomId);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRoomId(){
        return roomId;
    }
}

