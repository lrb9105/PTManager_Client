package com.teamnova.ptmanager.manager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.teamnova.ptmanager.model.chatting.ChatMsgInfo;
import com.teamnova.ptmanager.model.chatting.ChatRoomInfoDto;
import com.teamnova.ptmanager.model.chatting.ChatRoomInfoForListDto;
import com.teamnova.ptmanager.model.chatting.ChattingMemberDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.chatting.ChattingService;
import com.teamnova.ptmanager.ui.chatting.sync.SyncDeleteMember;
import com.teamnova.ptmanager.ui.chatting.sync.SyncGetChatMemberList;
import com.teamnova.ptmanager.ui.chatting.sync.SyncGetChatRoomInfo;
import com.teamnova.ptmanager.ui.chatting.sync.SyncGetChatRoomList;
import com.teamnova.ptmanager.ui.chatting.sync.SyncGetCurrentServerTime;
import com.teamnova.ptmanager.ui.chatting.sync.SyncGetExistedChatRoomId;
import com.teamnova.ptmanager.ui.chatting.sync.SyncInsertChatRoomInfo;
import com.teamnova.ptmanager.ui.chatting.sync.SyncInsertMemberList;

import java.util.ArrayList;
import java.util.HashMap;

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
        } else {
            Log.e("채팅방 만들기 ", "15. 인텐트에서 채팅 참여자 목록 정보를 가져온다. => 사이즈: " + chatMemberList.size());
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
        Log.e("서버에서 채팅방리스트 가져와서 뷰모델에 세팅", " 2. ChattingService 객체 생성 => " + service);

        // http request 객체 생성
        //Call<String> call = service.getChatRoomInfo(chattingRoomId);
        Call<ArrayList<ChatRoomInfoForListDto>> call = service.getChattingRoomList(userId);
        Log.e("서버에서 채팅방리스트 가져와서 뷰모델에 세팅", " 3. call 객체 생성 => " + call);

        // 서버에서 데이터를 가져오는 동기 함수의 쓰레드
        SyncGetChatRoomList t = new SyncGetChatRoomList(call);
        Log.e("서버에서 채팅방리스트 가져와서 뷰모델에 세팅", " 4. SyncGetChatRoomList 객체 생성 => " + t);
        t.start();

        try {
            // 쓰레드에서 데이터를 저장할 때 main쓰레드는 중지를 시켜야 하므로 join()사용
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 서버에서 가져온 채팅방 리스트
        ArrayList<ChatRoomInfoForListDto> list = t.getChatRoomList();
        Log.e("서버에서 채팅방리스트 가져와서 뷰모델에 세팅", " 8. 서버에서 가져온 채팅방리스트 반환=> " + list + " null이면 해당 유저가 속해있는 채팅방이 없는 것!");

        return list;
    }

    /**참여자 리스트 인원들을 포함하고 있는 채팅방 id를 가져와라*/
    public String getExistedChatRoomId(ArrayList<ChattingMemberDto> chatMemberList){
        ChattingService service = retrofit.create(ChattingService.class);
        Log.e("멤버리스트로 채팅방 아이디 가져오기 ", "1. 채팅서비스 구현체 생성=> " + service);

        // http request 객체 생성
        Call<String> call = service.getExistedChatRoomId(chatMemberList);
        Log.e("멤버리스트로 채팅방 아이디 가져오기 ", "2. call 객체 생성=> " + call);


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
        Log.e("채팅방정보 저장 ", "1. 채팅서비스 객체 생성 => " + service);

        // 채팅방 정보객체
        String chatRoomName = "";

        for(ChattingMemberDto c : chatMemberList){
            chatRoomName += c.getUserName() + ",";
        }
        chatRoomName = chatRoomName.substring(0, chatRoomName.length() - 1);

        Log.e("채팅방정보 저장 ", "2. 채팅방 명 생성 => " + chatRoomName);

        ChatRoomInfoDto chatRoomInfoDto = new ChatRoomInfoDto(null, chatRoomName, chatMemberList);
        Log.e("채팅방정보 저장 ", "3. 채팅방 정보 생성 => " + chatRoomInfoDto);

        // http request 객체 생성
        Call<String> call = service.insertChatRoomInfo(chatRoomInfoDto);
        Log.e("채팅방정보 저장 ", "4. call 객체 생성 => " + call);

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

    /** 초대된 사용자 정보를 저장하라 */
    public void insertMemberList(ArrayList<ChattingMemberDto> chatMemberList, String chatRoomId){
        ChattingService service = retrofit.create(ChattingService.class);
        Log.e("서버에 채팅 참여자 추가 ", "1. 채팅 서비스 객체 생성 => " + service);

        // http request 객체 생성
        // HashMap될까?
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("roomId", chatRoomId);
        hashMap.put("chatMemberList", chatMemberList);

        Log.e("채팅방에서 사용자 초대 10. HashMap애 초대한 인원 정보를 넣는다..", "채팅방아이디: " + hashMap.get("roomId"));

        Call<String> call = service.insertMemberList(hashMap);

        Log.e("채팅방에서 사용자 초대 11. call 생성", "채팅방아이디: " + call);


        // 서버에 데이터를 저장하는 동기 함수의 쓰레드
        SyncInsertMemberList t = new SyncInsertMemberList(call);
        t.start();

        try {
            // 쓰레드에서 데이터를 저장할 때 main쓰레드는 중지를 시켜야 하므로 join()사용
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /** 채팅을 나간 참여자 정보 삭제요청*/
    public void deleteMemberFromChatRoom(HashMap<String , String> map){
        ChattingService service = retrofit.create(ChattingService.class);

        Call<String> call = service.deleteMemberFromChatRoom(map);

        // 서버에 데이터를 저장하는 동기 함수의 쓰레드
        SyncDeleteMember t = new SyncDeleteMember(call);
        t.start();

        try {
            // 쓰레드에서 데이터를 저장할 때 main쓰레드는 중지를 시켜야 하므로 join()사용
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /** 서버의 현재시간 가져오는 메소드*/
    public long getCurrentServerTime(){
        ChattingService service = retrofit.create(ChattingService.class);

        // http request 객체 생성
        Call<String> call = service.getCurrentServerTime();

        // 서버에 데이터를 저장하는 동기 함수의 쓰레드
        SyncGetCurrentServerTime t = new SyncGetCurrentServerTime(call);
        t.start();

        try {
            // 쓰레드에서 데이터를 저장할 때 main쓰레드는 중지를 시켜야 하므로 join()사용
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return t.getServerTime();
    }

    /** 채팅방리스트에서 채팅방아이디만 추출하여 리스트 생성 */
    public ArrayList<String> getChatRoomIdListFromChatRoomList(ArrayList<ChatRoomInfoForListDto> userIncludedChatRoomList){
        ArrayList<String> chatRoomIdList = new ArrayList<>();
        Log.e("채팅방리스트에서 채팅방아이디만 추출하여 리스트 생성 1. chatRoomIdList 생성",chatRoomIdList.toString());

        // userIncludedChatRoomList 사이즈만큼 반복
        for (int i = 0; i < userIncludedChatRoomList.size(); i++){
            String chatRoomId = userIncludedChatRoomList.get(i).getChattingRoomId();
            Log.e("채팅방리스트에서 채팅방아이디만 추출하여 리스트 생성 " + i + "번째 2. chatRoomId 추출",chatRoomIdList.toString());
            chatRoomIdList.add(chatRoomId);
            Log.e("채팅방리스트에서 채팅방아이디만 추출하여 리스트 생성 3. chatRoomId 리스트에 저장 리스트 사이즈: ","" + chatRoomIdList.size());
        }
        
        return chatRoomIdList;
    }
    /** 채팅방아이디 리스트에서 채팅방아이디 추출하여 ":"로 구분된 문자열 생성 생성 */
    public String getChatRoomIdListStrFromChatRoomList(ArrayList<String> chatRoomIdList){
        String chatRoomIdListStr = "";
        Log.e("채팅방아이디 리스트에서 채팅방아이디만 추출하여 \"로\"구분된 문자열 생성 1. chatRoomIdListStr 생성", "생성");

        // userIncludedChatRoomList 사이즈만큼 반복
        for (int i = 0; i < chatRoomIdList.size(); i++){
            String chatRoomId = chatRoomIdList.get(i);
            Log.e("채팅방아이디 리스트에서 채팅방아이디만 추출하여 \"로\"구분된 문자열 생성 2. chatRoomId 추출", chatRoomId);

            chatRoomIdListStr += ":" + chatRoomId;
            Log.e("채팅방아이디 리스트에서 채팅방아이디만 추출하여 \"로\"구분된 문자열 생성 3. chatRoomId 문자열에 추가: ","" + chatRoomIdListStr);
        }

        chatRoomIdListStr = chatRoomIdListStr.substring(1);

        return chatRoomIdListStr;
    }

    /** 안읽은 메시지 카운트 반환
     * 입력: 서버로부터 수신한 메시지객체, SharedPreference
     * 출력: sendedMsgIdx - lastMsgIdx(서버로부터 받은 메시지에 저장되어있는 idx - 채팅방에서 마지막으로 수신한 msgIdx)
     * = 안읽은 메시지 카운트
     * */
    public int getNotReadMsgCount(ChatMsgInfo msgInfo, SharedPreferences sp){
        // 읽지않은 메시지 갯수
        int notReadMsgCount = 0;
        // 채팅방 id
        String chatRoomId = msgInfo.getChattingRoomId();
        Log.e("안읽은 메시지 카운트 반환","1-1. 수신한 메시지의 chatRoomId => " + chatRoomId);
        
        // 메시지 인덱스
        int msgIdx = msgInfo.getMsgIdx();
        Log.e("안읽은 메시지 카운트 반환","1-2. 수신한 메시지의 msgIdx => " + msgIdx);


        // SP에 저장된 마지막으로 읽은 메시지의 idx
        int lastMsgIdxFromSp = 0;

        // 1. SharedPreference(SP)에서 chatRoomId에 해당하는 마지막으로 수신한 메시지 idx 가져오기
        lastMsgIdxFromSp = sp.getInt(chatRoomId,999999);
        Log.e("안읽은 메시지 카운트 반환","2. 해당 채팅방 아이디로 SP에 저장된 마지막 메시지 인덱스 가져오기 없으면 999999 => " + lastMsgIdxFromSp);
        
        // 2. lastMsgIdx가 존재하지 않는다면 새로 생성된 채팅방이다 
        // 따라서 안읽은 메시지는 1이 되야 한다.
        // 그러므로 SP에 현재 msgIdx - 1값을 저장하여 현재 msgIdx - SP에 저장된 인덱스 = 1이 출력되도록 한다.
        if(lastMsgIdxFromSp == 999999){
            SharedPreferences.Editor editor = sp.edit();
            Log.e("안읽은 메시지 카운트 반환","3. SP에 저장된 인덱스가 없다면 msgIdx-1을 SP에 저장해줘야 함. 따라서 Editor 생성 => " + editor);
            
            // 해당 채팅방 id로 메시지 인덱스를 저장해준다
            editor.putInt(msgInfo.getChattingRoomId(), msgIdx - 1);
            editor.commit();

            lastMsgIdxFromSp = sp.getInt(msgInfo.getChattingRoomId(),999999);

            Log.e("안읽은 메시지 카운트 반환","4. SP에 잘 저장되었나 확인 => " + " SP에 저장된 idx: " + lastMsgIdxFromSp);
        }

        // 3. 읽지않은 메시지 갯수 계산
        notReadMsgCount = msgInfo.calculateNotReadMsgCount(lastMsgIdxFromSp);
        Log.e("안읽은 메시지 카운트 반환","6. 안읽은 메시지 카운트 반환 => " + notReadMsgCount);

        // 3. 읽지않은 메시지 갯수 반환
        return notReadMsgCount;
    }
}
