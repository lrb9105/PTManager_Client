package com.teamnova.ptmanager.ui.chatting;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityChattingBinding;
import com.teamnova.ptmanager.databinding.ActivityExampleBinding;
import com.teamnova.ptmanager.model.chatting.ChatRoomInfoDto;
import com.teamnova.ptmanager.model.chatting.ChattingMemberDto;
import com.teamnova.ptmanager.model.lesson.LessonSchInfo;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.changehistory.eyebody.EyeBodyService;
import com.teamnova.ptmanager.network.chatting.ChattingService;
import com.teamnova.ptmanager.network.schedule.lesson.LessonService;
import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodyChangeHistoryActivity;
import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodyEnLargeActivity;
import com.teamnova.ptmanager.ui.chatting.async.GetChatRoomInfo;
import com.teamnova.ptmanager.ui.chatting.async.InsertChatRoomInfo;
import com.teamnova.ptmanager.ui.chatting.sync.SyncGetChatRoomInfo;
import com.teamnova.ptmanager.ui.chatting.sync.SyncGetExistedChatRoomId;
import com.teamnova.ptmanager.ui.chatting.sync.SyncInsertChatRoomInfo;
import com.teamnova.ptmanager.ui.home.member.MemberHomeActivity;
import com.teamnova.ptmanager.util.DialogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Retrofit;

public class ChattingActivity extends AppCompatActivity {
    private ActivityChattingBinding binding;
    private ChatRoomInfoDto chatRoomInfoDto;
    private Retrofit retrofit;
    private Handler mHandler;
    private String ip = "192.168.56.1";
    private int port = 8888;
    private PrintWriter sendWriter;
    private ChattingMemberDto userInfo;
    private String chatRoomId;
    private String sendMsg;
    // 수신쓰레드
    private RecvThread recvThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityChattingBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        super.onCreate(savedInstanceState);

        initialize();
    }

    /** 초기화 */
    public void initialize(){
        retrofit= RetrofitInstance.getRetroClient();

        // 채팅참여자
        ArrayList<ChattingMemberDto> chatMemberList = getChatMemberListFromIntent(getIntent());

        // 채팅참여자리스트가 있다면 친구목록에서 접근한 것이다 => 채팅방을 생성해야 함!
        if(chatMemberList != null) {
            userInfo = chatMemberList.get(0);

            // 기존에 생성된 채팅방이 있는지 검색 후 존재 시 채팅방 아이디를 가져온다
            chatRoomId = getExistedChatRoomId(chatMemberList);

            chatRoomId = chatRoomId.replace("\"","");

            // 채팅방 아이디가 존재한다면
            if(chatRoomId != null && !chatRoomId.equals("null")){
                // 기존에 작성된 메시지 리스트 가져오가
                // 메시지 리스트 리사이클러뷰에 뿌리기
                System.out.println("111");
            } else { // 존재하지 않는다면
                // 채팅방 정보를 저장하라(저장하고 채팅방 아이디를 가져올 때까지 mainThread는 멈춰 있어야 함
                System.out.println("222");
                chatRoomId = insertChatRoomInfo(chatMemberList);

            }
        } else { // 채팅 참여자리스트가 없다면 채팅방 리스트에서 접근한 것이다.
            // 채팅방 id 가져오기
            chatRoomId = (String)getIntent().getSerializableExtra("chatRoomId");
            // 사용자 정보 가져오기기
           userInfo = (ChattingMemberDto) getIntent().getSerializableExtra("userInfo");
        }

        chatRoomId = chatRoomId.replace("\"","");

        // 채팅방 정보 가져오기
        chatRoomInfoDto = getChatRoomInfo(chatRoomId);

        /** 채팅방 정보를 세팅하라 */
        setChatRoomInfo();

        /** 서버와 연결요청을 하라 */
        requestConnectionToChatServer(chatRoomId);
    }

    /** 채팅참여자 정보를 가져와라*/
    public ArrayList<ChattingMemberDto> getChatMemberListFromIntent(Intent intent){
        ArrayList<ChattingMemberDto> chatMemberList = null;
        chatMemberList = (ArrayList<ChattingMemberDto>)intent.getSerializableExtra("chatMemberList");

        return chatMemberList;
    }

    /** 채팅방 정보를 세팅하라*/
    public void setChatRoomInfo(){
        // 채팅방 명
        binding.chatRoomTitle.setText(chatRoomInfoDto.getChattingRoomName());
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
    public String insertChatRoomInfo(ArrayList<ChattingMemberDto> chatMemberList){
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

    /** 채팅서버에 연결을 요청하라*/
    public void requestConnectionToChatServer(String chatRoomId){
        mHandler = new Handler();

        binding.textView.setText(userInfo.getUserName());

        recvThread = new RecvThread();
        recvThread.start();

        // 작성완료
        binding.chatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendMsg = binding.message.getText().toString();

                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            sendWriter.println(userInfo.getUserName() +">"+ sendMsg);
                            sendWriter.flush();

                            binding.message.setText("");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    // 뒤로가기 버튼 클릭 시 수신쓰레드 종료, 서버에 종료신호 보내기
    @Override
    public void onBackPressed() {
        super.onBackPressed();
 
        /*ExpireThread expireThread = new ExpireThread();
        expireThread.start();*/

        recvThread.interrupt();

        //expireThread.interrupt();
    }

    /** 메시지를 화면에 업데이트 하는 쓰레드*/
    class MsgUpdater implements Runnable{
        private String msg;

        // 메시지 타입에 따라 다르게 보여줌
        public MsgUpdater(String msg, int type) {
            switch (type){
                case 0: // 문자열
                    this.msg = msg;
                    break;
                case 1: // 이미지
                    break;
                case 2: // 동영상
                    break;
            }
        }

        @Override
        public void run() {
            // 리사이클러뷰에 보내주기
            binding.chatView.setText(binding.chatView.getText().toString() + msg+"\n");
        }
    }

    private class ExpireThread extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                sendWriter.println("!@#$connectionExpire:");
                sendWriter.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private class RecvThread extends Thread{
        @Override
        public void run() {
            try {
                System.out.println("실행됨?");

                InetAddress serverAddr = InetAddress.getByName(ip);
                Socket socket = new Socket(serverAddr, port);
                sendWriter = new PrintWriter(socket.getOutputStream());
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                /** 채팅방id 전송
                 * */
                sendWriter.println("!@#$chatRoomIdAndUserId:" + chatRoomId + ":" + userInfo.getUserId());
                sendWriter.flush();

                while(true){
                    String read = input.readLine();

                    // 채팅 문자열 - 추후에 객체로 변경하기
                    if(read!=null){
                        mHandler.post(new MsgUpdater(read, 0));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}