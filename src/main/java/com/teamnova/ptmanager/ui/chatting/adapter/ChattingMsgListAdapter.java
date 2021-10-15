package com.teamnova.ptmanager.ui.chatting.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.model.chatting.ChatMsgInfo;
import com.teamnova.ptmanager.model.chatting.ChatRoomInfoForListDto;
import com.teamnova.ptmanager.util.GetDate;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 채팅방의 메시지를 뿌려주는 리사이클러뷰의 아답터
 * */
public class ChattingMsgListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<ChatMsgInfo> chattingMsgList;
    private Context context;
    private ActivityResultLauncher<Intent> startActivity;
    private String userID;
    private HashMap<String, Bitmap> userProfileMap;

    public ChattingMsgListAdapter(ArrayList<ChatMsgInfo> chattingMsgList, Context context, ActivityResultLauncher<Intent> startActivity, String userId, HashMap<String, Bitmap> userProfileMap){
        this.chattingMsgList = chattingMsgList;
        this.context = context;
        this.startActivity = startActivity;
        this.userID = userId;
        this.userProfileMap = userProfileMap;
    }


    // 내가 작성한 채팅을 보여주는 뷰홀더
    public class MyMsgViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layout_my_chatBox;
        TextView textView_my_nickName;
        TextView editText_MyMsg;
        TextView textViewMyTime;
        CircleImageView user_profile;

        public MyMsgViewHolder(View itemView) {
            super(itemView);
            //layout_opponent_chatBox = itemView.findViewById(R.id.layout_opponent_chatBox);
            layout_my_chatBox = itemView.findViewById(R.id.layout_my_chatBox);

            textView_my_nickName = itemView.findViewById(R.id.textView_my_nickName);
            editText_MyMsg = itemView.findViewById(R.id.editText_MyMsg);
            textViewMyTime = itemView.findViewById(R.id.textViewMyTime);
            user_profile = itemView.findViewById(R.id.user_profile);
        }
    }

    // 상대방이 작성한 채팅을 보여주는 뷰홀더
    public class OpponentMsgViewHolderViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ConstraintLayout layout_opponent_chatBox;
        TextView textView_oppo_nickName;
        TextView editText_OpponentMsg;
        TextView textView_oppo_time;
        CircleImageView user_profile;

        public OpponentMsgViewHolderViewHolder(View itemView) {
            super(itemView);

            layout_opponent_chatBox = itemView.findViewById(R.id.layout_opponent_chatBox);

            textView_oppo_nickName = itemView.findViewById(R.id.textView_oppo_nickName);
            editText_OpponentMsg = itemView.findViewById(R.id.editText_OpponentMsg);
            textView_oppo_time = itemView.findViewById(R.id.textView_oppo_time);
            user_profile = itemView.findViewById(R.id.user_profile);
        }
    }

    // 내가 작성한 채팅과 날짜를 보여주는 뷰홀더
    public class MyMsgViewHolderWithDay extends RecyclerView.ViewHolder {
        ConstraintLayout layout_my_chatBox;
        TextView textView_date;
        TextView textView_my_nickName;
        TextView editText_MyMsg;
        TextView textViewMyTime;
        CircleImageView user_profile;

        public MyMsgViewHolderWithDay(View itemView) {
            super(itemView);
            layout_my_chatBox = itemView.findViewById(R.id.layout_my_chatBox);

            textView_date = itemView.findViewById(R.id.textView_date);
            textView_my_nickName = itemView.findViewById(R.id.textView_my_nickName);
            editText_MyMsg = itemView.findViewById(R.id.editText_MyMsg);
            textViewMyTime = itemView.findViewById(R.id.textViewMyTime);
            user_profile = itemView.findViewById(R.id.user_profile);
        }
    }


    // 상대방이 작성한 채팅채팅과 날짜를 보여주는 뷰홀더
    public class OpponentMsgViewHolderViewHolderWithDay extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ConstraintLayout layout_opponent_chatBox;
        TextView textView_date;
        TextView textView_oppo_nickName;
        TextView editText_OpponentMsg;
        TextView textView_oppo_time;
        CircleImageView user_profile;

        public OpponentMsgViewHolderViewHolderWithDay(View itemView) {
            super(itemView);

            layout_opponent_chatBox = itemView.findViewById(R.id.layout_opponent_chatBox);

            textView_date = itemView.findViewById(R.id.textView_date);
            textView_oppo_nickName = itemView.findViewById(R.id.textView_oppo_nickName);
            editText_OpponentMsg = itemView.findViewById(R.id.editText_OpponentMsg);
            textView_oppo_time = itemView.findViewById(R.id.textView_oppo_time);
            user_profile = itemView.findViewById(R.id.user_profile);
        }
    }

    // 참여자가 입장하고 나갈 때 출력되는 메시지를 보여주는 viewHolder
    public class EnterAndExitViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView textView_enter_or_exit;

        public EnterAndExitViewHolder(View itemView) {
            super(itemView);

            textView_enter_or_exit = itemView.findViewById(R.id.textView_enter_or_exit);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemViewMy = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting_chatbox_me, parent, false);
        View itemViewOppo = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting_chatbox_oppo, parent, false);
        View itemViewMyWithDate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting_chatbox_me_with_date, parent, false);
        View itemViewOppoWithDate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting_chatbox_oppo_with_date, parent, false);
        View itemEnterOrExit = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_enter_and_exit, parent, false);

        if(viewType == 0){ // 내가 작성한 채팅
            ChattingMsgListAdapter.MyMsgViewHolder vh = new ChattingMsgListAdapter.MyMsgViewHolder(itemViewMy);
            return vh;
        } else if(viewType == 1){ // 상대방이 작성한 채팅
            ChattingMsgListAdapter.OpponentMsgViewHolderViewHolder vh2 = new ChattingMsgListAdapter.OpponentMsgViewHolderViewHolder(itemViewOppo);
            return vh2;
        } else if(viewType == 2){ // 내가 작성했고 날짜 보여줘야 할 때
            ChattingMsgListAdapter.MyMsgViewHolderWithDay vh3 = new ChattingMsgListAdapter.MyMsgViewHolderWithDay(itemViewMyWithDate);
            return vh3;
        } else if(viewType == 3){ // 상대방이 작성했고 날짜 보여줘야 할 때
            ChattingMsgListAdapter.OpponentMsgViewHolderViewHolderWithDay vh4 = new ChattingMsgListAdapter.OpponentMsgViewHolderViewHolderWithDay(itemViewOppoWithDate);
            return vh4;
        } else { // 들어오거나 나깠을 때
            ChattingMsgListAdapter.EnterAndExitViewHolder vh5 = new ChattingMsgListAdapter.EnterAndExitViewHolder(itemEnterOrExit);
            return vh5;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMsgInfo chattingDto = chattingMsgList.get(position);

        // 내가 작성한 메시지일 경우
        if(holder instanceof MyMsgViewHolder){
            // 프로필, 메시지, 시간 세팅
            ((MyMsgViewHolder)holder).user_profile.setImageBitmap(userProfileMap.get(chattingDto.getChattingMemberId()));
            ((MyMsgViewHolder)holder).editText_MyMsg.setText(chattingDto.getMsg());
            ((MyMsgViewHolder)holder).textViewMyTime.setText(chattingDto.getCreDatetime());
        } else if(holder instanceof OpponentMsgViewHolderViewHolder){ //상대방이 작성한 메시지인 경우
            ((OpponentMsgViewHolderViewHolder)holder).user_profile.setImageBitmap(userProfileMap.get(chattingDto.getChattingMemberId()));
            ((OpponentMsgViewHolderViewHolder)holder).textView_oppo_nickName.setText(chattingDto.getChattingMemberName());
            ((OpponentMsgViewHolderViewHolder)holder).editText_OpponentMsg.setText(chattingDto.getMsg());
            ((OpponentMsgViewHolderViewHolder)holder).textView_oppo_time.setText(chattingDto.getCreDatetime());
        } else if(holder instanceof MyMsgViewHolderWithDay){ //내가 작성했고 날짜를 보여줘야 하는 경우
            ((MyMsgViewHolderWithDay)holder).user_profile.setImageBitmap(userProfileMap.get(chattingDto.getChattingMemberId()));
            ((MyMsgViewHolderWithDay)holder).editText_MyMsg.setText(chattingDto.getMsg());
            ((MyMsgViewHolderWithDay)holder).textViewMyTime.setText(chattingDto.getCreDatetime());
            ((MyMsgViewHolderWithDay)holder).textView_date.setText(getDate(chattingDto.getCreDatetime()));

        } else if(holder instanceof OpponentMsgViewHolderViewHolderWithDay){ //상대방이 작성했고 날짜를 보여줘야 하는 경우
            ((OpponentMsgViewHolderViewHolderWithDay)holder).user_profile.setImageBitmap(userProfileMap.get(chattingDto.getChattingMemberId()));
            ((OpponentMsgViewHolderViewHolderWithDay)holder).textView_oppo_nickName.setText(chattingDto.getChattingMemberName());
            ((OpponentMsgViewHolderViewHolderWithDay)holder).editText_OpponentMsg.setText(chattingDto.getMsg());
            ((OpponentMsgViewHolderViewHolderWithDay)holder).textView_oppo_time.setText(chattingDto.getCreDatetime());
            ((OpponentMsgViewHolderViewHolderWithDay)holder).textView_date.setText(getDate(chattingDto.getCreDatetime()));

        } else if(holder instanceof EnterAndExitViewHolder){ //나가거나 들어온 경우
            ((EnterAndExitViewHolder)holder).textView_enter_or_exit.setText(chattingDto.getMsg());
        }
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return chattingMsgList.size();
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = 0;
        boolean isMyChat = chattingMsgList.get(position).getChattingMemberId().equals(userID);
        boolean isDateShowed = false;

        if(position != 0){
            // 이전 채팅과 현재 채팅의 날짜가 다르다면
            if(!chattingMsgList.get(position).getCreDatetime().split(" ")[0].equals(chattingMsgList.get(position - 1).getCreDatetime().split(" ")[0])) {
                isDateShowed = true;
            }
        }

        if(isMyChat && (position == 0 || isDateShowed)){ // 내가 작성했고 날짜를 보여줘야 할 때
            viewType = 2;
        } else if(!isMyChat && (position == 0 || isDateShowed)){ // 상대방이 작성했고 날짜를 보여줘야 할 때
            viewType = 3;
        } else if(isMyChat){ // 내가 작성했고 날짜를 보여줄 필요가 없을 때
            viewType = 0;
        } else if(!isMyChat){ // 상대방이 작성했고 날짜를 보여줄 필요가 없을 때
            viewType = 1;
        } else {
            viewType = 4;
        }

        return viewType;
    }


    // 채팅 메시지 추가
    public void addChatMsgInfo(ChatMsgInfo chatMsgInfo){
        // 3. 리사에 잘 넘어와서 이 메소드가 호출 되는가?
        System.out.println();
        int size = getItemCount();

        chattingMsgList.add(size,chatMsgInfo);

        notifyItemInserted(size);


    }

    // 채팅메시지 리스트 반환
    public ArrayList<ChatMsgInfo> getChatMsgList(){
        return chattingMsgList;
    }

    // 날짜(요일) 반환
    public String getDate(String creDatetime){
        String date1 = creDatetime.split(" ")[0].replace("-","");

        String date2 = GetDate.getDateWithYMD(date1);

        date2 += " " + GetDate.getDayOfWeek(date1) + "요일";

        return date2;
    }
}
