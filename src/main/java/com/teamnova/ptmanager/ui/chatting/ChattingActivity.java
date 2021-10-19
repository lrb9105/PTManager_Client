package com.teamnova.ptmanager.ui.chatting;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.adapter.lesson.member.MemberLessonAdapter;
import com.teamnova.ptmanager.databinding.ActivityChattingBinding;
import com.teamnova.ptmanager.databinding.ActivityChattingRoomBinding;
import com.teamnova.ptmanager.databinding.ActivityExampleBinding;
import com.teamnova.ptmanager.manager.ChattingRoomManager;
import com.teamnova.ptmanager.model.chatting.ChatMsgInfo;
import com.teamnova.ptmanager.model.chatting.ChatRoomInfoDto;
import com.teamnova.ptmanager.model.chatting.ChatRoomInfoForListDto;
import com.teamnova.ptmanager.model.chatting.ChattingMemberDto;
import com.teamnova.ptmanager.model.lesson.LessonSchInfo;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.changehistory.eyebody.EyeBodyService;
import com.teamnova.ptmanager.network.chatting.ChattingService;
import com.teamnova.ptmanager.network.schedule.lesson.LessonService;
import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodyChangeHistoryActivity;
import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodyEnLargeActivity;
import com.teamnova.ptmanager.ui.chatting.adapter.ChattingMsgListAdapter;
import com.teamnova.ptmanager.ui.chatting.async.GetChatRoomInfo;
import com.teamnova.ptmanager.ui.chatting.async.InsertChatRoomInfo;
import com.teamnova.ptmanager.ui.chatting.sync.SyncGetChatRoomInfo;
import com.teamnova.ptmanager.ui.chatting.sync.SyncGetExistedChatRoomId;
import com.teamnova.ptmanager.ui.chatting.sync.SyncInsertChatRoomInfo;
import com.teamnova.ptmanager.ui.home.member.Event;
import com.teamnova.ptmanager.ui.home.member.MemberHomeActivity;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.ui.record.fitness.FitnessRegisterActivity;
import com.teamnova.ptmanager.util.DialogUtil;
import com.teamnova.ptmanager.util.GetDate;
import com.teamnova.ptmanager.viewmodel.chatting.ChattingMsgViewModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Retrofit;

/** 채팅방 화면*/
public class ChattingActivity extends AppCompatActivity {
    private ActivityChattingRoomBinding binding;
    private ChatRoomInfoDto chatRoomInfoDto;
    private Retrofit retrofit;
    // 메시지 수신 시 채팅방에 뿌려주는 핸들러
    private Handler mHandler;
    // 메시지 송신 시(송신은 다른 쓰레드에서 진행) 메인 쓰레드의 ui를 변경하기 위해 데이터를 전달하는 핸들러
    private Handler sendHandler;
    private String ip = "192.168.56.1";
    private int port = 8888;
    // 텍스트 전송자
    private PrintWriter sendWriter;
    // 객체 전송자
    private ObjectOutputStream objWriter;
    private ChattingMemberDto userInfo;
    private String chatRoomId;
    private String sendMsg;
    // 수신쓰레드
    private RecvThread recvThread;
    private boolean isRoomAdded = false;

    // 수신 reader
    private BufferedReader input;

    // 채팅방 매니져
    private ChattingRoomManager chattingRoomManager;
    // 채팅 메시지 뷰모델
    private ChattingMsgViewModel chattingMsgViewModel;

    // 채팅 참여자
    private ArrayList<ChattingMemberDto> chatMemberList;

    // 리사이클러뷰
    private ChattingMsgListAdapter chattingMsgListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;

    // 채팅방 사용자들의 프로필 사진을 담을 리스트
    private HashMap<String, String> userProfileBitmapMap;

    // 채팅상대 초대 후 다시 돌아왔을 때 기존 채팅참여자 리스트에 새로 초대된 사용자 리스트를 추가한다.
    // 서버에 새로 초대된 사용자들의 입장을 알려주는 메시지를 보낸다.
    private ActivityResultLauncher<Intent> startActivityResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityChattingRoomBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        startActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // 새로 초대한 인원 정보를 가져온다!
                            ArrayList<ChattingMemberDto> addedChatMemberList= (ArrayList<ChattingMemberDto>)result.getData().getSerializableExtra("chatMemberList");

                            // 서버에 해당 사용자들을 추가한다.
                            chattingRoomManager.insertMemberList(addedChatMemberList,chatRoomId);

                            // 기존 채팅참여자 리스트에 새로 초대된 사용자들을 추가하고 서버에 새로 추가된 사용자를 알려주는 메시지를 보낸다.
                            for(ChattingMemberDto c : addedChatMemberList){
                                // 기존에 존재하는 채팅참여자리스트에 새로 추가된 사용자의 정보를 추가해준다.
                                chatMemberList.add(c);

                                // 서버에 새로운 사용자가 추가됐음을 알려라!
                                String enterMsg = c.getUserName() + "님이 입장했습니다.";

                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        try {
                                            sendWriter.println("new" +":"+ c.getUserId() + ":" + chatRoomId + ":" + enterMsg + ":" + GetDate.getTodayDateWithTime());
                                            sendWriter.flush();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();

                                chattingMsgListAdapter.addChatMsgInfo(new ChatMsgInfo("new", "new", "new", chatRoomId, enterMsg, GetDate.getTodayDateWithTime(),0,0));
                                layoutManager.scrollToPosition(chattingMsgListAdapter.getItemCount() - 1);
                            }
                        }
                    }
                });


        super.onCreate(savedInstanceState);

        initialize();
    }

    /** 초기화 */
    public void initialize(){
        retrofit= RetrofitInstance.getRetroClient();

        // 채팅방 매니저
        chattingRoomManager = new ChattingRoomManager();

        // 채팅방 id 가져오기
        chatRoomId = (String)getIntent().getSerializableExtra("chatRoomId");

        // 채팅참여자 리스트
        chatMemberList = chattingRoomManager.getChatMemberList(getIntent(), chatRoomId);

        // 채팅 참여자들의 프로필 비트맵 가져오기
        userProfileBitmapMap = new HashMap<>();

        for(int i = 0, ii = chatMemberList.size(); i < ii; i++){
            String profileId = chatMemberList.get(i).getProfileId();
            String userId = chatMemberList.get(i).getUserId();

            System.out.println(chatMemberList.get(i).getUserName() + " : " + profileId);

            if(profileId != null) {
                userProfileBitmapMap.put(userId, profileId);

            } else{
                userProfileBitmapMap.put(userId,null);
            }
        }

        System.out.println("프로필 맵 사이즈: " + userProfileBitmapMap.size());


        // 뷰모델
        chattingMsgViewModel = new ViewModelProvider(this).get(ChattingMsgViewModel.class);

        // 채팅방 아이디가 없다면 친구목록에서 접근한 것이다 => 서버에서 채팅방을 생성해야 한다.
        if(chatRoomId == null) {
            userInfo = chatMemberList.get(0);

            // 기존에 생성된 채팅방이 있는지 검색 후 존재 시 채팅방 아이디를 가져온다
            chatRoomId = chattingRoomManager.getExistedChatRoomId(chatMemberList);

            chatRoomId = chatRoomId.replace("\"","");

            System.out.println("채팅방아이디: " + chatRoomId);

            // 채팅방 아이디가 존재한다면
            if(chatRoomId != null && !chatRoomId.equals("null")){

                System.out.println("111");
            } else { // 존재하지 않는다면
                // 채팅방 정보를 저장하라(저장하고 채팅방 아이디를 가져올 때까지 mainThread는 멈춰 있어야 함
                System.out.println("222");
                chatRoomId = chattingRoomManager.insertChatRoomInfo(chatMemberList,chatRoomId);
                // 새로운 채팅방이 생성되어 채팅방리스트에 추가해야 한다.
                isRoomAdded = true;
            }
        } else { // 채팅 참여자리스트가 없다면 채팅방 리스트에서 기존에 생성되어있는 채팅방에서 접근한 것이다.
            // 사용자 정보 가져오기기
           userInfo = (ChattingMemberDto) getIntent().getSerializableExtra("userInfo");
        }

        /** 리사이클러뷰 세팅 */
        recyclerView = binding.recyclerViewChatArea;
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // 방이 새로 생성된게 아니라면 서버에서 메시지리스트 가져오기
        if(!isRoomAdded){
            System.out.println("111");
            System.out.println("chatRoomId: " + chatRoomId);
            System.out.println("userInfo.getUserId(): " + userInfo.getUserId());

            ArrayList<ChatMsgInfo> chatMsgList = chattingMsgViewModel.getMsgListInfo(chatRoomId, userInfo.getUserId());

            System.out.println("메시지 사이즈: " + chatMsgList.size());

            chattingMsgListAdapter = new ChattingMsgListAdapter(chatMsgList, this, null, userInfo.getUserId(), userProfileBitmapMap);
            layoutManager.scrollToPosition(chattingMsgListAdapter.getItemCount() - 1);
        } else{
            System.out.println("222");
            chattingMsgListAdapter = new ChattingMsgListAdapter(new ArrayList<>(), this, null, userInfo.getUserId(), userProfileBitmapMap);
        }

        recyclerView.setAdapter(chattingMsgListAdapter);

        // 채팅방 아이디
        chatRoomId = chatRoomId.replace("\"","");

        // 채팅방 정보 가져오기
        chatRoomInfoDto = chattingRoomManager.getChatRoomInfo(chatRoomId);

        /** 채팅방 정보를 세팅하라 */
        setChatRoomInfo();

        /** 서버와 연결요청을 하라 */
        requestConnectionToChatServer(chatRoomId);

        /** 초대!*/
        binding.chatButtonInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 채팅참여 가능자 화면으로 이동
                // 유저아이디와 채팅참여리스트 넘기기

                Intent intent = new Intent(ChattingActivity.this, ChattingPossibleMemberListActivity.class);
                // 사용자 아이디, 채팅참여자 보내기!
                intent.putExtra("userId", userInfo.getUserId());
                intent.putExtra("chatMemberList", chatMemberList);

                startActivityResult.launch(intent);
            }
        });

        /** 나가기*/
        binding.chatButtonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 다이얼로그
                AlertDialog.Builder builder = new AlertDialog.Builder(ChattingActivity.this);

                builder.setMessage("채팅방을 나가시겠습니까?");

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // 서버에 해당 회원 정보 삭제 요청
                        deleteMemberFromChatRoom(chatRoomId, userInfo.getUserId());

                        // 트레이너, 회원 여부에 따라 이동하는 액티비티가 달라져야 함
                        Intent intent = null;

                        if(TrainerHomeActivity.staticLoginUserInfo != null) { //트레이너 라면
                            intent = new Intent(ChattingActivity.this, TrainerHomeActivity.class);
                        } else{
                            intent = new Intent(ChattingActivity.this, MemberHomeActivity.class);
                        }
                        // 채팅방 정보
                        ChatRoomInfoForListDto exitedChatRoomInfo = new ChatRoomInfoForListDto(chatRoomInfoDto.getChattingRoomId(), chatRoomInfoDto.getChattingRoomName(), null, null);

                        intent.putExtra("exitedChatRoomInfo", exitedChatRoomInfo);

                        // 서버에 사용자가 나갔음을 알려라!
                        String exitMsg = userInfo.getUserName() + "님이 나갔습니다.";

                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    sendWriter.println("exit" +":"+ userInfo.getUserId() + ":" + chatRoomId + ":" + exitMsg + ":" + GetDate.getTodayDateWithTime());
                                    sendWriter.flush();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();

                        // 소켓 연결 끊기
                        exit();

                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }).setNegativeButton("취소",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            { }
                        }
                );

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    /** 채팅방 정보를 세팅하라*/
    public void setChatRoomInfo(){
        // 채팅방 명
        binding.chatRoomTitle.setText(chatRoomInfoDto.getChattingRoomName());
    }


    /** 채팅서버에 연결을 요청하라*/
    public void requestConnectionToChatServer(String chatRoomId){
        mHandler = new Handler(getMainLooper());
        sendHandler = new Handler(getMainLooper());

        //binding.textView.setText(userInfo.getUserName());

        recvThread = new RecvThread();
        recvThread.start();

        // 작성완료
        binding.chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendMsg = binding.message.getText().toString();

                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            // 채팅을 입력한 경우에만 전송되도록
                            if(sendMsg != null && !sendMsg.equals("")){

                                // 메인쓰레드에서만 ui변경 가능
                                sendHandler.post(new UiUpdater(sendMsg));

                                // 텍스트만 전송
                                sendWriter.println(userInfo.getUserName() +":"+ userInfo.getUserId() + ":" + chatRoomId + ":" + sendMsg + ":" + GetDate.getTodayDateWithTime());
                                sendWriter.flush();

                                // 메시지 객체를 전송
                                //objWriter.writeObject(new ChatMsgInfo(null, userInfo.getUserId(),chatRoomId, sendMsg));
                                //objWriter.flush();


                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    /** 채팅방에서 나간 유저의 정보 삭제요청*/
    public void deleteMemberFromChatRoom(String chatRoomId, String userId){
        HashMap<String, String> map = new HashMap<>();

        map.put("chatRoomId", chatRoomId);
        map.put("userId", userId);

        chattingRoomManager.deleteMemberFromChatRoom(map);
    }

    // 뒤로가기 버튼 클릭 시 수신쓰레드 종료, 서버에 종료신호 보내기
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        exit();
    }

    /** 연결 종료 시 */
    private void exit(){
        new ExpireThread().start();

        recvThread.interrupt();
    }

    /** 메시지를 화면에 업데이트 하는 쓰레드*/
    class MsgUpdater implements Runnable{
        private String msg;
        private ChatMsgInfo msgInfo;

        // 메시지 타입에 따라 다르게 보여줌
        /*public MsgUpdater(String msg, int type) {
            switch (type){
                case 0: // 문자열
                    this.msg = msg;
                    break;
                case 1: // 이미지
                    break;
                case 2: // 동영상
                    break;
            }
        }*/

        public MsgUpdater(ChatMsgInfo msgInfo) {
            // 2. msgInfo가 잘 만들어 졌는가?
            System.out.println("msgInfo: " + msgInfo);

            this.msgInfo = msgInfo;
        }

        @Override
        public void run() {
            // 어댑터에 추가
            chattingMsgListAdapter.addChatMsgInfo(msgInfo);
            // 마지막 아이템으로 이동
            layoutManager.scrollToPosition(chattingMsgListAdapter.getItemCount() - 1);
            //binding.chatView.setText(binding.chatView.getText().toString() + msg+"\n");
        }
    }

    /** 메시지 송신 시 화면 업데이트 하는 쓰레드*/
    class UiUpdater implements Runnable{
        private String sendMsg;

        public UiUpdater(String sendMsg) {
            this.sendMsg = sendMsg;
        }

        @Override
        public void run() {
            // 리사이클러뷰에 뿌여주기
            // 처음에 뿌려줄 땐 읽지않은 사용자 수와 msgIdx를 0으로 초기화!
            chattingMsgListAdapter.addChatMsgInfo(new ChatMsgInfo(null, userInfo.getUserId(), userInfo.getUserName(), chatRoomId, sendMsg, GetDate.getTodayDateWithTime(),0,0));
            layoutManager.scrollToPosition(chattingMsgListAdapter.getItemCount() - 1);
            binding.message.setText("");
        }
    }

    private class ExpireThread extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                sendWriter.println("!@#$connectionExpire:");
                sendWriter.flush();
                sendWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private class RecvThread extends Thread{
        Socket socket = null;

        @Override
        public void run() {
            try {
                System.out.println("실행됨?");

                InetAddress serverAddr = InetAddress.getByName(ip);
                socket = new Socket(serverAddr, port);

                // 텍스트 메시지를 전송받는 경우
                sendWriter = new PrintWriter(socket.getOutputStream());
                //objWriter = new ObjectOutputStream(socket.getOutputStream());

                // 텍스트 메시지를 전송받는 경우
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // 객체를 전송받는 경우
                //ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());


                /** 채팅방id 전송
                 * */
                sendWriter.println("!@#$chatRoomIdAndUserId:" + chatRoomId + ":" + userInfo.getUserId());
                sendWriter.flush();

                /** 채팅방id를 포함한 매시지객체 전송
                 * */
                /*objWriter.writeObject(new ChatMsgInfo(null, userInfo.getUserId(),chatRoomId, "!@#$chatRoomIdAndUserId:" + chatRoomId + ":" + userInfo.getUserId()));
                objWriter.flush();*/



                while(true){
                    // 텍스트 메시지 전송
                    String read = null;

                    if(!socket.isClosed()){
                        read = input.readLine();
                    }

                    // 객체 전송
                    //String read = null;

                    /*try {
                        ChatMsgInfo chatMsgInfo = (ChatMsgInfo)ois.readObject();
                        read = chatMsgInfo.getMsg();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }*/

                    // 채팅 문자열 - 추후에 객체로 변경하기
                    if(read!=null){
                        String[] msgArr = read.split(":");

                        // 내가보낸 메시지가 아니라면
                        if(msgArr.length > 2){
                            String userName = msgArr[0];
                            String userId = msgArr[1];
                            String roomId = msgArr[2];
                            String msg = msgArr[3];
                            String creDatetime = GetDate.getTodayDateWithTime();
                            int notReadUserCount = Integer.parseInt(msgArr[5]);
                            int msgIdx = Integer.parseInt(msgArr[6]);

                            // 1. 서버에서 메시지가 잘 넘어오는가
                            System.out.println("서버로부터 메시지: " + read);

                            // 메시지 객체 만들어서 업데이터에게 보내기
                            mHandler.post(new MsgUpdater(new ChatMsgInfo(null, userId, userName, roomId, msg, creDatetime,notReadUserCount,msgIdx)));
                        } else { // 내가보낸 메시지라면 읽지않은사용자수와 메시지 인덱스만 보내줌
                            int notReadUserCount = Integer.parseInt(msgArr[0]);
                            int msgIdx = Integer.parseInt(msgArr[1]);

                            // 어댑터에서 가장 마지막에있는 메시지 업데이트!
                            chattingMsgListAdapter.updateCntAndIdx(notReadUserCount, msgIdx);
                        }

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(input != null){
                        input.close();
                    }

                    if(socket != null){
                        socket.close();
                    }

                    if(sendWriter != null){
                        sendWriter.close();
                    }

                    if(objWriter != null){
                        objWriter.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}