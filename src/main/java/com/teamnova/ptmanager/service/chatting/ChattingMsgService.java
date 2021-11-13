package com.teamnova.ptmanager.service.chatting;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.manager.ChattingRoomManager;
import com.teamnova.ptmanager.model.chatting.ChatMsgInfo;
import com.teamnova.ptmanager.model.chatting.ChatRoomInfoForListDto;
import com.teamnova.ptmanager.model.chatting.ChattingMemberDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.chatting.ChattingService;
import com.teamnova.ptmanager.ui.chatting.ChattingActivity;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.util.GetDate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

/** 메시지 송수신 및 알림 시 사용하는 서비스*/
public class ChattingMsgService extends Service {
    // 채팅상대 초대
    public static final int INVITE = 1;
    // 채팅방 나가기
    public static final int EXIT = 2;
    // 텍스트 전송
    public static final int SEND_TEXT = 3;
    // 이미지 전송
    public static final int SEND_IMAGE = 4;
    // 읽지 않은 사람수 업데이트
    public static final int NOT_READ_USER_UPDATE = 5;
    // 홈 액티비티와 연결
    public static final int CONNECT_HOME_ACT = 6;
    // 채팅 액티비티와 연결
    public static final int CONNECT_CHAT_ACT = 7;
    // 채팅 액티비티 - 서버 연결 해제
    public static final int DISCONNECTED_CHAT_ACT = 8;
    // 서버로부터 메시지 받음
    public static final int MSG_RECEIVE_FROM_SERVER = 10;
    // 메시지의 마지막 인덱스 보내기
    public static final int SEND_LAST_MSG = 11;

    // 바인딩된 액티비티들과 통신하기위해 필요한 메신저들을 저장하는 리스트
    ArrayList<Messenger> mActivityMessengerList = new ArrayList<>();

    // 메시지 수신 시 채팅방에 뿌려주는 핸들러
    private Handler mHandler;
    // 메시지 송신 시(송신은 다른 쓰레드에서 진행) 메인 쓰레드의 ui를 변경하기 위해 데이터를 전달하는 핸들러
    private Handler sendHandler;
    private String ip = "192.168.0.11";
    private int port = 8888;
    // 채팅방 아이디
    private String chatRoomId;
    // 수신쓰레드
    public static RecvThread recvThread;
    // 수신 reader
    public static  BufferedReader input;
    // 채팅참여자 정보
    private ChattingMemberDto userInfo;
    // SP에 저장된 마지막 메시지 인덱스
    private int lastMsgIdx;
    // 채팅방에 들어왔던적이 있는지 없는지를 나타내는 문자열
    private String firstOrOld;

    // 서비스에서 액티비티로 보내는 메신저 인것 같다.
    // 클라이언트가 IncomingHandler에 메시지를 보내기 위해 게시하는 대상
    private Messenger mServiceMessenger;

    // 서버와 통신하기위한 retrofit 객체
    private Retrofit retrofit = RetrofitInstance.getRetroClient();

    // ChatAct의 활성화 여부
    private boolean isChatActivityActive;

    // 메시지 아이디
    private static int msgID = 0;

    // 소켓!
    public static Socket socket = null;
    public static PrintWriter sendWriter = null;

    // 이 사용자의 채팅방 리스트
    private ArrayList<ChatRoomInfoForListDto> chatRoomList;


    // 액티비티 -> 서비스에 데이터를 전송할 때 이것을 처리하는 핸들러
    // handleMessage 함수에서 switch에 맞는 코드가 동작하는 식으로 CallBack을 구현하는 것 같다.
    class IncomingHandler extends Handler {
        private String send;


        @Override
        // 이 메소드는 뭐하는 녀석일까? 오버라이드 받은 녀석이긴 한데 말이지
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 채팅상대 초대
                case INVITE:
                    Log.e("채팅방에서 사용자 초대 19. 채팅방에서 회원 초대 시 알려주기 위해 서비스 접근!", "true");

                    // 초대한 채팅상대방 정보
                    ChattingMemberDto memberInfo = (ChattingMemberDto) msg.getData().getSerializable("chattingMemberDto");
                    Log.e("채팅방에서 사용자 초대 19. memberInfo.getUserId()", memberInfo.getUserId());

                    // 입장인사 메시지 생성
                    String enterMsg = memberInfo.getUserName() + "님이 입장했습니다.";
                    Log.e("채팅방에서 사용자 초대 20. enterMsg", enterMsg);

                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                sendWriter.println("new" + ":" + memberInfo.getUserId() + ":" + chatRoomId + ":" + enterMsg + ":" + GetDate.getTodayDateWithTime());
                                sendWriter.flush();
                                Log.e("채팅방에서 사용자 초대 21. 서버로 메시지 전달!", "new" + ":" + memberInfo.getUserId() + ":" + chatRoomId + ":" + enterMsg + ":" + GetDate.getTodayDateWithTime());

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    break;
                // 채팅방 나가기
                case EXIT:
                    // 서버에 사용자가 나갔음을 알려라!
                    String exitMsg = userInfo.getUserName() + "님이 나갔습니다.";
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                sendWriter.println("exit" + ":" + userInfo.getUserId() + ":" + chatRoomId + ":" + exitMsg + ":" + GetDate.getTodayDateWithTime());
                                sendWriter.flush();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                    break;
                // 문자열 전송
                case SEND_TEXT:
                    String sendMsg = msg.getData().getString("sendMsg");

                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                // 채팅을 입력한 경우에만 전송되도록
                                if (sendMsg != null && !sendMsg.equals("")) {
                                    // 서버로 텍스트만 전송
                                    sendWriter.println(userInfo.getUserName() + ":" + userInfo.getUserId() + ":" + chatRoomId + ":" + sendMsg + ":" + GetDate.getTodayDateWithTime());
                                    sendWriter.flush();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    break;
                // 이미지 전송
                case SEND_IMAGE:
                    RequestBody userIdReq = RequestBody.create(MediaType.parse("text/plain"), userInfo.getUserId());
                    RequestBody chatRoomIdReq = RequestBody.create(MediaType.parse("text/plain"), chatRoomId);
                    ArrayList<MultipartBody.Part> list = (ArrayList<MultipartBody.Part>) msg.getData().getSerializable("list");

                    ChattingService service = retrofit.create(ChattingService.class);

                    // http request 객체 생성
                    Call<ArrayList<String>> call = service.insertFileList(userIdReq, chatRoomIdReq, list);

                    System.out.println("transferFileToServer들어옴");

                    new InsertFileInfo().execute(call);

                    break;
                // 읽지 않은 사람수 업데이트
                case NOT_READ_USER_UPDATE:
                    break;
                // 홈 액티비티와 연결
                case CONNECT_HOME_ACT:
                    // 액티비티와 연결된 경우 그것과 통신할 수 있는 메신저를 저장한다.
                    mActivityMessengerList.add(msg.replyTo);

                    Log.e("HomeAct에서 Socket 연결 8. 서비스에서 mActivityMessengerList에 HOME ACT와 연결된 메신저 넣기", "" + mActivityMessengerList.get(0));


                    // 소켓 연결
                    recvThread = new RecvThread();
                    Log.e("HomeAct에서 Socket 연결 9. 수신쓰레드 생성", recvThread.toString());

                    recvThread.start();

                    // 채널 생성
                    createNotificationChannel();
                    Log.e("HomeAct에서 Socket 연결 13-1. notification 채널 생성", "true");

                    // 이 사용자의 채팅방리스트 가져오기
                    chatRoomList = new ChattingRoomManager().getChatRoomList(msg.getData().getString("userId"));
                    Log.e("HomeAct에서 Socket 연결 13-2. 채팅방 리스트 가져 옴", "" + chatRoomList.size());

                    break;
                // 채팅 액티비티와 연결
                case CONNECT_CHAT_ACT:
                    // 액티비티와 연결된 경우 그것과 통신할 수 있는 메신저를 저장한다.
                    mActivityMessengerList.add(msg.replyTo);
                    Log.e("ChatAct와 소켓 연결 12. 메신저리스트에 새로운 메신저 추가 ","" + mActivityMessengerList.size());

                    // 채팅방 아이디를 등록한다.
                    chatRoomId = msg.getData().getString("chatRoomId");
                    Log.e("ChatAct와 소켓 연결 13. chatRoomId ","" + chatRoomId);

                    // 채팅방 참여자 정보를 등록한다.
                    userInfo = (ChattingMemberDto) msg.getData().getSerializable("userInfo");
                    Log.e("ChatAct와 소켓 연결 14. userInfo ","" + userInfo.getUserId());

                    // SP에 저장된 메시지의 마지막 인덱스
                    lastMsgIdx = msg.getData().getInt("lastMsgIdx");
                    Log.e("ChatAct와 소켓 연결 15. lastMsgIdx ","" + lastMsgIdx);

                    // 채팅방에 처음들어오는지 들어왔던적이 있는지를 나타내는 문자열
                    firstOrOld = msg.getData().getString("firstOrOld");
                    Log.e("ChatAct와 소켓 연결 16. firstOrOld ","" + firstOrOld);

                    // 채팅 액티비티의 활성화여부 = true를 넣어줌
                    isChatActivityActive = true;
                    Log.e("ChatAct와 소켓 연결 17. isChatActivityActive ","" + isChatActivityActive);


                    /*recvThread = new RecvThread();
                    recvThread.start();*/

                    // 채널 생성
                    //createNotificationChannel();

                    /** 채팅방id 전송
                     * */
                    // 텍스트 메시지를 전송하기 위해 필요한 객체
                    try{
                        sendWriter = new PrintWriter(socket.getOutputStream());
                        Log.e("ChatAct와 소켓 연결 18. sendWriter 생성","" + sendWriter);

                    }catch (IOException e){
                        e.printStackTrace();
                    }

                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                //채팅방 정보 보내기
                                sendWriter.println("!@#$chatRoomIdAndUserId:" + chatRoomId + ":" + userInfo.getUserId());
                                Log.e("ChatAct와 소켓 연결 19. 채팅방 입장정보 보내기",chatRoomId + ":" + userInfo.getUserId());

                                sendWriter.flush();


                                // 마지막으로 저장된 메시지가 있다면 메시지 인덱스 보내기
                                if(lastMsgIdx != 999998){
                                    sendWriter.println("!@#$!@#lsatIdx:"+ userInfo.getUserId() + ":" + chatRoomId + ":" + lastMsgIdx + ":" + firstOrOld);
                                    //sendToServerLastIdx(lastMsgIdx, firstOrOld);
                                    sendWriter.flush();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();


                    break;
                // 채팅 액티비티 - 서버 연결 해제
                case DISCONNECTED_CHAT_ACT:
                    // 채팅 액티비티의 활성화여부 = false 를 넣어줌
                    isChatActivityActive = false;
                    Log.e("채팅방 나갈 때 서비스와의 연결 제거 4-1. isChatActivityActive false로 변경", "" + isChatActivityActive);

                    // 메신저 제거!
                    mActivityMessengerList.remove(1);
                    Log.e("채팅방 나갈 때 서비스와의 연결 제거 4-2. 메신저 제거!", "" + mActivityMessengerList.size());

                    // 채팅방에서 뒤로가기 시 해당 채팅방과의 연결을 끊어줌
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                sendWriter.println("!@#$connectionExpire:");
                                Log.e("채팅방 나갈 때 서비스와의 연결 제거 5. 서버로 채팅방과의 연결해제 하라는 메시지 전송", "" + "!@#$connectionExpire:");

                                sendWriter.flush();

                                // 닫아야 하지 않을까? 근데 닫아도 수신소켓은 그대로 있나?
                                //sendWriter.close();
                                Log.e("채팅방 나갈 때 서비스와의 연결 제거 6. 소켓 닫기", "true");

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    break;
                // 서버로부터 메시지 받음
                case MSG_RECEIVE_FROM_SERVER:
                    break;
            }
        }
    }

    // noti를 보내기 위한 채널 생성
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "notification";
            String description = "notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    // 서비스에 바인딩할 때 인터페이스를 메신저로 반환합니다
    // 서비스에 메시지를 보내기 위해
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();

        // 메신저의 매개변수로 핸들러를 받는 구나
        mServiceMessenger = new Messenger(new IncomingHandler()); // 매신저의 매개변수로 핸들러
        return mServiceMessenger.getBinder(); // <- 매신저에 바인더는 자동으로 들어가 있는건가?
    }

    /** 수신 쓰레드*/
    private class RecvThread extends Thread{
        @Override
        public void run() {
            try {
                // 서버의 ip주소를 이용하여 InetAddress객체 생성
                InetAddress serverAddr = InetAddress.getByName(ip);
                Log.e("HomeAct에서 Socket 연결 10. 서버와 연결하기 위한 serverAddr 생성", serverAddr.toString());

                // 서버에 소켓연결 요청 후 성공 시 통신을 위한 소켓을 생성한다.
                socket = new Socket(serverAddr, port);
                Log.e("HomeAct에서 Socket 연결 11. 서버와 연결하기 위한 socket 생성", socket.toString());

                // 텍스트 메시지를 전송받는 경우
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Log.e("HomeAct에서 Socket 연결 12. 메시지 수신을 위한한 input 생성", input.toString());

                /* sendWriter = new PrintWriter(socket.getOutputStream());
                sendWriter.println("!@#$chatRoomIdAndUserId:" + chatRoomId + ":" + userInfo.getUserId());
                sendWriter.flush();


                // 마지막으로 저장된 메시지가 있다면 메시지 인덱스 보내기
                if(lastMsgIdx != 999998){
                    sendToServerLastIdx(lastMsgIdx, firstOrOld);
                }*/

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
                    } else {
                        System.out.println("소켓 닫혀 있음!");
                    }

                    // 수신한 메시지가 있다면!
                    if(read!=null){
                        // 안읽은 사용자 --
                        if(read.contains("!@#$!@#lsatIdx:")){
                            // 새로 입장한 사용자가 이전에 가장 마지막으로 읽은 메시지 idx
                            int preLastIdx = Integer.parseInt(read.split(":")[1]);

                            // 안읽은 사용자수를 업데이트 하기위해 직전 메시지 인덱스 전송
                            Message msg = Message.obtain(null, ChattingMsgService.NOT_READ_USER_UPDATE); // 이건 생성자나 마찬가지 라는 것 같음
                            Bundle bundle = msg.getData();
                            bundle.putInt("preLastIdx", preLastIdx);

                            // 메신저 리스트와 연결된 모든 액트에 데이터 보내기!
                            for(Messenger m1 : mActivityMessengerList){
                                try {
                                    Message tempMsg = new Message();
                                    tempMsg.copyFrom(msg);
                                    m1.send(tempMsg);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }

                            // 아래 코드로 넘어가지 않도록
                            continue;
                        }

                        // 서버로부터 받은 메시지 액티비티로 전송
                        Message msg = Message.obtain(null, ChattingMsgService.MSG_RECEIVE_FROM_SERVER);
                        Log.e("메시지 전송 1. 액티비티로 보내기 위한 메시지 객체 생성", msg.toString());

                        Bundle bundle = msg.getData();
                        bundle.putString("message", read);

                        Log.e("메시지 전송 2. 서버로부터 받은 메시지", read);

                        if(read.contains("allMsg!@#:")){
                            try{
                                mActivityMessengerList.get(0).send(msg);
                                Log.e("알림용 메시지 전송 3-1. 서버로부터 받은 알림용 메시지 HomeAct로 전송 HomeAct의 메신저",mActivityMessengerList.get(0).toString());

                            }catch (RemoteException e){
                                e.printStackTrace();
                            }
                        } else {
                            try{
                                mActivityMessengerList.get(1).send(msg);
                                Log.e("채팅용 메시지 전송 3-2. 서버로부터 받은 채팅용 메시지 ChatAct로 전송 ChatAct의 메신저",mActivityMessengerList.get(1).toString());

                            }catch (RemoteException e){
                                e.printStackTrace();
                            }
                        }

                        Log.e("메시지 전송 4. 채팅방 액트활성화 여부: ", "" + isChatActivityActive);

                        // 채팅 액티비티가 비활성화 상태라면 알림을 보낸다.
                        if(!isChatActivityActive){
                            // 채팅방의 사이즈 만큼 반복 한다.
                            Log.e("메시지 전송 5-1. 채팅방 아이디: ", "" + read.split(":")[3]);

                            for (int i = 0; i < chatRoomList.size(); i++){
                                // msg가 채팅방 아이디와 동일하다면 즉, 내 채팅방 리스트에 존재하는 채팅방이라면 알림을 보낸다.
                                if(chatRoomList.get(i).getChattingRoomId().equals(read.split(":")[3])){
                                    Log.e("메시지 전송 5-2. 알림 보내기: ", "" + read.split(":")[3]);

                                    Intent intent = new Intent(ChattingMsgService.this, ChattingActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(ChattingMsgService.this, 0 /* Request code */, intent,
                                            PendingIntent.FLAG_ONE_SHOT);

                                    Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(ChattingMsgService.this, "1")
                                            .setSmallIcon(R.mipmap.ic_launcher)
                                            .setContentTitle(read.split(":")[1])
                                            .setContentText(read.split(":")[4])
                                            .setSound(defaultSoundUri)
                                            .setContentIntent(pendingIntent)
                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);


                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ChattingMsgService.this);

                                    // notificationId is a unique int for each notification that you must define
                                    notificationManager.notify(msgID, builder.build());
                                    msgID++;
                                    break;
                                }
                            }
                        }
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
                        System.out.println("소켓종료1");
                    }

                    if(sendWriter != null){
                        sendWriter.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /** 채팅방에 입장했을 때 다른 채팅방의 안읽은사용자수 -- 해주기위해 내가 가지고 있는 마지막 인덱스를 보내준다
     * 파라미터: 1) 마지막 메시지 인덱스
     *          2) 채팅방에 처음으로 입장했는지 여부 처음입장: first 재입장: old 보내 줌
     * */
    public void sendToServerLastIdx(int lastIdx, String firstOrOld){
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

    // 서버로 파일을 전송한다.
    public void transferFileToServer(RequestBody userIdReq, RequestBody chatRoomIdReq, ArrayList<MultipartBody.Part> list) {
        /** 서버로 파일리스트 전송*/
        ChattingService service = retrofit.create(ChattingService.class);

        // http request 객체 생성
        Call<ArrayList<String>> call = service.insertFileList(userIdReq, chatRoomIdReq, list);

        System.out.println("transferFileToServer들어옴");

        new InsertFileInfo().execute(call);
    }

    // ** 서버로 옮기기
    /** 파일정보를 저장하는 AsyncTask*/
        public class InsertFileInfo extends AsyncTask<Call, Void, String> {
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

                /** 파일 메시지 리스트를 저장하면 저장경로 리스트를 가져온다.*/
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
                                if(userInfo != null){
                                    sendWriter.println(userInfo.getUserName() +":"+ userInfo.getUserId() + ":" + chatRoomId + ":" + "사진" + ":" + GetDate.getTodayDateWithTime() + ":" + msgIdx + ":" + finalSavePathListStr);
                                    sendWriter.flush();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                    // 마지막으로 보낸 메시지 정보를 저장한다.
                    ChatMsgInfo chatMsgInfo = new ChatMsgInfo(null, userInfo.getUserId(), userInfo.getUserName(), chatRoomId, "사진", GetDate.getTodayDateWithTime(), 0, msgIdx, savePathListStr,null,"N");

                    // 마지막 메시지 전송
                    Message msg = Message.obtain(null, ChattingMsgService.SEND_LAST_MSG);
                    Bundle bundle = msg.getData();
                    bundle.putSerializable("lastMsg", chatMsgInfo);

                    // 메신저 리스트와 연결된 모든 액트에 데이터 보내기!
                    for(Messenger m1 : mActivityMessengerList){
                        try {
                            Message tempMsg = new Message();
                            tempMsg.copyFrom(msg);
                            m1.send(tempMsg);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    for(int i = 0; i < savePathList.size(); i++) {
                        System.out.println("사진 저장 실패 " + i + ": "+ savePathList.get(i));
                    }
                }
            }
    }

    private void exit(){
        new ExpireThread().start();

        recvThread.interrupt();
    }
}
