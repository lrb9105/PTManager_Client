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

import com.bumptech.glide.Glide;
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
    private HashMap<String, String> userProfileMap;

    public ChattingMsgListAdapter(ArrayList<ChatMsgInfo> chattingMsgList, Context context, ActivityResultLauncher<Intent> startActivity, String userId, HashMap<String, String> userProfileMap){
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
        TextView not_read_user_count;

        public MyMsgViewHolder(View itemView) {
            super(itemView);
            //layout_opponent_chatBox = itemView.findViewById(R.id.layout_opponent_chatBox);
            layout_my_chatBox = itemView.findViewById(R.id.layout_my_chatBox);

            textView_my_nickName = itemView.findViewById(R.id.textView_my_nickName);
            editText_MyMsg = itemView.findViewById(R.id.editText_MyMsg);
            textViewMyTime = itemView.findViewById(R.id.textViewMyTime);
            user_profile = itemView.findViewById(R.id.user_profile);
            not_read_user_count = itemView.findViewById(R.id.not_read_user_count);
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
        TextView not_read_user_count;

        public OpponentMsgViewHolderViewHolder(View itemView) {
            super(itemView);

            layout_opponent_chatBox = itemView.findViewById(R.id.layout_opponent_chatBox);

            textView_oppo_nickName = itemView.findViewById(R.id.textView_oppo_nickName);
            editText_OpponentMsg = itemView.findViewById(R.id.editText_OpponentMsg);
            textView_oppo_time = itemView.findViewById(R.id.textView_oppo_time);
            user_profile = itemView.findViewById(R.id.user_profile);
            not_read_user_count = itemView.findViewById(R.id.not_read_user_count);
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
        TextView not_read_user_count;

        public MyMsgViewHolderWithDay(View itemView) {
            super(itemView);
            layout_my_chatBox = itemView.findViewById(R.id.layout_my_chatBox);

            textView_date = itemView.findViewById(R.id.textView_date);
            textView_my_nickName = itemView.findViewById(R.id.textView_my_nickName);
            editText_MyMsg = itemView.findViewById(R.id.editText_MyMsg);
            textViewMyTime = itemView.findViewById(R.id.textViewMyTime);
            user_profile = itemView.findViewById(R.id.user_profile);
            not_read_user_count = itemView.findViewById(R.id.not_read_user_count);
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
        TextView not_read_user_count;

        public OpponentMsgViewHolderViewHolderWithDay(View itemView) {
            super(itemView);

            layout_opponent_chatBox = itemView.findViewById(R.id.layout_opponent_chatBox);

            textView_date = itemView.findViewById(R.id.textView_date);
            textView_oppo_nickName = itemView.findViewById(R.id.textView_oppo_nickName);
            editText_OpponentMsg = itemView.findViewById(R.id.editText_OpponentMsg);
            textView_oppo_time = itemView.findViewById(R.id.textView_oppo_time);
            user_profile = itemView.findViewById(R.id.user_profile);
            not_read_user_count = itemView.findViewById(R.id.not_read_user_count);
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
            /** 프로필, 메시지, 시간 세팅 */

            // 프로필 id가 null이라면
            if(userProfileMap.get(chattingDto.getChattingMemberId()) == null) {
                ((MyMsgViewHolder)holder).user_profile.setImageDrawable(context.getDrawable(R.drawable.profile_boy));
            } else{
                Glide.with(context).load("http://15.165.144.216" +  userProfileMap.get(chattingDto.getChattingMemberId())).into(((MyMsgViewHolder)holder).user_profile);
            }

            ((MyMsgViewHolder)holder).editText_MyMsg.setText(chattingDto.getMsg());
            ((MyMsgViewHolder)holder).textViewMyTime.setText(getTime(chattingDto.getCreDatetime()));

            // 리사이클러뷰는 뷰홀더를 재활용하기 때문에 처음에 무조건 GONE을 해주고 데이터가 있는경우에만 VISIBLE을 해줘야 한다.
            // 이렇게 하지 않으면 안읽은사람 = 0인데도 기존 뷰홀더가 사용하던 값을 가지고 올 수 있다.
            ((MyMsgViewHolder)holder).not_read_user_count.setVisibility(View.GONE);

            if(chattingDto.getNotReadUserCount() <= 0){
                ((MyMsgViewHolder)holder).not_read_user_count.setVisibility(View.GONE);
            } else {
                ((MyMsgViewHolder)holder).not_read_user_count.setVisibility(View.VISIBLE);

                ((MyMsgViewHolder)holder).not_read_user_count.setText("" + chattingDto.getNotReadUserCount());
            }
        } else if(holder instanceof OpponentMsgViewHolderViewHolder){ //상대방이 작성한 메시지인 경우
            if(userProfileMap.get(chattingDto.getChattingMemberId()) == null) {
                ((OpponentMsgViewHolderViewHolder)holder).user_profile.setImageDrawable(context.getDrawable(R.drawable.profile_boy));
            } else{
                Glide.with(context).load("http://15.165.144.216" +  userProfileMap.get(chattingDto.getChattingMemberId())).into(((OpponentMsgViewHolderViewHolder)holder).user_profile);
            }
            ((OpponentMsgViewHolderViewHolder)holder).textView_oppo_nickName.setText(chattingDto.getChattingMemberName());
            ((OpponentMsgViewHolderViewHolder)holder).editText_OpponentMsg.setText(chattingDto.getMsg());
            ((OpponentMsgViewHolderViewHolder)holder).textView_oppo_time.setText(getTime(chattingDto.getCreDatetime()));

            ((OpponentMsgViewHolderViewHolder)holder).not_read_user_count.setVisibility(View.GONE);

            if(chattingDto.getNotReadUserCount() <= 0){
                ((OpponentMsgViewHolderViewHolder)holder).not_read_user_count.setVisibility(View.GONE);
            } else {
                ((OpponentMsgViewHolderViewHolder)holder).not_read_user_count.setVisibility(View.VISIBLE);

                ((OpponentMsgViewHolderViewHolder)holder).not_read_user_count.setText("" + chattingDto.getNotReadUserCount());
            }
        } else if(holder instanceof MyMsgViewHolderWithDay){ //내가 작성했고 날짜를 보여줘야 하는 경우
            if(userProfileMap.get(chattingDto.getChattingMemberId()) == null) {
                ((MyMsgViewHolderWithDay)holder).user_profile.setImageDrawable(context.getDrawable(R.drawable.profile_boy));
            } else{
                Glide.with(context).load("http://15.165.144.216" +  userProfileMap.get(chattingDto.getChattingMemberId())).into(((MyMsgViewHolderWithDay)holder).user_profile);
            }
            ((MyMsgViewHolderWithDay)holder).editText_MyMsg.setText(chattingDto.getMsg());
            ((MyMsgViewHolderWithDay)holder).textViewMyTime.setText(getTime(chattingDto.getCreDatetime()));
            ((MyMsgViewHolderWithDay)holder).textView_date.setText(getDate(chattingDto.getCreDatetime()));

            ((MyMsgViewHolderWithDay)holder).not_read_user_count.setVisibility(View.GONE);

            if(chattingDto.getNotReadUserCount() <= 0){
                ((MyMsgViewHolderWithDay)holder).not_read_user_count.setVisibility(View.GONE);
            } else {
                ((MyMsgViewHolderWithDay)holder).not_read_user_count.setVisibility(View.VISIBLE);

                ((MyMsgViewHolderWithDay)holder).not_read_user_count.setText("" + chattingDto.getNotReadUserCount());
            }
        } else if(holder instanceof OpponentMsgViewHolderViewHolderWithDay){ //상대방이 작성했고 날짜를 보여줘야 하는 경우
            if(userProfileMap.get(chattingDto.getChattingMemberId()) == null) {
                ((OpponentMsgViewHolderViewHolderWithDay)holder).user_profile.setImageDrawable(context.getDrawable(R.drawable.profile_boy));
            } else{
                Glide.with(context).load("http://15.165.144.216" +  userProfileMap.get(chattingDto.getChattingMemberId())).into(((OpponentMsgViewHolderViewHolderWithDay)holder).user_profile);
            }
            ((OpponentMsgViewHolderViewHolderWithDay)holder).textView_oppo_nickName.setText(chattingDto.getChattingMemberName());
            ((OpponentMsgViewHolderViewHolderWithDay)holder).editText_OpponentMsg.setText(chattingDto.getMsg());
            ((OpponentMsgViewHolderViewHolderWithDay)holder).textView_oppo_time.setText(getTime(chattingDto.getCreDatetime()));
            ((OpponentMsgViewHolderViewHolderWithDay)holder).textView_date.setText(getDate(chattingDto.getCreDatetime()));

            ((OpponentMsgViewHolderViewHolderWithDay)holder).not_read_user_count.setVisibility(View.GONE);

            if(chattingDto.getNotReadUserCount() <= 0){
                ((OpponentMsgViewHolderViewHolderWithDay)holder).not_read_user_count.setVisibility(View.GONE);
            } else {
                ((OpponentMsgViewHolderViewHolderWithDay)holder).not_read_user_count.setVisibility(View.VISIBLE);

                ((OpponentMsgViewHolderViewHolderWithDay)holder).not_read_user_count.setText("" + chattingDto.getNotReadUserCount());
            }
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
        boolean isMyChat = chattingMsgList.get(position).getChattingMemberId() != null? chattingMsgList.get(position).getChattingMemberId().equals(userID) : false;
        boolean isDateShowed = false;

        if(position != 0){
            // 이전 채팅과 현재 채팅의 날짜가 다르다면
            if(!chattingMsgList.get(position).getCreDatetime().split(" ")[0].equals(chattingMsgList.get(position - 1).getCreDatetime().split(" ")[0])) {
                isDateShowed = true;
            }
        }

        if(chattingMsgList.get(position).getMsg().contains("입장했습니다.") || chattingMsgList.get(position).getMsg().contains("나갔습니다.")){
            viewType = 4;
        } else if(isMyChat && (position == 0 || isDateShowed)){ // 내가 작성했고 날짜를 보여줘야 할 때
            viewType = 2;
        } else if(!isMyChat && (position == 0 || isDateShowed)){ // 상대방이 작성했고 날짜를 보여줘야 할 때
            viewType = 3;
        } else if(isMyChat){ // 내가 작성했고 날짜를 보여줄 필요가 없을 때
            viewType = 0;
        } else if(!isMyChat){ // 상대방이 작성했고 날짜를 보여줄 필요가 없을 때
            viewType = 1;
        }

        return viewType;
    }


    // 채팅 메시지 추가
    public void addChatMsgInfo(ChatMsgInfo chatMsgInfo){
        System.out.println("addChatMsgInfo 실행");
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

    // 시간 반환
    public String getTime(String creDatetime){
        String[] dateArr = creDatetime.split(" ")[1].split(":");

        String time = dateArr[0] + ":" + dateArr[1];

        return GetDate.getAmPmTime(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]));
    }

    // 특정 범위에 idx에 포함된 메시지의 안읽은 유저수 --
    public void updateNotReadUserCountMinus(int previousLastIdx){
        // 아이템리스트에서 startIdx값을 가지고 있는 아이템의 인덱스 찾기
        int itemStartIdx = 0;

        for(int i = 0; i< chattingMsgList.size(); i++){
            if(chattingMsgList.get(i).getMsgIdx() == previousLastIdx){
                itemStartIdx = i;

                System.out.println("2 & 6. 찾은 idx: " + itemStartIdx);
                break;
            }
        }

        // 리스트에 있는 동일한 메시지 인덱스를 가지고 있는 아이템의 다음 아이템부터 시작해야 하므로 +1을 해준다.
        // itemStartIdx가 0이라는 것은 맨처음 방생성이 되고 한번도 채팅방에 들어가지 않았다는 거임
        // 이때는 리스트에 있는 모든 메시지의 안읽은 사용자수를 - 해야 하므로  다음 아이템 인덱스로 설정하지 않는다.
        // 반면 0이 아니하는 것은 idx가 일치하는 메시지가 리스트에 존재하는 것이고 그 메시지까지는 읽었다는 것이므로 그 메시지 다음 메시지부터 안읽은 사용자를 -해주기 위해
        // 다음 아이템 인덱스로 설정한다.
        if(itemStartIdx != 0){
            itemStartIdx += 1;
        }

        // 기존 마지막 인덱스와 리스트의 마지막 인덱스가 동일하지않다면 즉, 새로 추가된 메시지가 있다면
        // 혹은 맨처음 메시지라면
        if(previousLastIdx != chattingMsgList.get(chattingMsgList.size() - 1).getMsgIdx() || itemStartIdx == 0){
            for(int i = itemStartIdx; i< chattingMsgList.size(); i++){
                chattingMsgList.get(i).setNotReadUserCount(chattingMsgList.get(i).getNotReadUserCount() - 1);
            }

            // 2. 내 것 안읽은 메시지 --
            System.out.println("2 & 6.itemStartIdx: " + itemStartIdx);
            System.out.println("2 & 6.chattingMsgList.size() - itemStartIdx: " + (chattingMsgList.size() - itemStartIdx));

            notifyItemRangeChanged(itemStartIdx, chattingMsgList.size() - itemStartIdx);
        }
    }

    // 내가 작성한 메시지 업데이트
    public void updateCntAndIdx(int notReadUserCount, int msgIdx){
        System.out.println("updateCntAndIdx 실행");

        // 가장 마지막에 등록된 메시지 - 내가 작성한 메시지 업데이트
        int position = chattingMsgList.size() - 1;

        chattingMsgList.get(position).setNotReadUserCount(notReadUserCount);
        chattingMsgList.get(position).setMsgIdx(msgIdx);
        notifyItemChanged(position);
    }
}
