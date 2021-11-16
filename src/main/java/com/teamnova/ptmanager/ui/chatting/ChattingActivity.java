package com.teamnova.ptmanager.ui.chatting;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityChattingRoomBinding;
import com.teamnova.ptmanager.manager.ChattingRoomManager;
import com.teamnova.ptmanager.model.chatting.ChatMsgInfo;
import com.teamnova.ptmanager.model.chatting.ChatRoomInfoDto;
import com.teamnova.ptmanager.model.chatting.ChatRoomInfoForListDto;
import com.teamnova.ptmanager.model.chatting.ChattingMemberDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.chatting.ChattingService;
import com.teamnova.ptmanager.service.chatting.ChattingMsgService;
import com.teamnova.ptmanager.ui.chatting.adapter.ChattingMsgListAdapter;
import com.teamnova.ptmanager.ui.home.member.MemberHomeActivity;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.util.GetDate;
import com.teamnova.ptmanager.viewmodel.chatting.ChattingMsgViewModel;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

/** 채팅방 화면*/
public class ChattingActivity extends AppCompatActivity {
    private ActivityChattingRoomBinding binding;
    // db에서 가져온 채팅방 정보를 저장하는 변수
    private ChatRoomInfoDto chatRoomInfoDto;
    // Intent에 저장되어있는 사용자 정보를 저장할 변수
    private ChattingMemberDto userInfo;
    // 채팅방 아이디
    private String chatRoomId;
    // 채팅방이 새로 생성된 경우 true
    private boolean isRoomAdded = false;

    // 서버와 통신하기위한 retrofit 객체
    private Retrofit retrofit;
    // 메시지 수신 시 채팅방에 뿌려주는 핸들러
    private Handler mHandler;
    // 메시지 송신 시(송신은 다른 쓰레드에서 진행) 메인 쓰레드의 ui를 변경하기 위해 데이터를 전달하는 핸들러
    private Handler sendHandler;

    // 서버의 ip - 소켓통신을 위해 필요
    //private String ip = "172.30.20.1";
    // 서버의 port - 소켓통신을 위해 필요
    //private int port = 8888;
    // 텍스트 전송자 - 사용자가 작성한 메시지를 서버에 전송하기 위해 필요
    //private PrintWriter sendWriter;
    // 수신쓰레드 - 서버가 전송한 메시지를 수신하는 쓰레드
    //private RecvThread recvThread;
    // 서버로부터 전송된 메시지를 수신하는 reader
    //private BufferedReader input;


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

    // 마지막으로 저장된 인덱스
    private int lastMsgIdx = 999998;

    // 마지막으로 수신한 메시지 - 뒤로가기 클릭 시 채팅방 정보 갱신하기 위함
    private ChatMsgInfo lastMsg;

    // 채팅방에 처음입장 or 재입장여부를 알려줄 문자열(서버에서 읽음처리할 쿼리에서 사용 됨)
    private String firstOrOld;

    // 클라이언트시간 - 서버시간
    public static long timeDifference;

    // 촬영한 사진을 저장할 경로
    private String mCurrentPhotoPath;

    /** 사진 정보를 가져올 때 사용할 상수 */
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_ALBUM_PICK = 2;

    // 사진 파일
    private File photoFile;

    // 페이징 시 게시물 수
    private int limit = 10;
    // 가져올 페이지
    private int pageNo = 1;

    // 메시지리스트를 담을 리스트
    private ArrayList<ChatMsgInfo> chatMsgList;
    
    // 서비스와 통신하기 위한 메신저
    private Messenger mServiceMessenger = null;

    // 서비스로 보낼 메신저
    final Messenger mActivityMessenger = new Messenger(new ActivityHandler());

    // 서비스와 연결됐는지 여부
    boolean isBound = false;

    // 서비스의 상태에 따라 콜백 함수를 호출하는 객체.
    private ServiceConnection conn;

    // 서비스로부터 받은 메시지를 처리할 핸들러
    class ActivityHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            // msg.what에는 메시지 종류가 들어있음(서비스에서 설정)
            switch(msg.what){
                // 안 읽은 사용자수 업데이트
                case  ChattingMsgService.NOT_READ_USER_UPDATE:
                    // msg객체에 들어있는 Bundle 객체를 가져옴
                    Bundle bundle = msg.getData();
                    // Bundle객체에 들어있는 데이터를 가져 옴
                    // 새로 입장한 사용자가 이전에 가장 마지막으로 읽은 메시지 idx
                    int preLastIdx= bundle.getInt("preLastIdx");

                    // preLastIdx보다 큰 인덱스를 가지는 모든 메시지의 안 읽은 사용자수 -1 업데이트
                    chattingMsgListAdapter.updateNotReadUserCountMinus(preLastIdx);
                    break;
                // 메시지 수신
                case ChattingMsgService.MSG_RECEIVE_FROM_SERVER:
                    // 서비스로부터 받은 수신한 메시지 객체
                    bundle = msg.getData();
                    String read = bundle.getString("message");
                    Log.e("수신한 메시지 리사이클러뷰에 뿌리기 1. 수신한 메시지: ",read);

                    // 수신한 메시지를 저장할 객체
                    ChatMsgInfo chatMsgInfo = null;

                    // 서버에서 메시지 전송 시 ":"를 구분자로 하여 데이터를 전송하므로 이것을 기준으로 split!
                    String[] msgArr = read.split(":");
                    Log.e("수신한 메시지 리사이클러뷰에 뿌리기 2. msgArr: ",msgArr.toString());

                    // 내가보낸 메시지가 아니라면
                    // 다른사람이 보낸 메시지 갯수
                    // 텍스트: 12개
                    // 이미지: 13개
                    if(msgArr.length > 10){
                        Log.e("수신한 메시지 리사이클러뷰에 뿌리기 3. 메시지: ", "true");

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
                        if(msgArr.length == 12){
                            Log.e("수신한 메시지 리사이클러뷰에 뿌리기 4-1. 텍스트 메시지: ", "true");

                            notReadUserCountPos = msgArr.length-2;
                            Log.e("수신한 메시지 리사이클러뷰에 뿌리기 4-2. notReadUserCountPos: ", "" + notReadUserCountPos);

                            msgIdxPos = msgArr.length-1;
                            Log.e("수신한 메시지 리사이클러뷰에 뿌리기 4-3. msgIdxPos: ", "" + msgIdxPos);

                        } else { // 파일포함 메시지
                            Log.e("수신한 메시지 리사이클러뷰에 뿌리기 5-1. 이미지 메시지: ", "true");

                            creDatetime = msgArr[msgArr.length - 4] + ":" + msgArr[msgArr.length - 3]  + ":" + msgArr[msgArr.length - 2];
                            Log.e("수신한 메시지 리사이클러뷰에 뿌리기 5-2. creDatetime: ", creDatetime);

                            notReadUserCountPos = msgArr.length-5;
                            Log.e("수신한 메시지 리사이클러뷰에 뿌리기 5-3. notReadUserCountPos: ", "" + notReadUserCountPos);

                            msgIdxPos = msgArr.length-6;
                            Log.e("수신한 메시지 리사이클러뷰에 뿌리기 5-4. msgIdxPos: ", "" + msgIdxPos);

                            savePathPos = msgArr.length-1;
                            Log.e("수신한 메시지 리사이클러뷰에 뿌리기 5-5. savePathPos: ", "" + savePathPos);
                        }

                        // 뒤에서 두번째에 안읽은 사람수가 있음
                        int notReadUserCount = Integer.parseInt(msgArr[notReadUserCountPos]);
                        Log.e("수신한 메시지 리사이클러뷰에 뿌리기 6. notReadUserCount: ", "" + notReadUserCount);

                        int msgIdx = Integer.parseInt(msgArr[msgIdxPos]);
                        Log.e("수신한 메시지 리사이클러뷰에 뿌리기 7. msgIdx: ", "" + msgIdx);

                        // 파일이 존재한다면 저장경로 위치 != 0
                        if(savePathPos != 0){
                            savePath = msgArr[savePathPos];
                            Log.e("수신한 메시지 리사이클러뷰에 뿌리기 7-1. 파일이 존재한다면 저장경로 위치: ", "" + savePath);

                        }

                        chatMsgInfo = new ChatMsgInfo(null, userId, userName, roomId, msg2, creDatetime,notReadUserCount,msgIdx,savePath,null,"N");
                        Log.e("수신한 메시지 리사이클러뷰에 뿌리기 8. 출력할 메시지 객체: ", "" + chatMsgInfo);

                        // 마지막 메시지 저장
                        lastMsg = chatMsgInfo;
                        Log.e("수신한 메시지 리사이클러뷰에 뿌리기 9. 마지막 메시지 저장: ", "" + lastMsg);

                        // 내가 트레이너 이고 나간 사용자가 있을 때 그 사용자를 리스트에서 제거
                        Log.e("수신한 메시지 리사이클러뷰에 뿌리기 10-1. msg2: ", "" + msg2);
                        Log.e("수신한 메시지 리사이클러뷰에 뿌리기 10-2. TrainerHomeActivity.staticLoginUserInfo: ", "" + TrainerHomeActivity.staticLoginUserInfo);

                        if(msg2.contains("나갔습니다") && TrainerHomeActivity.staticLoginUserInfo != null){
                            Log.e("수신한 메시지 리사이클러뷰에 뿌리기 10-3. userId: ", "" + userId);
                            for(int i = 0; i < chatMemberList.size(); i++){
                                Log.e("수신한 메시지 리사이클러뷰에 뿌리기 10-4. chatMemberList.get(i): ", "" + chatMemberList.get(i));

                                if(userId.equals(chatMemberList.get(i).getUserId())){
                                    Log.e("수신한 메시지 리사이클러뷰에 뿌리기 10-5. 나간 사용자 삭제 사용자 id: ", "" + chatMemberList.get(i));
                                    chatMemberList.remove(i);
                                    break;
                                }
                            }
                        }
                    } else {
                        // 내가보낸 메시지
                        // 텍스트: 읽지않은사용자수, 메시지 인덱스, 서버 수신시간, 채팅방아이디
                        // 파일: 읽지않은사용자수, 메시지 인덱스, 서버 수신시간, 저장경로, 채팅방아이디
                        boolean isText = (msgArr.length == 7);

                        Log.e("내가보낸 메시지 리사이클러뷰에 뿌리기 1. 메시지 텍스트 여부: ", "" + isText);

                        int notReadUserCount = Integer.parseInt(msgArr[0]);
                        Log.e("내가보낸 메시지 리사이클러뷰에 뿌리기 2. notReadUserCount: ", "" + notReadUserCount);

                        int msgIdx = Integer.parseInt(msgArr[1]);
                        Log.e("내가보낸 메시지 리사이클러뷰에 뿌리기 3. msgIdx: ", "" + msgIdx);

                        String savePath = null;

                        String creDateTime = msgArr[2] + ":" + msgArr[3] + ":" + msgArr[4];
                        Log.e("내가보낸 메시지 리사이클러뷰에 뿌리기 4. creDateTime: ", "" + creDateTime);

                        String msg2 = null;

                        // 어댑터에서 가장 마지막에있는 메시지 업데이트!
                        // 리사이클러뷰의 업데이트는 main UI에서만 가능!!!!
                        //System.out.println("서버로부터 메시지222: " + read);
                        // 이미지라면
                        if(!isText) {
                            savePath = msgArr[5];
                            msg2 =  msgArr[7];
                            Log.e("내가보낸 메시지 리사이클러뷰에 뿌리기 4-1. savePath: ", "" + savePath);
                        } else {
                            // 텍스트라면
                            msg2 = msgArr[6];
                        }

                        chatMsgInfo = new ChatMsgInfo(null, null, null, chatRoomId, msg2, creDateTime,notReadUserCount,msgIdx, savePath,null,"N");
                        Log.e("내가보낸 메시지 리사이클러뷰에 뿌리기 5. chatMsgInfo: ", chatMsgInfo.toString());

                        if(lastMsg != null){
                            lastMsg.setNotReadUserCount(notReadUserCount);
                            Log.e("내가보낸 메시지 리사이클러뷰에 뿌리기 6. lastMsg.setNotReadUserCount: ", "" + notReadUserCount);

                            lastMsg.setMsgIdx(msgIdx);
                            Log.e("내가보낸 메시지 리사이클러뷰에 뿌리기 7. lastMsg.setMsgIdx: ", "" + msgIdx);

                        }

                    }

                    // 액티비티로 데이터 전송
                    //mHandler.post(new MsgUpdater(chatMsgInfo));

                    // 상대방에게서 온 메시지
                    if(chatMsgInfo.getChattingMemberId() != null){
                        Log.e("수신한 메시지 리사이클러뷰에 뿌리기 10. 리사에 뿌리기 ", "" + chatMsgInfo.getMsg());

                        // 사진파일이라면
                        if(chatMsgInfo.getMsg().equals("사진")){
                            Log.e("수신한 메시지 리사이클러뷰에 뿌리기 10-1. 사진이라면 ", "" + chatMsgInfo.getSavePath());
                        }

                        // 어댑터에 추가
                        chattingMsgListAdapter.addChatMsgInfo(chatMsgInfo);
                        Log.e("수신한 메시지 리사이클러뷰에 뿌리기 15. 리사에 추가 완료! ", "true");

                        // 마지막 아이템으로 이동
                        layoutManager.scrollToPosition(chattingMsgListAdapter.getItemCount() - 1);
                        Log.e("수신한 메시지 리사이클러뷰에 뿌리기 16. 마지막으로 위치 이동! ", "" + (chattingMsgListAdapter.getItemCount() - 1));

                    } else { // 내가 작성한 메시지
                        chattingMsgListAdapter.updateCntAndIdx(chatMsgInfo.getNotReadUserCount(), chatMsgInfo.getMsgIdx(), chatMsgInfo.getCreDatetime(), chatMsgInfo.getSavePath());
                        Log.e("내가보낸 메시지 리사이클러뷰에 뿌리기 8. updateCntAndIdx: ", "true");

                    }
                    break;
                // 마지막 메시지 수신
                case ChattingMsgService.SEND_LAST_MSG:
                    lastMsg = (ChatMsgInfo) msg.getData().getSerializable("lastMsg");
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityChattingRoomBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        // 새로운 인원 초대
        startActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // 새로 초대한 인원 정보를 가져온다!
                            ArrayList<ChattingMemberDto> addedChatMemberList= (ArrayList<ChattingMemberDto>)result.getData().getSerializableExtra("chatMemberList");
                            Log.e("채팅방에서 사용자 초대 8.addedChatMemberList.size", "" + addedChatMemberList.size());

                            // 서버에 해당 사용자들을 추가한다.
                            chattingRoomManager.insertMemberList(addedChatMemberList,chatRoomId);
                            Log.e("채팅방에서 사용자 초대 14. insertMemberList 완료! & 기존 채팅방에 있는 회원들 사이즈!", "" + chatMemberList.size());

                            // 기존 채팅참여자 리스트에 새로 초대된 사용자들을 추가하고 서버에 새로 추가된 사용자를 알려주는 메시지를 보낸다.
                            for(ChattingMemberDto c : addedChatMemberList){
                                // 기존에 존재하는 채팅참여자리스트에 새로 추가된 사용자의 정보를 추가해준다.
                                chatMemberList.add(c);

                                Log.e("채팅방에서 사용자 초대 15. 기존 리스트에 초대된 인원 추가", "" + chatMemberList.size());

                                // 서비스에게 보낼 메시지를 생성한다.
                                // 파라미터: 핸들러, msg.what에 들어갈 int값
                                // 채팅상대 초대
                                Message msg = Message.obtain(null, ChattingMsgService.INVITE);
                                Log.e("채팅방에서 사용자 초대 16. 서비스에 전달할 메시지 객체 생성", "" + msg.what);

                                Bundle bundle = msg.getData();

                                // 서비스에 전달할 텍스트를 넣는다.
                                bundle.putSerializable("chattingMemberDto", c);

                                Log.e("채팅방에서 사용자 초대 17. 서비스에 전달할 메시지 객체에 채팅멤버 객체 넣기", "" + c);

                                // 서비스에 연결됐다는 메시지를 전송한다.
                                try{
                                    mServiceMessenger.send(msg);
                                    Log.e("채팅방에서 사용자 초대 18. 서비스에 메시지 전송", "true");

                                }catch (RemoteException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });

        super.onCreate(savedInstanceState);

        initialize();
    }

    /** 초기화 */
    public void initialize(){
        // 채팅방 매니저
        chattingRoomManager = new ChattingRoomManager();

        // 서버의 현재 시간
        long serverTime = getCurrentServerTime();
        // 클라이언트의 현재 시간
        long clientTime = System.currentTimeMillis();

        // 서버시간 - 클라이언트 시간: 서버와 클라이언트의 시간차이를 보정하기 위해 이 값을 구함
        timeDifference = clientTime - serverTime;

        // "초대 버튼" 트레이너인 경우에만 나오도록
        if(TrainerHomeActivity.staticLoginUserInfo != null) { //트레이너 라면
            binding.chatButtonInvite.setVisibility(View.VISIBLE);
        } else {
            binding.chatButtonInvite.setVisibility(View.GONE);
        }

        // 서버와 통신하기 위한 retrofit 객체의 인스턴스 가져오기
        retrofit= RetrofitInstance.getRetroClient();

        // 채팅방 id 가져오기
        chatRoomId = (String)getIntent().getSerializableExtra("chatRoomId");

        // 채팅참여자 리스트
        chatMemberList = chattingRoomManager.getChatMemberList(getIntent(), chatRoomId);

        // 채팅 참여자의 프로필 사진을 저장할 map
        userProfileBitmapMap = new HashMap<>();

        // 채팅참여자 수만큼 반복하여 프로필사진을 맵에 저장한다.
        for(int i = 0, ii = chatMemberList.size(); i < ii; i++){
            // 채팅 참여자정보에 프로필 사진 경로가 들어있다.
            String profileId = chatMemberList.get(i).getProfileId();
            String userId = chatMemberList.get(i).getUserId();

            // 프로필 사진이 존재한다면 맵에 경로를 넣어준다.
            if(profileId != null) {
                userProfileBitmapMap.put(userId, profileId);
            } else { // 프로필 사진이 존재하지 않는다면(프로필 사진 설전 안되어있음)
                     // null값을 넣어 기본사진이 출력되도록 한다.
                userProfileBitmapMap.put(userId,null);
            }
        }

        // 채팅방 및 메시지를 관리할 뷰모델
        chattingMsgViewModel = new ViewModelProvider(this).get(ChattingMsgViewModel.class);

        // 채팅방 아이디가 없다면 친구목록에서 접근한 것이다 => 서버에서 채팅방을 생성해야 한다.
        if(chatRoomId == null) {
            // 채팅멤버리스트의 첫번째값에 채팅방을 생성한 사용자의 값이 들어있다.
            userInfo = chatMemberList.get(0);

            // 기존에 생성된 채팅방이 있는지 검색 후 존재 시 채팅방 아이디를 가져온다
            // 채팅방 검색 시 채팅방 참여자 리스트를 사용해서 검색한다.
            chatRoomId = chattingRoomManager.getExistedChatRoomId(chatMemberList);

            // 서버에서 가져온 채팅방 id의 경우 "(큰따옴표)가 포함되어있어 이를 제거해준다.
            chatRoomId = chatRoomId.replace("\"","");

            // 채팅방 아이디가 존재한다면
            if(chatRoomId != null && !chatRoomId.equals("null")){

            } else {
                // 기존에 생성된 채팅방 정보가 존재하지 않는다면
                // 채팅방 정보를 저장하라
                // (저장하고 채팅방 아이디를 가져올 때까지 mainThread는 멈춰 있어야 함)
                chatRoomId = chattingRoomManager.insertChatRoomInfo(chatMemberList,chatRoomId);

                // 새로운 채팅방이 생성되어 채팅방리스트에 추가해야 한다.
                isRoomAdded = true;
            }
        } else { // 채팅 참여자리스트가 없다면 채팅방 리스트에서 기존에 생성되어있는 채팅방에서 접근한 것이다.
            // 사용자 정보 가져오기
            userInfo = (ChattingMemberDto) getIntent().getSerializableExtra("userInfo");
        }

        /** 리사이클러뷰 세팅 */
        recyclerView = binding.recyclerViewChatArea;
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // 방이 새로 생성된게 아니라면 서버에서 메시지리스트 가져오기
        if(!isRoomAdded){
            // 빈 ArrayList 생성
            chatMsgList = new ArrayList<>();

            // 빈 ArrayList 넘겨 줌 => 페이징해서 가져온 데이터를 하나하나 add해줄 것임!
            chattingMsgListAdapter = new ChattingMsgListAdapter(chatMsgList, this, null, userInfo.getUserId(), userProfileBitmapMap);

            // 리사이클러뷰에 아답터를 넘겨 줌
            recyclerView.setAdapter(chattingMsgListAdapter);

            // 페이징된 데이터 리스트를 가져와 리사이클러뷰에 뿌려준다.
            getDataList();

            // 맨처음 데이터를 가져올 땐 제일 마지막으로 포커싱해야 함
            layoutManager.scrollToPosition(chattingMsgListAdapter.getItemCount() - 1);

            /** 채팅데이터가 있는 경우 안읽은 메시지 --해준다 */
            // sp에 저장된 마지막 메시지 인덱스를 가져온다.
            // 메시지를 작성하거나 수신할 때마다 sp에 해당 메시지의 인덱스를 저장해준다.
            // 따라서 새로 생성된 채팅방이 아닌 경우 채팅방id에 해당하는 마지막 메시지 인덱스가 존재하고 그것을 가져온다.
            lastMsgIdx = getLastMsgIdx(chatRoomId);
            Log.e("채팅방 들어왔을 때 마지막 메시지 idx 저장 lastMsgIdx", "" + lastMsgIdx);

            // 맨처음 채팅방에 입장한 경우 이 값이 존재하지 않는다. 따라서 가져온 메시지의 첫번째 값을 넘겨준다.(임시용 임)
            // 메시지 받았을 때 채팅방 리스트에 채팅방을 생성하는 코드를 구현하면 그때 받은 (메시지 idx -1)값을 sp에 넣어 줄 것이기 때문에 이 코드는 사용 안 할 거임
            // * 여기 로직은 나중에 수정하기
            if(lastMsgIdx == 999999){
                if(chatMsgList.size() > 0){
                    lastMsgIdx = chatMsgList.get(0).getMsgIdx();
                }

                // 해당 채팅방에 처음 입장!
                firstOrOld = "first";

            } else {
                // 해당 채팅방에 재입장!
                firstOrOld = "old";
            }

            // 초기값이 아니면 즉 마지막 메시지의 인덱스가 존재한다면
            if(lastMsgIdx != 999999) {

                // 마지막 인덱스 이후의 모든 메시지에 대해 내 화면에서 안읽은 사용자수를 -- 해준다.
                chattingMsgListAdapter.updateNotReadUserCountMinus(lastMsgIdx);

                //가장 마지막 메시지의 인덱스를 sp에 업데이트 해준다.
                saveMsgInfoToSharedPreference(chatMsgList.get(chatMsgList.size() - 1));
            }

            // 조회한 가장 마지막 메시지 저장
            lastMsg = chatMsgList.get(chatMsgList.size() - 1);
        } else{ // 방이 새로생성된 경우 아답터만 세팅해준다(가져올 메시지리스트가 없기 때문!)
            // 아답터를 생성한다.
            chattingMsgListAdapter = new ChattingMsgListAdapter(chatMsgList, this, null, userInfo.getUserId(), userProfileBitmapMap);

            // 아답터를 세팅한다.
            recyclerView.setAdapter(chattingMsgListAdapter);
        }

        // 채팅방 아이디(서버에서 가져온 문자열의 경우 "가 포함되어있어 제거해 줌)
        chatRoomId = chatRoomId.replace("\"","");

        // 채팅방 정보 가져오기
        chatRoomInfoDto = chattingRoomManager.getChatRoomInfo(chatRoomId);

        // 어댑터에 서버시간과의 차이 업데이트
        chattingMsgListAdapter.setTimeDiffer(this.timeDifference);

        // 리사이클러뷰 업데이트
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 리사의 맨위에 도달했다면 데이터 가져오기
                if(!recyclerView.canScrollVertically(-1)){
                    // 리사이클러뷰에서 눈에
                    int firstVisibleItemPos = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

                    // 페이지의 끝에 도달했다면 데이터 가져오기!
                    if(firstVisibleItemPos == 0){
                        // 페이지 넘버를 올려 줌
                        pageNo++;
                        getDataList();
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                /*// 현재 보이는 것 중 마지막 아이템의 위치값
                int firstVisibleItemPos = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

                System.out.println("firstVisibleItemPos" + firstVisibleItemPos);

                // 페이지의 끝에 도달했다면 데이터 가져오기!
                if(firstVisibleItemPos == 0){
                    // 페이지 넘버를 올려 줌
                    pageNo++;
                    getDataList();
                    System.out.println("들어옴!");
                }*/

            }
        });

        // 서비스 커넥션 생성
        conn = new ServiceConnection() {
            // 서비스와 연결된 경우 호출
            // IBinder 서비스의 onBind에서 반환한 메신저 객체
            public void onServiceConnected(ComponentName className, IBinder service) {
                // 서비스와 연결된 메신저 객체 생성
                mServiceMessenger = new Messenger(service);

                Log.e("ChatAct와 소켓 연결 2. mServiceMessenger 생성", mServiceMessenger.toString());


                // 연결여부를 true로 변경해준다.
                isBound = true;
                Log.e("ChatAct와 소켓 연결 3. isBound ", "isBound");

                try {
                    // 서비스에게 보낼 메시지를 생성한다.
                    // 파라미터: 핸들러, msg.what에 들어갈 int값
                    Message msg = Message.obtain(null, ChattingMsgService.CONNECT_CHAT_ACT);
                    Log.e("ChatAct와 소켓 연결 4. 서비스에 보내기 위한 메시지 객체 생성 ", msg.toString());

                    // msg객체에 들어있는 Bundle 객체를 가져옴
                    Bundle bundle = msg.getData();
                    Log.e("ChatAct와 소켓 연결 5. 데이터를 전달하기 위한 Bundle객체 생성 ", bundle.toString());

                    // 서비스에 필요한 데이터를 넘겨 줌
                    // 1. chatRoomId
                    bundle.putString("chatRoomId", chatRoomId);
                    Log.e("ChatAct와 소켓 연결 6. chatRoomId: ", chatRoomId);
                    // 2. userInfo
                    bundle.putSerializable("userInfo", userInfo);
                    Log.e("ChatAct와 소켓 연결 7. userInfo: ", userInfo.getUserId());

                    // 3. lastMsgIdx
                    bundle.putInt("lastMsgIdx", lastMsgIdx);
                    Log.e("ChatAct와 소켓 연결 8. lastMsgIdx: ", "" + lastMsgIdx);

                    // 4. firstOrOld
                    bundle.putString("firstOrOld", firstOrOld);
                    Log.e("ChatAct와 소켓 연결 9. firstOrOld: ", firstOrOld);


                    // 메시지의 송신자를 넣어준다.
                    msg.replyTo = mActivityMessenger;
                    Log.e("ChatAct와 소켓 연결 10. 서비스와 통신하기 위한 메신저: ", mActivityMessenger.toString());

                    // 서비스에 연결됐다는 메시지를 전송한다.
                    mServiceMessenger.send(msg);
                    Log.e("ChatAct와 소켓 연결 11. 서비스에 메시지 전송: ",msg.toString());

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

        // 서비스에 바인드 한다.
        // Intent와 ServiceConnection 객체를 파라미터로 넣는다.
        getApplicationContext().bindService(new Intent(getApplicationContext(), ChattingMsgService.class), conn, Context.BIND_AUTO_CREATE);
        Log.e("ChatAct와 소켓 연결 1. bindService", "true");

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

                Log.e("채팅방에서 사용자 초대 1.userId", userInfo.getUserId());
                Log.e("채팅방에서 사용자 초대 2.chatMemberList.size()", "" + chatMemberList.size());

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

                        // 나간 회원 지우기
                        chatMemberList.remove(userInfo);

                        // SharedPreference 데이터 지우기
                        removeSpData(chatRoomId);

                        // 트레이너, 회원 여부에 따라 이동하는 액티비티가 달라져야 함
                        Intent intent = null;

                        if(TrainerHomeActivity.staticLoginUserInfo != null) { //트레이너 라면
                            intent = new Intent(ChattingActivity.this, TrainerHomeActivity.class);
                        } else{
                            intent = new Intent(ChattingActivity.this, MemberHomeActivity.class);
                        }

                        // 채팅방 정보
                        ChatRoomInfoForListDto exitedChatRoomInfo = new ChatRoomInfoForListDto(chatRoomInfoDto.getChattingRoomId(), chatRoomInfoDto.getChattingRoomName(), null, null,chatMemberList.size(),-1);

                        intent.putExtra("exitedChatRoomInfo", exitedChatRoomInfo);

                        // 서버에 사용자가 나갔음을 알려라!
                        //String exitMsg = userInfo.getUserName() + "님이 나갔습니다.";

                        // 서비스로 데이터 전송
                        // 파라미터: 핸들러, msg.what에 들어갈 int값
                        // 채팅방 나가겠다!
                        Message msg = Message.obtain(null, ChattingMsgService.EXIT);

                        // 서비스에 연결됐다는 메시지를 전송한다.
                        try{
                            mServiceMessenger.send(msg);
                        }catch (RemoteException e){
                            e.printStackTrace();
                        }

                        // ** 서비스로 데이터 전송
                        /*new Thread() {
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
                        }.start();*/

                        // 소켓 연결 끊기
                        //exit();

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

        binding.message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    // 마지막으로 이동
                    layoutManager.scrollToPosition(chattingMsgListAdapter.getItemCount() - 1);
                }
            }
        });

        /** 사진선택 버튼 클릭 시 직접촬역 혹은 갤러리에서 사진선택 다이얼로그 출력*/
        binding.btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChattingActivity.this);

                builder.setTitle("이미지 전송");

                builder.setItems(R.array.photoOrImageOrRemove, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int pos)
                    {
                        if(pos == 0){ //직접촬영
                            dispatchTakePictureIntent();
                        } else { //갤러리에서 가져오기
                            doTakeMultiAlbumAction();
                        }
                    }
                });

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

        // ** 서비스로 옮기기
        /*recvThread = new RecvThread();
        recvThread.start();
*/
        /** 메시지 송신
         *  전송버튼 클릭 시 실행되는 부분
         * */
        binding.chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendMsg = binding.message.getText().toString();

                // 서비스에게 보낼 메시지를 생성한다.
                // 파라미터: 핸들러, msg.what에 들어갈 int값
                // 텍스트 메시지 전송
                Message msg = Message.obtain(null, ChattingMsgService.SEND_TEXT);

                Bundle bundle = msg.getData();

                // 서비스에 전달할 텍스트를 넣는다.
                bundle.putSerializable("sendMsg", sendMsg);

                // 서버에 전송할 텍스트 메시지를 서비스로 전송한다.
                try{
                    mServiceMessenger.send(msg);
                }catch (RemoteException e){
                    e.printStackTrace();
                }

                // 메시지의 마지막 인덱스 저장하기 - 뒤로가기 시 sp에 저장
                lastMsg = new ChatMsgInfo(null, userInfo.getUserId(), userInfo.getUserName(), chatRoomId, sendMsg, GetDate.getTodayDateWithTime(), 0, 0, "",null,"N");

                // 리사이클러뷰에 뿌려주기
                chattingMsgListAdapter.addChatMsgInfo(new ChatMsgInfo(null, userInfo.getUserId(), userInfo.getUserName(), chatRoomId, sendMsg, GetDate.getTodayDateWithTime(),0,0, "",null,"N"));
                layoutManager.scrollToPosition(chattingMsgListAdapter.getItemCount() - 1);
                binding.message.setText("");
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

    /** 메시지 수신 시 SharedPreference에 메시지 정보 저장*/
    public void saveMsgInfoToSharedPreference(ChatMsgInfo msg){
        SharedPreferences sp = getSharedPreferences("chat", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        // 해당 채팅방 id로 메시지 인덱스를 저장해준다
        editor.putInt(msg.getChattingRoomId(), msg.getMsgIdx());

        editor.commit();
        Log.e("수신한 메시지 리사이클러뷰에 뿌리기 18. SP에 저장, 저장한 인덱스: ", "" + sp.getInt(msg.getChattingRoomId(),999999));
    }

    /** 특정 채팅방의 마지막 메시지 idx를 가져와라*/
    public int getLastMsgIdx(String chatRoomId){
        SharedPreferences sp = getSharedPreferences("chat", Activity.MODE_PRIVATE);
        int idx = sp.getInt(chatRoomId,999999);

        return idx;
    }


    /** 채팅방에 입장했을 때 다른 채팅방의 안읽은사용자수 -- 해주기위해 내가 가지고 있는 마지막 인덱스를 보내준다
     * 파라미터: 1) 마지막 메시지 인덱스
     *          2) 채팅방에 처음으로 입장했는지 여부 처음입장: first 재입장: old 보내 줌
     * */
    /*public void sendToServerLastIdx(int lastIdx, String firstOrOld){
        // ** 서비스로 데이터 전송
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sendWriter.println("!@#$!@#lsatIdx:"+ userInfo.getUserId() + ":" + chatRoomId + ":" + lastIdx + ":" + firstOrOld);
                    sendWriter.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }*/


    // 뒤로가기 버튼 클릭 시 수신쓰레드 종료, 서버에 종료신호 보내기
    @Override
    public void onBackPressed() {
        // 뒤로가기 시 새로 생성된 방 정보 보내기
        Intent intent = null;

        if(TrainerHomeActivity.staticLoginUserInfo != null) { //트레이너 라면
            intent = new Intent(ChattingActivity.this, TrainerHomeActivity.class);
        } else{
            intent = new Intent(ChattingActivity.this, MemberHomeActivity.class);
        }

        // 메시지를 보내서 채팅방이 수정되거나 새로 생성한 경우 채팅방 정보를 담기위한 변수
        // 채팅방 리스트 화면으로 넘겨줘서 리스트를 업데이트 한다.
        ChatRoomInfoForListDto addedOrModifiedChatRoomInfo = null;


        // 채팅방 정보
        if(lastMsg != null){
            // 채팅방이 수정된 경우
            addedOrModifiedChatRoomInfo = new ChatRoomInfoForListDto(chatRoomInfoDto.getChattingRoomId(), chatRoomInfoDto.getChattingRoomName(), lastMsg.getMsg(), lastMsg.getCreDatetime(),chatMemberList.size(),lastMsg.getMsgIdx());
        } else {
            // 채팅방이 새로 생성된 경우
            addedOrModifiedChatRoomInfo = new ChatRoomInfoForListDto(chatRoomInfoDto.getChattingRoomId(), chatRoomInfoDto.getChattingRoomName(), null, null,chatMemberList.size(),-1);
        }

        Log.e("뒤로가기 시 보내는 msg", "" + lastMsg);
        Log.e("뒤로가기 시 보내는 msg", "" + lastMsg.getMsgIdx());

        // 새로 생성 혹은 수정된 채팅방 정보 전송
        intent.putExtra("addedOrModifiedChatRoomInfo", addedOrModifiedChatRoomInfo);

        setResult(Activity.RESULT_OK, intent);
        //exit();
        finish();

        super.onBackPressed();
    }

    // 서비스로 옮기기
    /** 연결 종료 시 */
    /*private void exit(){
        new ExpireThread().start();

        recvThread.interrupt();
    }*/

    /** 채팅방 나가면 shared도 지우기*/
    public void removeSpData(String chatRoomId){
        SharedPreferences sp = getSharedPreferences("chat", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        // 나간 채팅방의 sp 정보를 삭제한다.
        editor.remove(chatRoomId);

        editor.commit();
    }

    /** 서버의 현재시간 가져오는 메소드*/
    public long getCurrentServerTime(){
        return chattingRoomManager.getCurrentServerTime();
    }


    /** 메시지를 화면에 업데이트 하는 쓰레드*/
    /*class MsgUpdater implements Runnable{
        private ChatMsgInfo msgInfo;

        public MsgUpdater(ChatMsgInfo msgInfo) {
            this.msgInfo = msgInfo;
        }

        @Override
        public void run() {
            // 상대방에게서 온 메시지
            if(msgInfo.getMsg() != null){
                // 사진파일이라면
                if(msgInfo.getMsg().equals("사진")){
                    System.out.println(msgInfo.getSavePath());
                }

                // 어댑터에 추가
                chattingMsgListAdapter.addChatMsgInfo(msgInfo);
                // 마지막 아이템으로 이동
                layoutManager.scrollToPosition(chattingMsgListAdapter.getItemCount() - 1);

            } else { // 내가 작성한 메시지
                chattingMsgListAdapter.updateCntAndIdx(msgInfo.getNotReadUserCount(), msgInfo.getMsgIdx(), msgInfo.getCreDatetime(), msgInfo.getSavePath());
            }
        }
    }*/

    /** 안읽은 사용자 수를 업데이트 하는 쓰레드*/
    /*class NotReadUserValueUpdater implements Runnable{
        private int lastIdx;

        public NotReadUserValueUpdater(int lastIdx) {
            this.lastIdx = lastIdx;
        }

        @Override
        public void run() {
            // 안 읽은 사용자수 업데이트
            chattingMsgListAdapter.updateNotReadUserCountMinus(lastIdx);
        }
    }*/


    /** 메시지 송신 시 화면 업데이트 하는 쓰레드*/
    class UiUpdater implements Runnable{
        private String sendMsg;
        private Thread thread;

        public UiUpdater(String sendMsg, Thread thread) {
            this.sendMsg = sendMsg;
            this.thread = thread;
        }

        @Override
        public void run() {
            // 리사이클러뷰에 뿌여주기
            // 처음에 뿌려줄 땐 읽지않은 사용자 수와 msgIdx를 0으로 초기화! => 추후에 서버에서 읽지않은 사용자수와 msgIdx값을 받아와서 업데이트 해줘야한다!
            synchronized (thread){
                chattingMsgListAdapter.addChatMsgInfo(new ChatMsgInfo(null, userInfo.getUserId(), userInfo.getUserName(), chatRoomId, sendMsg, GetDate.getTodayDateWithTime(),0,0, "",null,"N"));
                layoutManager.scrollToPosition(chattingMsgListAdapter.getItemCount() - 1);
                binding.message.setText("");

                thread.notify();
            }
        }
    }

    // * 서비스로 옮기기
    // 서버에 소켓을 닫으라고 명령하는 쓰레드
    /*private class ExpireThread extends Thread{
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
    }*/

    // * 서비스로 옮기기
    /** 수신 쓰레드*/
    /*private class RecvThread extends Thread{
        Socket socket = null;

        @Override
        public void run() {
            try {
                // 서버의 ip주소를 이용하여 InetAddress객체 생성
                InetAddress serverAddr = InetAddress.getByName(ip);
                // 서버에 소켓연결 요청 후 성공 시 통신을 위한 소켓을 생성한다.
                socket = new Socket(serverAddr, port);

                // 텍스트 메시지를 전송하기 위해 필요한 객체
                sendWriter = new PrintWriter(socket.getOutputStream());

                // 텍스트 메시지를 전송받는 경우
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                *//** 채팅방id 전송
                 * *//*
                sendWriter.println("!@#$chatRoomIdAndUserId:" + chatRoomId + ":" + userInfo.getUserId());
                sendWriter.flush();


                *//** 마지막으로 저장된 메시지가 있다면 메시지 인덱스 보내기*//*
                if(lastMsgIdx != 999998){
                    sendToServerLastIdx(lastMsgIdx, firstOrOld);
                }


                // 메시지 수신하는 쓰레드
                while(true){
                    // 텍스트 메시지 전송
                    String read = null;

                    // 소켓이 열려있고 메시지를 받을 수 있는 상태라면
                    if(!socket.isClosed() && !input.ready()){
                        try {
                            // 스트림에서 수신한 데이터를 읽는다.
                            read = input.readLine();
                        } catch (SocketException e){
                        }
                    }

                    // 수신한 메시지가 있다면!
                    if(read!=null){
                        // 안읽은 사용자 --
                        if(read.contains("!@#$!@#lsatIdx:")){
                            // 새로 입장한 사용자가 이전에 가장 마지막으로 읽은 메시지 idx
                            int preLastIdx = Integer.parseInt(read.split(":")[1]);

                            // ** 액티비티로 메시지 전송
                            // 안읽은 사용자수 값 업데이트
                            mHandler.post(new NotReadUserValueUpdater(preLastIdx));

                            // 아래 코드로 넘어가지 않도록
                            continue;
                        }

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
                           String msg = msgArr[3];
                           String creDatetime = msgArr[msgArr.length - 5] + ":" + msgArr[msgArr.length - 4]  + ":" + msgArr[msgArr.length - 3];
                           int notReadUserCountPos = 0;
                           int msgIdxPos = 0;
                           int savePathPos = 0;
                           String savePath = null;

                            // 텍스트 메시지
                            if(msgArr.length == 12){
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

                            chatMsgInfo = new ChatMsgInfo(null, userId, userName, roomId, msg, creDatetime,notReadUserCount,msgIdx,savePath,null,"N");

                            // 마지막 메시지 저장
                            lastMsg = chatMsgInfo;

                            // 내가 트레이너 이고 나간 사용자가 있을 때 그 사용자를 리스트에서 제거
                           if(msg.contains("나갔습니다") && TrainerHomeActivity.staticLoginUserInfo != null){
                               for(int i = 0; i < chatMemberList.size(); i++){
                                   if(userId.equals(chatMemberList.get(i))){
                                       chatMemberList.remove(i);
                                   }
                               }
                           }
                       } else { 
                           // 내가보낸 메시지
                           // 텍스트: 읽지않은사용자수, 메시지 인덱스, 서버 수신시간
                           // 파일: 읽지않은사용자수, 메시지 인덱스, 서버 수신시간, 저장경로
                           boolean isText = (msgArr.length == 5);

                           int notReadUserCount = Integer.parseInt(msgArr[0]);
                           int msgIdx = Integer.parseInt(msgArr[1]);
                           String savePath = null;

                           String creDateTime = msgArr[2] + ":" + msgArr[3] + ":" + msgArr[4];

                           // 어댑터에서 가장 마지막에있는 메시지 업데이트!
                           // 리사이클러뷰의 업데이트는 main UI에서만 가능!!!!
                           //System.out.println("서버로부터 메시지222: " + read);
                           // 텍스트 메시지라면
                           if(!isText) {
                               savePath = msgArr[5];
                           }

                           chatMsgInfo = new ChatMsgInfo(null, null, null, chatRoomId, null, creDateTime,notReadUserCount,msgIdx, savePath,null,"N");

                           if(lastMsg != null){
                               lastMsg.setNotReadUserCount(notReadUserCount);
                               lastMsg.setMsgIdx(msgIdx);
                           }

                       }

                       // 액티비티로 데이터 전송
                        mHandler.post(new MsgUpdater(chatMsgInfo));

                        // SharedPreference에 가장 마지막 메시지 인덱스 저장
                        saveMsgInfoToSharedPreference(chatMsgInfo);
                    } else{
                        // null이 들어오면 반복문 나가기
                        break;
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

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/

    // 직접촬영
    private void dispatchTakePictureIntent() {
        // 특정 권한에 대한 허용을 했는지 여부
        int permissionCheck = ContextCompat.checkSelfPermission(ChattingActivity.this, Manifest.permission.CAMERA);

        if(permissionCheck == PackageManager.PERMISSION_DENIED){ //카메라 권한 없음
            ActivityCompat.requestPermissions(ChattingActivity.this,new String[]{Manifest.permission.CAMERA},0);
        }else{ //카메라 권한 있음
            // 카메라 앱 액티비티를 여는 인텐트 생성
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) { }
                if(photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this, "com.teamnova.ptmanager.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }
    }

    private void doTakeMultiAlbumAction(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        // 프로필 사진 고를 땐 여러장 못고르도록.
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        startActivityForResult(intent,REQUEST_ALBUM_PICK);
    }

    // 이미지 전송
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 직접 촬영
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (resultCode == RESULT_OK) {
                photoFile = new File(mCurrentPhotoPath);

                Bitmap bitmap;

                // api 레벨이 27 이상이면
                if (Build.VERSION.SDK_INT > 27) {
                    ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), Uri.fromFile(photoFile));

                    try {
                        bitmap = ImageDecoder.decodeBitmap(source);

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                        // 사진을 압축
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), byteArrayOutputStream.toByteArray());
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                        // 리사이클러뷰에 뿌리기 위한 비트맵
                        Bitmap bitmapImg = BitmapFactory.decodeByteArray(byteArrayOutputStream.toByteArray(), 0, byteArrayOutputStream.toByteArray().length ) ;

                        // 서버로 전송하기 위한 MultipartBody.Part 객체 생성
                        MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("msgFile[]", "JPEG_" + timeStamp + "_.jpg" ,requestBody);

                        // 내 리사이클러뷰에 뿌려주기
                        // ChatMsgInfo 객체 만들기
                        ChatMsgInfo msgInfo = new ChatMsgInfo(null, userInfo.getUserId(), userInfo.getUserName(), chatRoomId, "사진", GetDate.getTodayDateWithTime(), 0, 0, bitmapImg, "N");

                        chattingMsgListAdapter.addChatMsgInfo(msgInfo);
                        layoutManager.scrollToPosition(chattingMsgListAdapter.getItemCount() - 1);

                        ArrayList<MultipartBody.Part> list = new ArrayList<>();
                        list.add(uploadFile);

                        // 서버로 전송하기
                        transferFileToServer(userInfo.getUserId(), chatRoomId, list);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(photoFile));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            // 갤러리에서 가져오기
        } else if(requestCode == REQUEST_ALBUM_PICK && resultCode == RESULT_OK){
            ClipData clipData = data.getClipData();

            // 선택한 사진이 한장이라면 clipData존재 X
            if(clipData == null){
                try {
                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    // 사진을 압축
                    img.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), byteArrayOutputStream.toByteArray());

                    // 리사이클러뷰에 뿌리기 위한 비트맵
                    Bitmap bitmapImg = BitmapFactory.decodeByteArray(byteArrayOutputStream.toByteArray(), 0, byteArrayOutputStream.toByteArray().length ) ;

                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                    // 서버로 전송하기 위한 MultipartBody.Part 객체 생성
                    MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("msgFile[]", "JPEG_" + timeStamp + "_.jpg" ,requestBody);

                    // 내 리사이클러뷰에 뿌려주기
                    ChatMsgInfo msgInfo = new ChatMsgInfo(null, userInfo.getUserId(), userInfo.getUserName(), chatRoomId, "사진", GetDate.getTodayDateWithTime(), 0, 0, bitmapImg,"N");

                    chattingMsgListAdapter.addChatMsgInfo(msgInfo);
                    layoutManager.scrollToPosition(chattingMsgListAdapter.getItemCount() - 1);


                    ArrayList<MultipartBody.Part> list = new ArrayList<>();
                    list.add(uploadFile);

                    // 서버로 전송하기
                    transferFileToServer(userInfo.getUserId(), chatRoomId, list);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                InputStream in = null;
                ArrayList<Bitmap> imgList = new ArrayList<>();
                ArrayList<MultipartBody.Part> multiPartList = new ArrayList<>();

                for(int i = 0; i < clipData.getItemCount(); i++){
                    Uri uri =  clipData.getItemAt(i).getUri();
                    try {
                        in = getContentResolver().openInputStream(uri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap img = BitmapFactory.decodeStream(in);
                    imgList.add(img);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    // 사진을 압축
                    img.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), byteArrayOutputStream.toByteArray());

                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                    // 리사이클러뷰에 뿌리기 위한 비트맵
                    Bitmap bitmapImg = BitmapFactory.decodeByteArray(byteArrayOutputStream.toByteArray(), 0, byteArrayOutputStream.toByteArray().length ) ;

                    // 서버로 전송하기 위한 MultipartBody.Part 객체 생성
                    MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("msgFile[]", "JPEG_" + i + timeStamp + "_.jpg" ,requestBody);

                    // 내 리사이클러뷰에 뿌려주기
                    ChatMsgInfo msgInfo = new ChatMsgInfo(null, userInfo.getUserId(), userInfo.getUserName(), chatRoomId, "사진", GetDate.getTodayDateWithTime(), 0, 0, bitmapImg,"N");

                    chattingMsgListAdapter.addChatMsgInfo(msgInfo);

                    // 리스트에 추가
                    multiPartList.add(uploadFile);
                }

                layoutManager.scrollToPosition(chattingMsgListAdapter.getItemCount() - 1);

                // 서버로 전송하기
                transferFileToServer(userInfo.getUserId(), chatRoomId, multiPartList);
            }
        }
    }

    // 촬영한 사진을 저장할 파일을 생성한다.
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile( imageFileName, ".jpg", storageDir );
        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }

    // 서버로 파일을 전송한다.
    public void transferFileToServer(String userId, String chatRoomId, ArrayList<MultipartBody.Part> list) {
        RequestBody userIdReq = RequestBody.create(MediaType.parse("text/plain"), userId);
        RequestBody chatRoomIdReq = RequestBody.create(MediaType.parse("text/plain"), chatRoomId);

        Message msg = Message.obtain(null,ChattingMsgService.SEND_IMAGE);

        Bundle bundle = msg.getData();

        // 서비스에 전달할 사진리스트를 넣는다.
        bundle.putSerializable("list", list);

        // 서비스에 연결됐다는 메시지를 전송한다.
        try{
            mServiceMessenger.send(msg);
        }catch (RemoteException e){
            e.printStackTrace();
        }


        // ** 서비스로 옮기기
        // userIdReq, chatRoomIdReq ,List 보내기
        /** 서버로 파일리스트 전송*/
        /*ChattingService service = retrofit.create(ChattingService.class);

        // http request 객체 생성
        Call<ArrayList<String>> call = service.insertFileList(userIdReq, chatRoomIdReq, list);

        System.out.println("transferFileToServer들어옴");

        new InsertFileInfo().execute(call);*/
    }
    
    // ** 서버로 옮기기
    /** 파일정보를 저장하는 AsyncTask*/
    /*public class InsertFileInfo extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<ArrayList<String>> response;
        private ArrayList<String> savePathList;

        public InsertFileInfo() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPreExecute", "112222221212321");
        }

        @Override
        protected String doInBackground(Call[] params) {
            try {
                Call<ArrayList<String>> call = params[0];
                response = call.execute();

                Log.d("doInBackground", "1232142142141");

                savePathList = response.body();

                return response.body().toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("onPostExecute", result);

            // 파일 메시지 리스트를 저장하면 저장경로 리스트를 가져온다.
            if (!savePathList.get(0).toUpperCase().contains("FAIL")) {
                // 마지막 값은 메시지 idx이기 때문에 size - 1만큼 반복한다.
                int repeatSize = savePathList.size() - 1;

                // 맨 마지막 값은 msgIdx
                int msgIdx = Integer.parseInt(savePathList.get(savePathList.size() - 1));

                // 저장한 사진 경로들을 ","로 구분해서 서버로 전송한다.
                String savePathListStr = "";

                for(int i = 0; i < repeatSize; i++){
                    savePathListStr += savePathList.get(i) + ",";
                }

                // 맨 뒤의 ","는 지워준다.
                savePathListStr = savePathListStr.substring(0, savePathListStr.length() - 1);

                System.out.println("서버로부터 받은 저장경로 배열로 만듬: " + savePathListStr);

                // 서버로 사진 경로 전송
                String finalSavePathListStr = savePathListStr;

                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            sendWriter.println(userInfo.getUserName() +":"+ userInfo.getUserId() + ":" + chatRoomId + ":" + "사진" + ":" + GetDate.getTodayDateWithTime() + ":" + msgIdx + ":" + finalSavePathListStr);
                            sendWriter.flush();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

                // 마지막으로 보낸 메시지 정보를 저장한다.
                ChatMsgInfo chatMsgInfo = new ChatMsgInfo(null, userInfo.getUserId(), userInfo.getUserName(), chatRoomId, "사진", GetDate.getTodayDateWithTime(), 0, msgIdx, savePathListStr,null,"N");
                lastMsg = chatMsgInfo;
            } else {
                for(int i = 0; i < savePathList.size(); i++) {
                    System.out.println("사진 저장 실패 " + i + ": "+ savePathList.get(i));
                }
            }
        }
    }*/

    // 페이징 된 데이터 가져 옴
    public void getDataList(){
        // pageNo에 해당하는 데이터 리스트 가져 옴
        ArrayList<ChatMsgInfo> tempChatMsgList = chattingMsgViewModel.getMsgListInfo(chatRoomId, userInfo.getUserId(), limit, pageNo);

        // 서버에서 가져온 메시지 리스트의 크기
        int msgSize = tempChatMsgList.size();

        if(pageNo == 1){
            for(int i = (tempChatMsgList.size() - 1); i >= 0; i--) {
                chatMsgList.add(tempChatMsgList.get(i));
            }
        } else{
            // msgIdx가 높은순으로 정렬되어있다.
            // 그런데 가져온 데이터를 역순으로 넣어야 한다. 따라서 계속 0번째에 넣어준다.
            for(int i = 0; i < msgSize; i++) {
                chatMsgList.add(0, tempChatMsgList.get(i));
            }

            // 서버에서 가져온 데이터가 있다면
            if(tempChatMsgList != null){

                // 지정해둔 갯수만큼 메시지를 가져왔다면 즉, 마지막이 아니라면
                if(msgSize == limit){
                    System.out.println("pageNo: " + pageNo);
                    System.out.println("메시지아이디 " + tempChatMsgList.get(0).getChattingMsgId());

                    layoutManager.scrollToPosition(limit);

                    // 페이징해서 가져온 값을 세팅했다고 알려 줌
                    chattingMsgListAdapter.notifyDataSetChanged();
                } else if(msgSize > 0){
                    layoutManager.scrollToPosition(0);

                    // 페이징해서 가져온 값을 세팅했다고 알려 줌
                    chattingMsgListAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        // 액티비티 종료 시 바인딩도 해제하기
        // 바인딩 된 서비스에 해제한다고 알려주기 => 소켓연결 해제하기
        // 서비스에 전달할 메시지를 생성한다.
        // 파라미터: 핸들러, msg.what에 들어갈 int값
        Message msg = Message.obtain(null, ChattingMsgService.DISCONNECTED_CHAT_ACT);
        Log.e("채팅방 나갈 때 서비스와의 연결 제거 1. 서비스로 보낼 메시지 생성", msg.toString());
        Bundle bundle = msg.getData();        // 서비스에게 메시지를 전달한다.
        bundle.putString("chatRoomId", chatRoomId);
        Log.e("채팅방 나갈 때 서비스와의 연결 제거 3. 채팅방 아이디 전송", chatRoomId);

        try {
            mServiceMessenger.send(msg);
            Log.e("채팅방 나갈 때 서비스와의 연결 제거 4. 서비스로 메시지 전송", msg.toString());

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // 앱 종료 시 저절로 서비스와 연결이 끊킴
        /*if(isBound){
            unbindService(conn);
            Log.e("채팅방 나갈 때 서비스와의 연결 제거 3. unbindService", "" + true);
        }*/

        // 서비스와 연결여부
        isBound = false;
        Log.e("채팅방 나갈 때 서비스와의 연결 제거 3. isBound false로 변경", "" + isBound);

        super.onDestroy();
    }
}