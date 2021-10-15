package com.teamnova.ptmanager.ui.chatting.adapter;

import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;

/**
 * 채팅방 리스트를 담는 리사이클러뷰의 아답터
 * */
public class ChattingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<ChatRoomInfoForListDto> chatRoomList;
    private Context context;
    private ActivityResultLauncher<Intent> startActivity;
    private FriendInfoDto memberInfo;

    public ChattingListAdapter(ArrayList<ChatRoomInfoForListDto> chatRoomList, Context context, ActivityResultLauncher<Intent> startActivity, FriendInfoDto memberInfo){
        this.chatRoomList = chatRoomList;
        this.context = context;
        this.startActivity = startActivity;
        this.memberInfo = memberInfo;
    }

    // 채팅리스트 정보를 담을 뷰홀더
    public class ChattingListViewHolder extends RecyclerView.ViewHolder {
        TextView chat_room_name;
        TextView latest_msg;
        TextView latest_msg_time;
        LinearLayout layout_chat_room;

        public ChattingListViewHolder(View itemView) {
            super(itemView);
            chat_room_name = itemView.findViewById(R.id.chat_room_name);
            latest_msg = itemView.findViewById(R.id.latest_msg);
            latest_msg_time = itemView.findViewById(R.id.latest_msg_time);
            layout_chat_room = itemView.findViewById(R.id.layout_chat_room);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemEyeBody = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_room_info, parent, false);
        ChattingListViewHolder vh = new ChattingListViewHolder(itemEyeBody);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatRoomInfoForListDto chatRoomInfo = chatRoomList.get(position);

        /**
         * 채팅방 정보 셋
         * */
        // 채팅방명
        ((ChattingListViewHolder)holder).chat_room_name.setText(chatRoomInfo.getChattingRoomName());

        // 최신메시지
        ((ChattingListViewHolder)holder).latest_msg.setText(chatRoomInfo.getLatestMsg());

        // 전송시간
        ((ChattingListViewHolder)holder).latest_msg_time.setText(chatRoomInfo.getLatestMsgTime());

        // 레이아웃 클릭
        ((ChattingListViewHolder)holder).layout_chat_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 채팅방화면으로 이동
                enterChatRoom(chatRoomInfo.getChattingRoomId(), makeChatMemberInfo(memberInfo));
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatRoomList.size();
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
}
