package com.teamnova.ptmanager.ui.home.member;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityMemberHomeBinding;
import com.teamnova.ptmanager.manager.ChattingRoomManager;
import com.teamnova.ptmanager.model.chatting.ChatMsgInfo;
import com.teamnova.ptmanager.model.chatting.ChatRoomInfoForListDto;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.chatting.ChattingService;
import com.teamnova.ptmanager.service.chatting.ChattingMsgService;
//import com.teamnova.ptmanager.service.chatting.ChattingNotificationService;
import com.teamnova.ptmanager.ui.changehistory.ChangeHistoryFragment;
import com.teamnova.ptmanager.ui.chatting.ChattingActivity;
import com.teamnova.ptmanager.ui.home.member.fragment.MemberHomeFragment;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.ui.moreinfo.ChatListFragment2;
import com.teamnova.ptmanager.ui.record.RecordFragment;
import com.teamnova.ptmanager.viewmodel.chatting.ChattingViewModel;
import com.teamnova.ptmanager.viewmodel.friend.FriendViewModel;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;

public class MemberHomeActivity extends AppCompatActivity {
    ActivityMemberHomeBinding binding;

    // 프래그먼트 트랜잭션(프래그먼트 교체, 추가 , 삭제)을 처리할 프래그먼트 매니저
    private FragmentManager fragmentManager;

    // 홈프래그먼트
    private MemberHomeFragment memberHomeFragment;

    // 변화기록 프래그먼트
    private ChangeHistoryFragment changeHistoryFragment;

    // 기록 프래그먼트
    private RecordFragment recordFragment;

    // 채팅 프래그먼트
    private ChatListFragment2 chatListFragment;

    // 뷰모델
    private FriendViewModel friendViewModel;
    private ChattingViewModel chattingViewModel;

    // 로그인 시 로그인 act로부터 loginId 받아옴
    private String loginId;

    // 서비스와 통신하기 위한 메신저
    private Messenger mServiceMessenger = null;

    // 서비스로 보낼 메신저
    final Messenger mActivityMessenger = new Messenger(new ActivityHandler());

    // 서비스와 연결됐는지 여부
    boolean isBound = false;

    // 사용자 정보
    private FriendInfoDto memberInfo;

    // 서비스의 상태에 따라 콜백 함수를 호출하는 객체.
    private ServiceConnection conn;

    // 서비스로부터 받은 메시지를 처리할 핸들러
    class ActivityHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            // msg.what에는 메시지 종류가 들어있음(서비스에서 설정)
            switch(msg.what){
                // 메시지 수신
                case ChattingMsgService.MSG_RECEIVE_FROM_SERVER:
                    // 서비스로부터 받은 수신한 메시지 객체
                    Bundle bundle = msg.getData();
                    String read = bundle.getString("message");
                    Log.e("채팅방리스트에서 정보 변경 - 0-0.1 서비스로부터 받아온 메시지", "" + read);

                    // 수신한 메시지를 저장할 객체
                    ChatMsgInfo chatMsgInfo = null;

                    // 서버에서 메시지 전송 시 ":"를 구분자로 하여 데이터를 전송하므로 이것을 기준으로 split!
                    String[] msgArr = read.split(":");

                    // 내가보낸 메시지가 아니라면
                    // 텍스트msg: userNama:userId:roomId:msg:creDatetime:notReadUserCount:msgIdx:savePath;
                    // 파일 포함msg: userNama:userId:roomId:msg:creDatetime:notReadUserCount:msgIdx;
                    if(msgArr.length > 10){
                        String userName = msgArr[0];
                        String userId = msgArr[1];
                        String roomId = msgArr[2];
                        String msg2 = msgArr[3];
                        String creDatetime = msgArr[msgArr.length - 5] + ":" + msgArr[msgArr.length - 4]  + ":" + msgArr[msgArr.length - 3];
                        int notReadUserCountPos = 0;
                        int msgIdxPos = 0;
                        int savePathPos = 0;
                        String savePath = null;

                        // 텍스트 메시지
                        if(msgArr.length == 13){
                            userName = msgArr[1];
                            userId = msgArr[2];
                            roomId = msgArr[3];
                            msg2 = msgArr[4];
                            creDatetime = msgArr[msgArr.length - 5] + ":" + msgArr[msgArr.length - 4]  + ":" + msgArr[msgArr.length - 3];
                            notReadUserCountPos = msgArr.length-2;
                            msgIdxPos = msgArr.length-1;
                        } else if(msgArr.length == 12){
                            notReadUserCountPos = msgArr.length-2;
                            msgIdxPos = msgArr.length-1;
                        } else { // 파일포함 메시지
                            creDatetime = msgArr[msgArr.length - 3] + ":" + msgArr[msgArr.length - 2]  + ":" + msgArr[msgArr.length - 1];
                            notReadUserCountPos = msgArr.length-4;
                            msgIdxPos = msgArr.length-6;
                            savePathPos = msgArr.length-5;
                        }

                        // 뒤에서 두번째에 안읽은 사람수가 있음
                        int notReadUserCount = Integer.parseInt(msgArr[notReadUserCountPos]);
                        int msgIdx = Integer.parseInt(msgArr[msgIdxPos]);

                        // 파일이 존재한다면 저장경로 위치 != 0
                        if(savePathPos != 0){
                            savePath = msgArr[savePathPos];
                        }

                        chatMsgInfo = new ChatMsgInfo(null, userId, userName, roomId, msg2, creDatetime,notReadUserCount,msgIdx,savePath,null,"N");
                        Log.e("채팅방리스트에서 정보 변경 - 0-1. chatMsgInfo", "" + chatMsgInfo.toString());
                    } else {
                        // 내가보낸 메시지
                        // 텍스트: 읽지않은사용자수, 메시지 인덱스, 서버 수신시간
                        // 파일: 읽지않은사용자수, 메시지 인덱스, 서버 수신시간, 저장경로
                        boolean isText = (msgArr.length == 7);

                        int notReadUserCount = Integer.parseInt(msgArr[0]);
                        int msgIdx = Integer.parseInt(msgArr[1]);
                        String savePath = null;
                        String chatRoomId = null;
                        String msg2 = null;

                        String creDateTime = msgArr[2] + ":" + msgArr[3] + ":" + msgArr[4];

                        // 어댑터에서 가장 마지막에있는 메시지 업데이트!
                        // 리사이클러뷰의 업데이트는 main UI에서만 가능!!!!
                        //System.out.println("서버로부터 메시지222: " + read);
                        // 이미지 메시지라면
                        if(!isText) {
                            savePath = msgArr[5];
                            chatRoomId = msgArr[6];
                            msg2 =  msgArr[7];
                        } else {
                            // 텍스트 메시지라면 5번 인덱스에 채팅방 아이디 들어있음.
                            chatRoomId = msgArr[5];
                            msg2 =  msgArr[6];
                        }

                        chatMsgInfo = new ChatMsgInfo(null, null, null, chatRoomId, msg2, creDateTime,notReadUserCount,msgIdx, savePath,null,"N");
                        Log.e("채팅방리스트에서 정보 변경 - 0-2. chatMsgInfo", "" + chatMsgInfo.toString());
                    }



                    // 뷰모델에 메시지 정보 업데이트
                    ArrayList<ChatRoomInfoForListDto> chatRoomList = new ChattingRoomManager().getChatRoomList(memberInfo.getUserId());

                    // 이미 존재하는 채팅방일 경우 그것의 인덱스를 저장하는 변수
                    int chatRoomIdx = 0;

                    // 0. 메시지에 들어있는 채팅방 아이디로 기존에 존재하는 채팅방인지 여부 구하기
                    // 존재하는 방 여부를 저장하는 변수
                    boolean isExistRoomId = false;

                    // chatRoomList의 사이즈만큼 반복
                    for(int i = 0; i < chatRoomList.size(); i++ ) {
                        // 만약 수신한 메시지와 동일한 채팅방아이디를 가지는 아이템이 존재한다면 isExistRoomId = true
                        if(chatMsgInfo.getChattingRoomId().equals(chatRoomList.get(i).getChattingRoomId())){
                            isExistRoomId = true;
                            chatRoomIdx = i;
                            break;
                        }
                    }

                    Log.e("채팅방리스트에서 정보 변경 - 1. isExistRoomId", "" + isExistRoomId);
                    Log.e("채팅방리스트에서 정보 변경 - 2. chatRoomIdx", "" + chatRoomIdx);

                    // 1. 아래 값들을 세팅해야 함
                    // 채팅방 아이디
                    String chattingRoomId = chatMsgInfo.getChattingRoomId();
                    Log.e("채팅방리스트에서 정보 변경 - 3. chattingRoomId", "" + chattingRoomId);

                    // 채팅방 명
                    String chattingRoomName = null;
                    // 채팅촘방 유저 수
                    int userCount = 0;

                    // 메시지 내용
                    String latestMsg = chatMsgInfo.getMsg();
                    Log.e("채팅방리스트에서 정보 변경 - 4. latestMsg", "" + latestMsg);


                    // 메시지 수신시간
                    String latestMsgTime = chatMsgInfo.getCreDatetime();
                    Log.e("채팅방리스트에서 정보 변경 - 5. latestMsgTime", "" + latestMsgTime);

                    // 생성인 경우 가져오기
                    if(!isExistRoomId){
                        // 서버에서 채팅방 정보 가져오기
                        // 3. 채팅방이 존재하지 않는다면 채팅방 생성 - 첫번쨰 위치에 채팅방 데이터 추가
                        Log.e("채팅방리스트에서 정보 변경 - 6. 생성인 경우 잘들어오는가", "true");

                        // 레트로핏 객체
                        Retrofit retrofit = RetrofitInstance.getRetroClient();
                        Log.e("채팅방리스트에서 정보 변경 - 7. 레트로핏 객체 생성", retrofit.toString());

                        // 채팅방 정보 가져오기
                        ChattingService service = retrofit.create(ChattingService.class);
                        Log.e("채팅방리스트에서 정보 변경 - 8. 서비스구현 객체 생성", service.toString());

                        // http request 객체 생성
                        Call<ChatRoomInfoForListDto> call = service.getChatRoomInfoWithUserId(chattingRoomId, memberInfo.getUserId());
                        Log.e("채팅방리스트에서 정보 변경 - 9. call 객체 생성", service.toString());

                        new GetChatRoomInfo(chatRoomList, latestMsg, latestMsgTime).execute(call);
                    } else { // 수정인 경우 그대로 사용하기
                        Log.e("채팅방리스트에서 정보 변경 - 6. 수정인 경우 잘들어오는가", "true");

                        // 2. 채팅방이 존재한다면 채팅방 데이터 수정 - 채팅방 위치 첫번째로 옮기기(기존 데이터 삭제, 0번째 인덱스에 추가)
                        // 채팅방 정보
                        ChatRoomInfoForListDto chatRoomInfo = chatRoomList.get(chatRoomIdx);
                        Log.e("채팅방리스트에서 정보 변경 - 7. chatRoomInfo", chatRoomInfo.toString());

                        // 메시지 정보 세팅
                        chatRoomInfo.setLatestMsg(latestMsg);
                        Log.e("채팅방리스트에서 정보 변경 - 8. setLatestMsg", latestMsg);

                        chatRoomInfo.setLatestMsgTime(latestMsgTime);
                        Log.e("채팅방리스트에서 정보 변경 - 9. latestMsgTime", latestMsgTime);

                        // 기존 채팅방정보 삭제
                        Log.e("채팅방리스트에서 정보 변경 - 10. remove 전", "" + chatRoomList.size());

                        chatRoomList.remove(chatRoomIdx);

                        Log.e("채팅방리스트에서 정보 변경 - 11 remove 후", "" + chatRoomList.size());

                        // 수정한 채팅방정보 맨위로 올리기
                        chatRoomList.add(0, chatRoomInfo);

                        Log.e("채팅방리스트에서 정보 변경 - 12. add", "" + chatRoomList.size());

                        // viewModel에 저장하기
                        chattingViewModel.getChattingList().setValue(chatRoomList);
                    }

                    //
                    // 계산해야 함 - sp에 마지막 인덱스가 존재하는 경우와 그렇지 않은 경우로 나눠서 계산해야 함
                    // 없다면 1로하고 sp에 저장
                    // 있다면 현재 "메시지인덱스 - sp에 저장된 메시지인덱스 값"이 읽지않은 메시지 값

                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다.
        binding = ActivityMemberHomeBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        // 앱이 시작되면 이 값을 true로 변경한다.
        //ChattingNotificationService.isActRunning = true;
        
        super.onCreate(savedInstanceState);

        initialize();
    }

    // 초기화
    public void initialize(){
        Intent intent = getIntent();

        // 로그인 액티비티에서 액티비티 전환이 됐다면 -> 최초 로그인 시에만 Intent가 있음
        if(intent != null){
            // 액티비티와 프래그먼트 사이에서 트랜잭션을 담당할 프래그먼트 매니저
            fragmentManager = getSupportFragmentManager();

            // 프래그먼트 추가, 교체, 삭제 등을 담당하는 트랜잭션
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // 교체할 홈 프래그먼트
            memberHomeFragment = new MemberHomeFragment();

            transaction.add(binding.trainerFrame.getId(), memberHomeFragment,"frag1");

            transaction.commit();

            // viewModel 초기화
            friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);
            chattingViewModel = new ViewModelProvider(this).get(ChattingViewModel.class);

            loginId = intent.getStringExtra("loginId");

            // 로그인한 회원 정보 가져오기
            friendViewModel.getFriendInfo(loginId);

            friendViewModel.getFriendInfo().observe(this, memberInfo -> {
                this.memberInfo = memberInfo;

                // 서비스에 바인드 한다.
                // Intent와 ServiceConnection 객체를 파라미터로 넣는다.
                Intent intent1 = new Intent(getApplicationContext(), ChattingMsgService.class);

                startService(intent1);
                Log.e("MemberHomeAct에서 Socket 연결 0. startService", "true");
                getApplicationContext().bindService(intent1, conn, Context.BIND_AUTO_CREATE);
                Log.e("MemberHomeAct에서 Socket 연결 1. bindService", "true");
            });
        }

        // Bottom Navigation의 아이템 선택 리스너
        binding.bottomNavigation.setOnNavigationItemSelectedListener(item ->  {
            FragmentTransaction transaction2 = fragmentManager.beginTransaction();

            switch(item.getItemId()) {
                case R.id.item_home:
                    if(memberHomeFragment == null) {
                        memberHomeFragment = new MemberHomeFragment();
                    }

                    if(recordFragment != null) fragmentManager.beginTransaction().hide(recordFragment).commit();
                    if(changeHistoryFragment != null) fragmentManager.beginTransaction().hide(changeHistoryFragment).commit();
                    if(memberHomeFragment != null) transaction2.show(memberHomeFragment).commit();
                    if(chatListFragment != null) fragmentManager.beginTransaction().hide(chatListFragment).commit();

                    break;
                case R.id.item_chatting: // 채팅
                    /*// 채팅화면 생성
                    Intent intent2 = new Intent(this, ChattingActivity.class);

                    //채팅참여자 정보 생성
                    ArrayList<ChattingMemberDto> chatMemberList = new ArrayList<>();

                    // 회원 정보
                    FriendInfoDto memberInfo = friendViewModel.getFriendInfo().getValue();

                    intent2.putExtra("userInfo",ChattingMemberDto.makeChatMemberInfo(memberInfo));
                    intent2.putExtra("chatRoomId","CHATTING_ROOM_16");

                    startActivity(intent2);*/


                    break;
                case R.id.item_history: // 변화관리
                    if(changeHistoryFragment == null) {
                        changeHistoryFragment = new ChangeHistoryFragment();
                        transaction2.add(binding.trainerFrame.getId(), changeHistoryFragment,"frag2");
                    }

                    if(memberHomeFragment != null) transaction2.hide(memberHomeFragment).commit();
                    if(recordFragment != null) fragmentManager.beginTransaction().hide(recordFragment).commit();
                    if(changeHistoryFragment != null) fragmentManager.beginTransaction().show(changeHistoryFragment).commit();
                    if(chatListFragment != null) fragmentManager.beginTransaction().hide(chatListFragment).commit();

                    //transaction2.replace(binding.trainerFrame.getId(), changeHistoryFragment,"frag2").commit();
                    break;
                case R.id.item_cahnge: // 기록
                    if(recordFragment == null) {
                        recordFragment = new RecordFragment();
                        transaction2.add(binding.trainerFrame.getId(), recordFragment,"frag3");
                    }

                    if(memberHomeFragment != null) transaction2.hide(memberHomeFragment).commit();
                    if(changeHistoryFragment != null) fragmentManager.beginTransaction().hide(changeHistoryFragment).commit();
                    if(recordFragment != null) fragmentManager.beginTransaction().show(recordFragment).commit();
                    if(chatListFragment != null) fragmentManager.beginTransaction().hide(chatListFragment).commit();

                    //transaction2.replace(binding.trainerFrame.getId(), changeHistoryFragment,"frag2").commit();
                    break;
                case R.id.item_more: // 채팅
                    if(chatListFragment == null) {
                        chatListFragment = new ChatListFragment2();
                        transaction2.add(binding.trainerFrame.getId(), chatListFragment,"frag4");
                    }

                    if(memberHomeFragment != null) transaction2.hide(memberHomeFragment).commit();
                    if(changeHistoryFragment != null) fragmentManager.beginTransaction().hide(changeHistoryFragment).commit();
                    if(recordFragment != null) fragmentManager.beginTransaction().hide(recordFragment).commit();
                    if(chatListFragment != null) fragmentManager.beginTransaction().show(chatListFragment).commit();

                    //transaction2.replace(binding.trainerFrame.getId(), changeHistoryFragment,"frag2").commit();
                    break;
                case R.id.item_more2: // 더보기2
                    //transaction2.replace(binding.trainerFrame.getId(), changeHistoryFragment,"frag2").commit();
                    break;
            }
            return true;
        });

        conn = new ServiceConnection() {
            // 서비스와 연결된 경우 호출
            public void onServiceConnected(ComponentName className, IBinder service) {
                // 우리는 메신저로 서비스와 통신하기 때문에 클라이언트를 대표하는 날섯의 IBinder 객체가 있다.
                Log.e("MemberHomeAct에서 Socket 연결 2. 바인딩", "성공");

                // 서비스와 연결된 메신저 객체 생성
                mServiceMessenger = new Messenger(service);
                Log.e("MemberH촘omeAct에서 Socket 연결 3. mServiceMessenger 생성", mServiceMessenger.toString());

                // 연결여부를 true로 변경해준다.
                isBound = true;
                Log.e("MemberHomeAct에서 Socket 연결 4. isBound true로 변경", "" + isBound);

                try {
                    // 서비스에게 보낼 메시지를 생성한다.
                    // 파라미터: 핸들러, msg.what에 들어갈 int값
                    Message msg = Message.obtain(null, ChattingMsgService.CONNECT_HOME_ACT);
                    Log.e("MemberHomeAct에서 Socket 연결 5. 서비스로 보낼 메시지 객체 생성", "" + msg);

                    // 메시지의 송신자를 넣어준다.
                    msg.replyTo = mActivityMessenger;
                    Log.e("MemberHomeAct에서 Socket 연결 6-1. 메시지를 처리할 메신저 객체 넣어주기 msg.replyTo = mActivityMessenger", "" + mActivityMessenger);

                    Bundle bundle = msg.getData();
                    bundle.putString("userId", memberInfo.getUserId());
                    Log.e("TrainerHomeAct에서 Socket 연결 6-2. 채팅방정보를 가져올 userId 넣어줌", "" + memberInfo.getUserId());

                    // 서비스에 연결됐다는 메시지를 전송한다.
                    mServiceMessenger.send(msg);
                    Log.e("MemberHomeAct에서 Socket 연결 7. 서비스에 메시지 전송", "" + msg);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            // 예상치 못하게 연결이 종료되는 경우 호출되는 콜백 함수
            public void onServiceDisconnected(ComponentName className) {
                // 서비스의 메시지를 받는 메신저를 null로 변경
                mServiceMessenger = null;
                // 연결이 종료되었으므로 연결여부를 false로 봐꿔준다.
                isBound = false;
            }
        };
    }

    /** 파일정보를 저장하는 AsyncTask*/
    public class GetChatRoomInfo extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<ChatRoomInfoForListDto> response;
        private ChatRoomInfoForListDto chatRoomInfo;
        private ArrayList<ChatRoomInfoForListDto> chatRoomList;
        private String latestMsg;
        private String latestMsgTime;

        public GetChatRoomInfo(ArrayList<ChatRoomInfoForListDto> chatRoomList, String latestMsg, String latestMsgTime) {
            this.chatRoomList = chatRoomList;
            this.latestMsg = latestMsg;
            this.latestMsgTime = latestMsgTime;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("채팅방리스트에서 정보 변경 - 9. onPreExecute", "onPreExecute");
        }

        @Override
        protected String doInBackground(Call[] params) {
            try {
                Call<ChatRoomInfoForListDto> call = params[0];
                response = call.execute();
                chatRoomInfo = response.body();

                Log.e("채팅방리스트에서 정보 변경 - 10. doInBackground", "doInBackground");

                return response.body().toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("채팅방리스트에서 정보 변경 - 11. onPostExecute", "onPostExecute");
            Log.e("채팅방리스트에서 정보 변경 - 12. chatRoomInfo.getChattingRoomId()", "" + chatRoomInfo.getChattingRoomId());


            // 채팅방 아이디가 존재한다면 해당 유저가 포함된 채팅방이 있는 것임!
            if(chatRoomInfo.getChattingRoomId() != null){
                // 3. 채팅방이 존재하지 않는다면 채팅방 생성 - 첫번쨰 위치에 채팅방 데이터 추가

                // 메시지 정보 세팅
                chatRoomInfo.setLatestMsg(latestMsg);
                Log.e("채팅방리스트에서 정보 변경 - 13. setLatestMsg", latestMsg);

                chatRoomInfo.setLatestMsgTime(latestMsgTime);
                Log.e("채팅방리스트에서 정보 변경 - 14. latestMsgTime", latestMsgTime);

                // 수정한 채팅방정보 맨위로 올리기
                chatRoomList.add(0, chatRoomInfo);

                Log.e("채팅방리스트에서 정보 변경 - 15. add", "" + chatRoomList.size());

                // viewModel에 저장하기
                chattingViewModel.getChattingList().setValue(chatRoomList);
            }
        }
    }
}