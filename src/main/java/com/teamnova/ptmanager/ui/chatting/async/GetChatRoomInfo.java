package com.teamnova.ptmanager.ui.chatting.async;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.teamnova.ptmanager.model.chatting.ChatRoomInfoDto;
import com.teamnova.ptmanager.model.chatting.ChattingMemberDto;
import com.teamnova.ptmanager.model.lesson.LessonSchInfo;
import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodyChangeHistoryActivity;
import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodyEnLargeActivity;
import com.teamnova.ptmanager.ui.home.member.Event;
import com.teamnova.ptmanager.ui.home.member.MemberHomeActivity;
import com.teamnova.ptmanager.util.DialogUtil;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;

/** 채팅방 정보를 가져오는 AsyncTask*/
public class GetChatRoomInfo extends AsyncTask<Call, Void, String> {
    private retrofit2.Response<ChatRoomInfoDto> response;
    private ChatRoomInfoDto chatRoomInfoDto;

    public GetChatRoomInfo(ChatRoomInfoDto chatRoomInfoDto) {
        this.chatRoomInfoDto = chatRoomInfoDto;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d("onPreExecute", "11");
    }

    @Override
    protected String  doInBackground(Call[] params) {
        try {
            Call<ChatRoomInfoDto> call = params[0];
            response = call.execute();

            return response.body().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        /** 채팅방 정보를 가져왔다면*/
        if(!result.equals("[]")){
            chatRoomInfoDto = response.body();
        }
    }
}
