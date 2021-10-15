package com.teamnova.ptmanager.manager;

import android.content.Intent;

import com.teamnova.ptmanager.model.chatting.ChatRoomInfoDto;
import com.teamnova.ptmanager.model.chatting.ChatRoomInfoForListDto;
import com.teamnova.ptmanager.model.chatting.ChattingMemberDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.chatting.ChattingService;
import com.teamnova.ptmanager.ui.chatting.sync.SyncGetChatMemberList;
import com.teamnova.ptmanager.ui.chatting.sync.SyncGetChatRoomInfo;
import com.teamnova.ptmanager.ui.chatting.sync.SyncGetChatRoomList;
import com.teamnova.ptmanager.ui.chatting.sync.SyncGetExistedChatRoomId;
import com.teamnova.ptmanager.ui.chatting.sync.SyncInsertChatRoomInfo;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;

/** 채팅방과 관련된 일을 수행하는 객체*/
public class ChattingRoomManager {
    private Retrofit retrofit = RetrofitInstance.getRetroClient();

    /** 채팅참여자 정보를 가져와라*/
    public ArrayList<ChattingMemberDto> getChatMemberList(Intent intent, String chatRoomId){
        ArrayList<ChattingMemberDto> chatMemberList = null;
        chatMemberList = (ArrayList<ChattingMemberDto>)intent.getSerializableExtra("chatMemberList");

        // 친구목록에서 넘어온게 아니라면(채팅방 리스트에서 넘어왔다면) 서버에서 채팅 참여자목록을 가져온다.
        if(chatMemberList == null){
            ChattingService service = retrofit.create(ChattingService.class);

            Call<ArrayList<ChattingMemberDto>> call = service.getChatMemberList(chatRoomId);

            // 서버에서 참여자리스트 데이터를 가져오는 동기 함수의 쓰레드
            SyncGetChatMemberList t = new SyncGetChatMemberList(call);
            t.start();

            try {
                // 쓰레드에서 데이터를 저장할 때 main쓰레드는 중지를 시켜야 하므로 join()사용
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            chatMemberList = t.getChatMemberList();
        }

        return chatMemberList;
    }

    /** 채팅방 정보를 가져와라 */
    public ChatRoomInfoDto getChatRoomInfo(String chattingRoomId){
        ChattingService service = retrofit.create(ChattingService.class);

        // http request 객체 생성
        //Call<String> call = service.getChatRoomInfo(chattingRoomId);
        Call<ChatRoomInfoDto> call = service.getChatRoomInfo(chattingRoomId);

        // 서버에서 데이터를 가져오는 동기 함수의 쓰레드
        SyncGetChatRoomInfo t = new SyncGetChatRoomInfo(call);
        t.start();

        try {
            // 쓰레드에서 데이터를 저장할 때 main쓰레드는 중지를 시켜야 하므로 join()사용
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return t.getChatRoomInfoDto();
    }

    /** 채팅방 리스트를 가져와라 */
    public ArrayList<ChatRoomInfoForListDto> getChatRoomList(String userId){
        ChattingService service = retrofit.create(ChattingService.class);

        // http request 객체 생성
        //Call<String> call = service.getChatRoomInfo(chattingRoomId);
        Call<ArrayList<ChatRoomInfoForListDto>> call = service.getChattingRoomList(userId);

        // 서버에서 데이터를 가져오는 동기 함수의 쓰레드
        SyncGetChatRoomList t = new SyncGetChatRoomList(call);
        t.start();

        try {
            // 쓰레드에서 데이터를 저장할 때 main쓰레드는 중지를 시켜야 하므로 join()사용
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return t.getChatRoomList();
    }

    /**참여자 리스트 인원들을 포함하고 있는 채팅방 id를 가져와라*/
    public String getExistedChatRoomId(ArrayList<ChattingMemberDto> chatMemberList){
        ChattingService service = retrofit.create(ChattingService.class);
        // http request 객체 생성
        Call<String> call = service.getExistedChatRoomId(chatMemberList);

        // 서버에 데이터를 저장하는 동기 함수의 쓰레드
        SyncGetExistedChatRoomId t = new SyncGetExistedChatRoomId(call);
        t.start();

        try {
            // 쓰레드에서 데이터를 저장할 때 main쓰레드는 중지를 시켜야 하므로 join()사용
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return t.getChatRoomId();
    }

    /** 채팅방 정보를 저장하라 */
    public String insertChatRoomInfo(ArrayList<ChattingMemberDto> chatMemberList, String chatRoomId){
        ChattingService service = retrofit.create(ChattingService.class);

        // 채팅방 정보객체
        String chatRoomName = "";

        for(ChattingMemberDto c : chatMemberList){
            chatRoomName += c.getUserName() + ",";
        }
        chatRoomName = chatRoomName.substring(0, chatRoomName.length() - 1);

        ChatRoomInfoDto chatRoomInfoDto = new ChatRoomInfoDto(null, chatRoomName, chatMemberList);

        // http request 객체 생성
        Call<String> call = service.insertChatRoomInfo(chatRoomInfoDto);

        // 서버에 데이터를 저장하는 동기 함수의 쓰레드
        SyncInsertChatRoomInfo t = new SyncInsertChatRoomInfo(chatRoomId , call);
        t.start();

        try {
            // 쓰레드에서 데이터를 저장할 때 main쓰레드는 중지를 시켜야 하므로 join()사용
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return t.getRoomId();
    }
}