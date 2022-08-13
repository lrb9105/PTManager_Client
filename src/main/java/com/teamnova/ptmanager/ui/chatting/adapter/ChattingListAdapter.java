package com.teamnova.ptmanager.ui.chatting.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.model.chatting.ChatRoomInfoForListDto;
import com.teamnova.ptmanager.model.chatting.ChattingMemberDto;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.ui.chatting.ChattingActivity;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.util.GetDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 채팅방 리스트를 담는 리사이클러뷰의 아답터
 * */
public class ChattingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<ChatRoomInfoForListDto> chatRoomList;
    private Context context;
    private ActivityResultLauncher<Intent> startActivity;
    private FriendInfoDto memberInfo;
    private long timeDiffer;
    private boolean shouldCompensate;

    public ChattingListAdapter(ArrayList<ChatRoomInfoForListDto> chatRoomList, Context context, ActivityResultLauncher<Intent> startActivity, FriendInfoDto memberInfo, long timeDiffer, boolean shouldCompensate){
        this.chatRoomList = chatRoomList;
        this.context = context;
        this.startActivity = startActivity;
        this.memberInfo = memberInfo;
        this.timeDiffer = timeDiffer;
        this.shouldCompensate = shouldCompensate;
    }

    // 채팅리스트 정보를 담을 뷰홀더
    public class ChattingListViewHolder extends RecyclerView.ViewHolder {
        TextView chat_room_name;
        TextView latest_msg;
        TextView latest_msg_time;
        TextView chat_user_count;
        TextView not_read_msg_count;
        LinearLayout layout_chat_room;

        public ChattingListViewHolder(View itemView) {
            super(itemView);
            chat_room_name = itemView.findViewById(R.id.chat_room_name);
            latest_msg = itemView.findViewById(R.id.latest_msg);
            latest_msg_time = itemView.findViewById(R.id.latest_msg_time);
            chat_user_count = itemView.findViewById(R.id.chat_user_count);
            not_read_msg_count = itemView.findViewById(R.id.not_read_msg_count);
            layout_chat_room = itemView.findViewById(R.id.layout_chat_room);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemEyeBody = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_room_info, parent, false);
        ChattingListViewHolder vh = new ChattingListViewHolder(itemEyeBody);

        Log.e("채팅방 리스트 정보 리사이클러뷰에 세팅", " 0-1. vh 생성 => " + vh);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatRoomInfoForListDto chatRoomInfo = chatRoomList.get(position);
        Log.e("채팅방 리스트 정보 리사이클러뷰에 세팅", " 1. " + position + "번째 데이터 세팅 시작===============================================================");

        Log.e("채팅방 리스트 정보 리사이클러뷰에 세팅", " 2. 서버에서 가져온 채팅방리스트의 각 채팅방 객체 => " + chatRoomInfo);

        String chatRoomName = chatRoomInfo.getChattingRoomName();
        Log.e("채팅방 리스트 정보 리사이클러뷰에 세팅", " 3-1. 데이터: 채팅방 명 => " + chatRoomName);

        String latestMsg = chatRoomInfo.getLatestMsg();
        Log.e("채팅방 리스트 정보 리사이클러뷰에 세팅", " 3-2. 데이터: latestMsg => " + latestMsg);

        String latestMsgTime = chatRoomInfo.getLatestMsgTime();
        Log.e("채팅방 리스트 정보 리사이클러뷰에 세팅", " 3-3. 데이터: latestMsgTime => " + latestMsgTime);

        int notReadMsgCount = chatRoomInfo.getNotReadMsgCount();
        Log.e("채팅방 리스트 정보 리사이클러뷰에 세팅", " 3-4. 데이터: notReadMsgCount => " + notReadMsgCount);

        int userCount = chatRoomInfo.getUserCount();
        Log.e("채팅방 리스트 정보 리사이클러뷰에 세팅", " 3-5. 데이터: userCount => " + userCount);


        /**
         * 채팅방 정보 셋
         * */
        // 채팅방명
        ((ChattingListViewHolder)holder).chat_room_name.setText(chatRoomName);
        Log.e("채팅방 리스트 정보 리사이클러뷰에 세팅", " 4. vh의 chat_room_name에 채팅방명 세팅 => " + chatRoomName);

        // 최신메시지
        if(chatRoomInfo.getLatestMsg() != null){
            ((ChattingListViewHolder)holder).latest_msg.setText(latestMsg);
            Log.e("채팅방 리스트 정보 리사이클러뷰에 세팅", " 5. vh의 latest_msg에 latest_msg 세팅 => " + latestMsg);
        }

        // 전송시간
        if(chatRoomInfo.getLatestMsgTime() != null) {
            if(shouldCompensate){ //처음 조회시에만 보정
                Log.e("채팅방 리스트 정보 리사이클러뷰에 세팅", " 6. 서버에서 처음 조회한 경우 시간보정 시작");
                ((ChattingListViewHolder) holder).latest_msg_time.setText(chatRoomInfo.getLatestMsgTime().split(" ")[0] + " " + getTime(computeTimeDifferToServer(chatRoomInfo.getLatestMsgTime(), timeDiffer)));
                Log.e("채팅방 리스트 정보 리사이클러뷰에 세팅", " 7. 서버에서 처음 조회한 경우 시간보정 종료");
            } else{ //수정하는 경우는 보정하지 않음!
                Log.e("채팅방 리스트 정보 리사이클러뷰에 세팅", " 6. 수정인 경우 makeTimeYYYYMMDDhhmm 시작");
                ((ChattingListViewHolder) holder).latest_msg_time.setText(chatRoomInfo.getLatestMsgTime().split(" ")[0] + " " + getTime(makeTimeYYYYMMDDhhmm(latestMsgTime)));
                Log.e("채팅방 리스트 정보 리사이클러뷰에 세팅", " 7. 수정인 경우 makeTimeYYYYMMDDhhmm 종료");
            }
        }

        // 읽지 않은 msg수가 0보다 크다면 세팅해준다.
        if( notReadMsgCount> 0){
            Log.e("채팅방 리스트 정보 리사이클러뷰에 세팅", " 8-1. 읽지 않은 메시지 수가 0보다 큰경우");

            ((ChattingListViewHolder) holder).not_read_msg_count.setVisibility(View.VISIBLE);
            Log.e("채팅방 리스트 정보 리사이클러뷰에 세팅", " 8-2. vh의 not_read_msg_count VISIBLE");

            ((ChattingListViewHolder) holder).not_read_msg_count.setText("" + notReadMsgCount);
            Log.e("채팅방 리스트 정보 리사이클러뷰에 세팅", " 8-3. vh의 not_read_msg_count 에 notReadMsgCount 세팅 => " + notReadMsgCount);
        } else {
            Log.e("채팅방 리스트 정보 리사이클러뷰에 세팅", " 8-1. 읽지 않은 메시지 수가 0보다 작은경우");

            ((ChattingListViewHolder) holder).not_read_msg_count.setVisibility(View.GONE);

            Log.e("채팅방 리스트 정보 리사이클러뷰에 세팅", " 8-2. vh의 not_read_msg_count GONE");

        }

        // 유저수
        ((ChattingListViewHolder)holder).chat_user_count.setText("" + userCount);
        Log.e("채팅방 리스트 정보 리사이클러뷰에 세팅", " 9. vh의 chat_user_count에 userCount 세팅 => " + userCount);


        // 레이아웃 클릭
        ((ChattingListViewHolder)holder).layout_chat_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 채팅방화면으로 이동
                enterChatRoom(chatRoomInfo.getChattingRoomId(), makeChatMemberInfo(memberInfo));
                Log.e("채팅방 클릭 시 채팅방 액티비티로 이동", " 1. enterChatRoom 호출");

                // 읽지않은 메시지 수 = 0
                ((ChattingListViewHolder) holder).not_read_msg_count.setText("" + 0);
                Log.e("채팅방 클릭 시 채팅방 액티비티로 이동", " 2. not_read_msg_count 값 0으로 세팅(클릭하면 안읽은 메시지가 없는거니까!)");

                ((ChattingListViewHolder) holder).not_read_msg_count.setVisibility(View.GONE);
                Log.e("채팅방 클릭 시 채팅방 액티비티로 이동", " 3. not_read_msg_count 안보이게 하기");

            }
        });
        Log.e("채팅방 리스트 정보 리사이클러뷰에 세팅", " 10. " + position + "번째 데이터 세팅 종료 ===============================================================");

    }

    @Override
    public int getItemCount() {
        int listSize = chatRoomList.size();

        Log.e("채팅방 리스트 정보 리사이클러뷰에 세팅", " 0-2. 총갯수 카운트 => " + listSize);

        return listSize;
    }

    // 채팅방 추가
    public void addChatRoomInfo(ChatRoomInfoForListDto chatRoomInfo){
        chatRoomList.add(0,chatRoomInfo);

        notifyItemInserted(0);
    }

    // 채팅방 내용 수정
    public void modifyChatRoomInfo(int position, ChatRoomInfoForListDto chatRoomInfo){
        chatRoomList.set(position,chatRoomInfo);
        notifyItemChanged(position);
    }

    // 채팅방 삭제
    public void deleteChatRoomInfo(int position){
        chatRoomList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, chatRoomList.size());
    }


    // 채팅방리스트 반환
    public ArrayList<ChatRoomInfoForListDto> getChatRoomList(){
        return chatRoomList;
    }

    /** 채팅참여자 정보를 생성하라*/
    private ChattingMemberDto makeChatMemberInfo(FriendInfoDto memberInfo){
        ChattingMemberDto chattingMemberDto = null;

        if(memberInfo == null) { // 트레이너 라면
            chattingMemberDto = ChattingMemberDto.makeChatMemberInfo(TrainerHomeActivity.staticLoginUserInfo);
        } else { // 회원이라면
            chattingMemberDto = ChattingMemberDto.makeChatMemberInfo(memberInfo);
        }

        return chattingMemberDto;
    }

    /** 채팅방에 입장하라*/
    private void enterChatRoom(String chatRoomId, ChattingMemberDto chattingMemberDto){
        Intent intent = new Intent(context, ChattingActivity.class);

        intent.putExtra("chatRoomId", chatRoomId);
        intent.putExtra("userInfo", chattingMemberDto);

        startActivity.launch(intent);
    }

    // String(datetime형태) to date
    public Date makeDateFromDatetimeOfString(String datetime){
        Log.e("datetime값 Date 객체로 변환", " 1. 받아온 datetime값 출력 => " + datetime);

        datetime += ":00";
        Log.e("datetime값 Date 객체로 변환", " 2. 받아온 datetime에 ss값 붙여주기 => " + datetime);


        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = null;

        try {
            time = transFormat.parse(datetime);
            Log.e("datetime값 Date 객체로 변환", " 3. Date 객체로 변환 => 반환값: " + time);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;
    }

    // 서버시간과의 차이 보정한 메시지 수신시간 리턴
    public String computeTimeDifferToServer(String datetime, long timeDiffer){
        Log.e("서버에서 처음 조회한 경우 시간보정", " 1. datetime값 Date 객체로 변환 시작");
        Date currentTimeOfDate = makeDateFromDatetimeOfString(datetime);
        Log.e("서버에서 처음 조회한 경우 시간보정", " 2. datetime값 Date 객체로 변환 종료");

        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        int timeDifferSec = (int)(timeDiffer/1000);
        Log.e("서버에서 처음 조회한 경우 시간보정", " 3. 서버와의 시간 차이값 => " + timeDifferSec);

        Calendar cal = Calendar.getInstance();
        Log.e("서버에서 처음 조회한 경우 시간보정", " 4. Calendar 객체 생성 => " + cal);

        cal.setTime(currentTimeOfDate);
        Log.e("서버에서 처음 조회한 경우 시간보정", " 5. Calendar 객체에 현재시간 넣어줌 => 현재시간: " + cal.getTime());

        cal.add(Calendar.SECOND, timeDifferSec);
        Log.e("서버에서 처음 조회한 경우 시간보정", " 6. Calendar 객체에 서버와의 시간차이 보정해줌 => 보정시간: " + sdFormat.format(cal.getTime()));

        return sdFormat.format(cal.getTime());
    }

    // YYYY-MM-DD hh:mm형태의 시간 출력
    public String makeTimeYYYYMMDDhhmm(String datetime){
        Log.e("makeTimeYYYYMMDDhhmm", " 1. datetime값 Date 객체로 변환 시작");
        Date currentTimeOfDate = makeDateFromDatetimeOfString(datetime);
        Log.e("makeTimeYYYYMMDDhhmm", " 2. datetime값 Date 객체로 변환 종ㄽ");

        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Calendar cal = Calendar.getInstance();
        Log.e("makeTimeYYYYMMDDhhmm", " 3. Calendar 객체 생성 => " + cal);

        cal.setTime(currentTimeOfDate);
        Log.e("makeTimeYYYYMMDDhhmm", " 4. Calendar 객체에 현재시간 넣어줌 => 현재시간: " + cal.getTime());

        String date = sdFormat.format(cal.getTime());
        Log.e("makeTimeYYYYMMDDhhmm", " 5. YYYY-MM-DD hh:mm형태의 시간 => " + date);

        return date;
    }

    public String getTime(String creDatetime){
        //System.out.println("22");

        //System.out.println("creDatetime: "  + creDatetime);

        String[] dateArr = creDatetime.split(" ")[1].split(":");

        String time = dateArr[0] + ":" + dateArr[1];

        return GetDate.getAmPmTime(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]));
    }
}
