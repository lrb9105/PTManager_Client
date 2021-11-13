package com.teamnova.ptmanager.ui.chatting.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.model.chatting.ChatMsgInfo;
import com.teamnova.ptmanager.model.chatting.ChatRoomInfoForListDto;
import com.teamnova.ptmanager.test.GlideLoaderActivity;
import com.teamnova.ptmanager.util.GetDate;

import java.sql.SQLOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    private long timeDiffer;

    public ChattingMsgListAdapter(ArrayList<ChatMsgInfo> chattingMsgList, Context context, ActivityResultLauncher<Intent> startActivity, String userId, HashMap<String, String> userProfileMap){
        this.chattingMsgList = chattingMsgList;
        this.context = context;
        this.startActivity = startActivity;
        this.userID = userId;
        this.userProfileMap = userProfileMap;
    }

    public ChattingMsgListAdapter(ArrayList<ChatMsgInfo> chattingMsgList, Context context, ActivityResultLauncher<Intent> startActivity, String userId, HashMap<String, String> userProfileMap, long timeDiffer){
        this.chattingMsgList = chattingMsgList;
        this.context = context;
        this.startActivity = startActivity;
        this.userID = userId;
        this.userProfileMap = userProfileMap;
        this.timeDiffer = timeDiffer;
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

    /** 이미지 뷰홀더*/
    // 내가 작성한 이미지를 보여주는 뷰홀더
    public class MyMsgViewHolderWithPhoto extends RecyclerView.ViewHolder {
        ConstraintLayout layout_my_chatBox;
        TextView textView_my_nickName;
        ImageView img_myMsg;
        TextView textViewMyTime;
        CircleImageView user_profile;
        TextView not_read_user_count;

        public MyMsgViewHolderWithPhoto(View itemView) {
            super(itemView);
            //layout_opponent_chatBox = itemView.findViewById(R.id.layout_opponent_chatBox);
            layout_my_chatBox = itemView.findViewById(R.id.layout_my_chatBox);
            textView_my_nickName = itemView.findViewById(R.id.textView_my_nickName);
            img_myMsg = itemView.findViewById(R.id.img_MyMsg);
            textViewMyTime = itemView.findViewById(R.id.textViewMyTime);
            user_profile = itemView.findViewById(R.id.user_profile);
            not_read_user_count = itemView.findViewById(R.id.not_read_user_count);
        }
    }

    // 상대방이 작성한 이미지를 보여주는 뷰홀더
    public class OpponentMsgViewHolderViewHolderWithPhoto extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ConstraintLayout layout_opponent_chatBox;
        TextView textView_oppo_nickName;
        ImageView img_OpponentMsg;
        TextView textView_oppo_time;
        CircleImageView user_profile;
        TextView not_read_user_count;

        public OpponentMsgViewHolderViewHolderWithPhoto(View itemView) {
            super(itemView);

            layout_opponent_chatBox = itemView.findViewById(R.id.layout_opponent_chatBox);
            textView_oppo_nickName = itemView.findViewById(R.id.textView_oppo_nickName);
            img_OpponentMsg = itemView.findViewById(R.id.img_OpponentMsg);
            textView_oppo_time = itemView.findViewById(R.id.textView_oppo_time);
            user_profile = itemView.findViewById(R.id.user_profile);
            not_read_user_count = itemView.findViewById(R.id.not_read_user_count);
        }
    }

    // 내가 작성한 이미지와 날짜를 보여주는 뷰홀더
    public class MyMsgViewHolderWithDayAndPhoto extends RecyclerView.ViewHolder {
        ConstraintLayout layout_my_chatBox;
        TextView textView_date;
        TextView textView_my_nickName;
        ImageView img_myMsg;
        TextView textViewMyTime;
        CircleImageView user_profile;
        TextView not_read_user_count;

        public MyMsgViewHolderWithDayAndPhoto(View itemView) {
            super(itemView);
            layout_my_chatBox = itemView.findViewById(R.id.layout_my_chatBox);

            textView_date = itemView.findViewById(R.id.textView_date);
            textView_my_nickName = itemView.findViewById(R.id.textView_my_nickName);
            img_myMsg = itemView.findViewById(R.id.img_MyMsg);
            textViewMyTime = itemView.findViewById(R.id.textViewMyTime);
            user_profile = itemView.findViewById(R.id.user_profile);
            not_read_user_count = itemView.findViewById(R.id.not_read_user_count);
        }
    }


    // 상대방이 작성한 이미지와 날짜를 보여주는 뷰홀더
    public class OpponentMsgViewHolderViewHolderWithDayAndPhoto extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ConstraintLayout layout_opponent_chatBox;
        TextView textView_date;
        TextView textView_oppo_nickName;
        ImageView img_OpponentMsg;
        TextView textView_oppo_time;
        CircleImageView user_profile;
        TextView not_read_user_count;

        public OpponentMsgViewHolderViewHolderWithDayAndPhoto(View itemView) {
            super(itemView);

            layout_opponent_chatBox = itemView.findViewById(R.id.layout_opponent_chatBox);

            textView_date = itemView.findViewById(R.id.textView_date);
            textView_oppo_nickName = itemView.findViewById(R.id.textView_oppo_nickName);
            img_OpponentMsg = itemView.findViewById(R.id.img_OpponentMsg);
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

        // 이미지를 출력하는 뷰홀더
        View itemViewMyWithPhoto = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting_chatbox_me_with_photo, parent, false);
        View itemViewOppoWithPhoto = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting_chatbox_oppo_with_photo, parent, false);
        View itemViewMyWithDateAndPhoto = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting_chatbox_me_with_date_and_photo, parent, false);
        View itemViewOppoWithDateAndPhoto = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting_chatbox_oppo_with_date_and_photo, parent, false);

        if(viewType == 0){ // 내가 작성한 채팅
            ChattingMsgListAdapter.MyMsgViewHolder vh = new ChattingMsgListAdapter.MyMsgViewHolder(itemViewMy);
            return vh;
        } else if(viewType == 1){ // 상대방이 작성한 채팅
            ChattingMsgListAdapter.OpponentMsgViewHolderViewHolder vh2 = new ChattingMsgListAdapter.OpponentMsgViewHolderViewHolder(itemViewOppo);
            return vh2;
        } else if(viewType == 2){ // 내가 작성했고 날짜 보여줘야 할 때
            ChattingMsgListAdapter.MyMsgViewHolderWithDay vh3 = new ChattingMsgListAdapter.MyMsgViewHolderWithDay(itemViewMyWithDate);
            return vh3;
        } else if(viewType == 3) { // 상대방이 작성했고 날짜 보여줘야 할 때
            ChattingMsgListAdapter.OpponentMsgViewHolderViewHolderWithDay vh4 = new ChattingMsgListAdapter.OpponentMsgViewHolderViewHolderWithDay(itemViewOppoWithDate);
            return vh4;
        }else if(viewType == 4){ // 내가 작성한 이미지
                ChattingMsgListAdapter.MyMsgViewHolderWithPhoto vh5 = new ChattingMsgListAdapter.MyMsgViewHolderWithPhoto(itemViewMyWithPhoto);
                return vh5;
        } else if(viewType == 5){ // 상대방이 작성한 이미지
            ChattingMsgListAdapter.OpponentMsgViewHolderViewHolderWithPhoto vh6 = new ChattingMsgListAdapter.OpponentMsgViewHolderViewHolderWithPhoto(itemViewOppoWithPhoto);
            return vh6;
        } else if(viewType == 6){ // 내가 작성한 이미지와 날짜 보여줘야 할 때
            ChattingMsgListAdapter.MyMsgViewHolderWithDayAndPhoto vh7 = new ChattingMsgListAdapter.MyMsgViewHolderWithDayAndPhoto(itemViewMyWithDateAndPhoto);
            return vh7;
        } else if(viewType == 7){ // 상대방이 작성한 이미지와 날짜 보여줘야 할 때
            ChattingMsgListAdapter.OpponentMsgViewHolderViewHolderWithDayAndPhoto vh8 = new ChattingMsgListAdapter.OpponentMsgViewHolderViewHolderWithDayAndPhoto(itemViewOppoWithDateAndPhoto);
            return vh8;
        } else { // 들어오거나 나깠을 때
            ChattingMsgListAdapter.EnterAndExitViewHolder vh9 = new ChattingMsgListAdapter.EnterAndExitViewHolder(itemEnterOrExit);
            return vh9;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMsgInfo chattingDto = chattingMsgList.get(position);

        // 내가 작성한 메시지일 경우
        if(holder instanceof MyMsgViewHolder){
            /** 프로필, 메시지, 시간 세팅 */

            // 프로필 id가 null이라면
            /*if(userProfileMap.get(chattingDto.getChattingMemberId()) == null) {
                ((MyMsgViewHolder)holder).user_profile.setImageDrawable(context.getDrawable(R.drawable.profile_boy));
            } else{
                Glide.with(context).load("http://15.165.144.216" +  userProfileMap.get(chattingDto.getChattingMemberId())).into(((MyMsgViewHolder)holder).user_profile);
            }*/

            ((MyMsgViewHolder)holder).editText_MyMsg.setText(chattingDto.getMsg());
            ((MyMsgViewHolder)holder).textViewMyTime.setText(getTime(computeTimeDifferToServer(chattingDto.getCreDatetime(), timeDiffer)));
            
                    
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
            ((OpponentMsgViewHolderViewHolder)holder).textView_oppo_time.setText(getTime(computeTimeDifferToServer(chattingDto.getCreDatetime(), timeDiffer)));

            ((OpponentMsgViewHolderViewHolder)holder).not_read_user_count.setVisibility(View.GONE);

            if(chattingDto.getNotReadUserCount() <= 0){
                ((OpponentMsgViewHolderViewHolder)holder).not_read_user_count.setVisibility(View.GONE);
            } else {
                ((OpponentMsgViewHolderViewHolder)holder).not_read_user_count.setVisibility(View.VISIBLE);

                ((OpponentMsgViewHolderViewHolder)holder).not_read_user_count.setText("" + chattingDto.getNotReadUserCount());
            }
        } else if(holder instanceof MyMsgViewHolderWithDay){ //내가 작성했고 날짜를 보여줘야 하는 경우
            /*if(userProfileMap.get(chattingDto.getChattingMemberId()) == null) {
                ((MyMsgViewHolderWithDay)holder).user_profile.setImageDrawable(context.getDrawable(R.drawable.profile_boy));
            } else{
                Glide.with(context).load("http://15.165.144.216" +  userProfileMap.get(chattingDto.getChattingMemberId())).into(((MyMsgViewHolderWithDay)holder).user_profile);
            }*/

            ((MyMsgViewHolderWithDay)holder).editText_MyMsg.setText(chattingDto.getMsg());
            ((MyMsgViewHolderWithDay)holder).textViewMyTime.setText(getTime(computeTimeDifferToServer(chattingDto.getCreDatetime(), timeDiffer)));
            ((MyMsgViewHolderWithDay)holder).textView_date.setText(getDate(computeTimeDifferToServer(chattingDto.getCreDatetime(), timeDiffer)));

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
            ((OpponentMsgViewHolderViewHolderWithDay)holder).textView_oppo_time.setText(getTime(computeTimeDifferToServer(chattingDto.getCreDatetime(), timeDiffer)));
            ((OpponentMsgViewHolderViewHolderWithDay)holder).textView_date.setText(getDate(computeTimeDifferToServer(chattingDto.getCreDatetime(), timeDiffer)));
            
            ((OpponentMsgViewHolderViewHolderWithDay)holder).not_read_user_count.setVisibility(View.GONE);

            if(chattingDto.getNotReadUserCount() <= 0){
                ((OpponentMsgViewHolderViewHolderWithDay)holder).not_read_user_count.setVisibility(View.GONE);
            } else {
                ((OpponentMsgViewHolderViewHolderWithDay)holder).not_read_user_count.setVisibility(View.VISIBLE);

                ((OpponentMsgViewHolderViewHolderWithDay)holder).not_read_user_count.setText("" + chattingDto.getNotReadUserCount());
            }
        } if(holder instanceof MyMsgViewHolderWithPhoto){ // 내가 작성한 사진인 경우
            // 내가 전송한 이미지를 출력하는 경우 비트맵에 있는 데이터를 가져온다.
            if(chattingDto.getSaveImgBitmap() != null){
                ((MyMsgViewHolderWithPhoto)holder).img_myMsg.setImageBitmap(chattingDto.getSaveImgBitmap());
            } else { // 조회한 데이터인 경우 글라이드로 뿌려준다.
                Glide.with(context).load("http://15.165.144.216" +  chattingDto.getSavePath()).into(((MyMsgViewHolderWithPhoto)holder).img_myMsg);
            }

            ((MyMsgViewHolderWithPhoto)holder).textViewMyTime.setText(getTime(computeTimeDifferToServer(chattingDto.getCreDatetime(), timeDiffer)));

            // 리사이클러뷰는 뷰홀더를 재활용하기 때문에 처음에 무조건 GONE을 해주고 데이터가 있는경우에만 VISIBLE을 해줘야 한다.
            // 이렇게 하지 않으면 안읽은사람 = 0인데도 기존 뷰홀더가 사용하던 값을 가지고 올 수 있다.
            ((MyMsgViewHolderWithPhoto)holder).not_read_user_count.setVisibility(View.GONE);

            if(chattingDto.getNotReadUserCount() <= 0){
                ((MyMsgViewHolderWithPhoto)holder).not_read_user_count.setVisibility(View.GONE);
            } else {
                ((MyMsgViewHolderWithPhoto)holder).not_read_user_count.setVisibility(View.VISIBLE);

                ((MyMsgViewHolderWithPhoto)holder).not_read_user_count.setText("" + chattingDto.getNotReadUserCount());
            }

            // 클릭 시 다운로드 화면으로 이동
            ((MyMsgViewHolderWithPhoto)holder).img_myMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, GlideLoaderActivity.class);

                    intent.putExtra("url", "http://15.165.144.216" +  chattingDto.getSavePath());
                    System.out.println("url1: " + "http://15.165.144.216" + chattingDto.getSavePath());
                    context.startActivity(intent);
                }
            });
        } else if(holder instanceof OpponentMsgViewHolderViewHolderWithPhoto){ //상대방이 작성한 사진인 경우
            if(userProfileMap.get(chattingDto.getChattingMemberId()) == null) {
                ((OpponentMsgViewHolderViewHolderWithPhoto)holder).user_profile.setImageDrawable(context.getDrawable(R.drawable.profile_boy));
            } else{
                Glide.with(context).load("http://15.165.144.216" +  userProfileMap.get(chattingDto.getChattingMemberId())).into(((OpponentMsgViewHolderViewHolderWithPhoto)holder).user_profile);
            }

            ((OpponentMsgViewHolderViewHolderWithPhoto)holder).textView_oppo_nickName.setText(chattingDto.getChattingMemberName());
            Glide.with(context).load("http://15.165.144.216" +  chattingDto.getSavePath()).into(((OpponentMsgViewHolderViewHolderWithPhoto)holder).img_OpponentMsg);
            ((OpponentMsgViewHolderViewHolderWithPhoto)holder).textView_oppo_time.setText(getTime(computeTimeDifferToServer(chattingDto.getCreDatetime(), timeDiffer)));

            ((OpponentMsgViewHolderViewHolderWithPhoto)holder).not_read_user_count.setVisibility(View.GONE);

            if(chattingDto.getNotReadUserCount() <= 0){
                ((OpponentMsgViewHolderViewHolderWithPhoto)holder).not_read_user_count.setVisibility(View.GONE);
            } else {
                ((OpponentMsgViewHolderViewHolderWithPhoto)holder).not_read_user_count.setVisibility(View.VISIBLE);

                ((OpponentMsgViewHolderViewHolderWithPhoto)holder).not_read_user_count.setText("" + chattingDto.getNotReadUserCount());
            }

            // 클릭 시 다운로드 화면으로 이동
            ((OpponentMsgViewHolderViewHolderWithPhoto)holder).img_OpponentMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, GlideLoaderActivity.class);
                    intent.putExtra("url", "http://15.165.144.216" +  chattingDto.getSavePath());
                    System.out.println("url1: " + "http://15.165.144.216" + chattingDto.getSavePath());
                    context.startActivity(intent);
                }
            });
        } else if(holder instanceof MyMsgViewHolderWithDayAndPhoto){ //내가 작성한 사진이고 날짜를 보여줘야 하는 경우
            // 내가 전송한 이미지를 출력하는 경우 비트맵에 있는 데이터를 가져온다.
            if(chattingDto.getSaveImgBitmap() != null){
                ((MyMsgViewHolderWithDayAndPhoto)holder).img_myMsg.setImageBitmap(chattingDto.getSaveImgBitmap());
            } else { // 조회한 데이터인 경우 글라이드로 뿌려준다.
                Glide.with(context).load("http://15.165.144.216" +  chattingDto.getSavePath()).into(((MyMsgViewHolderWithDayAndPhoto)holder).img_myMsg);
            }

            ((MyMsgViewHolderWithDayAndPhoto)holder).textViewMyTime.setText(getTime(computeTimeDifferToServer(chattingDto.getCreDatetime(), timeDiffer)));
            ((MyMsgViewHolderWithDayAndPhoto)holder).textView_date.setText(getDate(computeTimeDifferToServer(chattingDto.getCreDatetime(), timeDiffer)));

            ((MyMsgViewHolderWithDayAndPhoto)holder).not_read_user_count.setVisibility(View.GONE);

            if(chattingDto.getNotReadUserCount() <= 0){
                ((MyMsgViewHolderWithDayAndPhoto)holder).not_read_user_count.setVisibility(View.GONE);
            } else {
                ((MyMsgViewHolderWithDayAndPhoto)holder).not_read_user_count.setVisibility(View.VISIBLE);

                ((MyMsgViewHolderWithDayAndPhoto)holder).not_read_user_count.setText("" + chattingDto.getNotReadUserCount());
            }

            // 클릭 시 다운로드 화면으로 이동
            ((MyMsgViewHolderWithDayAndPhoto)holder).img_myMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, GlideLoaderActivity.class);

                    intent.putExtra("url", "http://15.165.144.216" +  chattingDto.getSavePath());
                    System.out.println("url1: " + "http://15.165.144.216" + chattingDto.getSavePath());
                    context.startActivity(intent);
                }
            });
        } else if(holder instanceof OpponentMsgViewHolderViewHolderWithDayAndPhoto){ //상대방이 작성한 사진이고 날짜를 보여줘야 하는 경우
            if(userProfileMap.get(chattingDto.getChattingMemberId()) == null) {
                ((OpponentMsgViewHolderViewHolderWithDayAndPhoto)holder).user_profile.setImageDrawable(context.getDrawable(R.drawable.profile_boy));
            } else{
                Glide.with(context).load("http://15.165.144.216" +  userProfileMap.get(chattingDto.getChattingMemberId())).into(((OpponentMsgViewHolderViewHolderWithDayAndPhoto)holder).user_profile);
            }
            ((OpponentMsgViewHolderViewHolderWithDayAndPhoto)holder).textView_oppo_nickName.setText(chattingDto.getChattingMemberName());
            Glide.with(context).load("http://15.165.144.216" +  chattingDto.getSavePath()).into(((OpponentMsgViewHolderViewHolderWithDayAndPhoto)holder).img_OpponentMsg);
            ((OpponentMsgViewHolderViewHolderWithDayAndPhoto)holder).textView_oppo_time.setText(getTime(computeTimeDifferToServer(chattingDto.getCreDatetime(), timeDiffer)));
            ((OpponentMsgViewHolderViewHolderWithDayAndPhoto)holder).textView_date.setText(getDate(computeTimeDifferToServer(chattingDto.getCreDatetime(), timeDiffer)));

            ((OpponentMsgViewHolderViewHolderWithDayAndPhoto)holder).not_read_user_count.setVisibility(View.GONE);

            if(chattingDto.getNotReadUserCount() <= 0){
                ((OpponentMsgViewHolderViewHolderWithDayAndPhoto)holder).not_read_user_count.setVisibility(View.GONE);
            } else {
                ((OpponentMsgViewHolderViewHolderWithDayAndPhoto)holder).not_read_user_count.setVisibility(View.VISIBLE);

                ((OpponentMsgViewHolderViewHolderWithDayAndPhoto)holder).not_read_user_count.setText("" + chattingDto.getNotReadUserCount());
            }

            // 클릭 시 다운로드 화면으로 이동
            ((OpponentMsgViewHolderViewHolderWithDayAndPhoto)holder).img_OpponentMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, GlideLoaderActivity.class);

                    intent.putExtra("url", "http://15.165.144.216" +  chattingDto.getSavePath());
                    System.out.println("url1: " + "http://15.165.144.216" + chattingDto.getSavePath());
                    context.startActivity(intent);
                }
            });
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
        boolean isDateVisible = chattingMsgList.get(position).getIsDateVisible() != null && chattingMsgList.get(position).getIsDateVisible().equals("Y");

        // 사진정보가 있을 때 getSaveImgBitmap: 내가 전송한 사진을 바로 출력할 때, getSavePath: 상대방이 보냈거나 db에서 조회한 사진을 출력할 때
        boolean isPhotoExist = chattingMsgList.get(position).getSaveImgBitmap() != null || (chattingMsgList.get(position).getSavePath() != null && !chattingMsgList.get(position).getSavePath().equals(""));

        if(position != 0){
            // 이전 채팅과 현재 채팅의 날짜가 다르다면
            if(!chattingMsgList.get(position).getCreDatetime().split(" ")[0].equals(chattingMsgList.get(position - 1).getCreDatetime().split(" ")[0])) {
                isDateShowed = true;
            }
        }

        if(chattingMsgList.get(position).getIsDbSelect().equals("Y")){
            if(chattingMsgList.get(position).getMsg().contains("입장했습니다.") || chattingMsgList.get(position).getMsg().contains("나갔습니다.")){
                viewType = 8;
            } else if((isDateVisible) && isPhotoExist && isMyChat) { // db에서 조회했고 내가 작성한 사진이고 날짜를 보여줘야 할 때
                viewType = 6;
            } else if((isDateVisible) && isPhotoExist) { // db에서 조회했고 상대방이 작성한 사진이고 날짜를 보여줘야 할 때
                viewType = 7;
            } else if(isPhotoExist && isMyChat){ // 내가 작성한 사진이고 날짜를 보여줄 필요가 없을 때
                viewType = 4;
            } else if(isPhotoExist){ // 상대방이 작성한 사진이고 날짜를 보여줄 필요가 없을 때
                viewType = 5;
            } else if((isDateVisible) && isMyChat) { // db에서 조회했고 내가 작성한 메시지고 날짜를 보여줘야 할 때
                viewType = 2;
            } else if((isDateVisible)) { // db에서 조회했고 상대방이 작성한 메시지고 날짜를 보여줘야 할 때
                viewType = 3;
            } else if(isMyChat){ // 내가 작성했고 날짜를 보여줄 필요가 없을 때
                viewType = 0;
            } else if(!isMyChat){ // 상대방이 작성했고 날짜를 보여줄 필요가 없을 때
                viewType = 1;
            }
        } else{
            if(chattingMsgList.get(position).getMsg().contains("입장했습니다.") || chattingMsgList.get(position).getMsg().contains("나갔습니다.")){
                viewType = 8;
            } else if(isPhotoExist && isMyChat && (position == 0 || isDateShowed)){ // 내가 작성한 사진이고 날짜를 보여줘야 할 때
                viewType = 6;
            } else if(isPhotoExist && !isMyChat && (position == 0 || isDateShowed)){ // 상대방이 작성한 사진이고 날짜를 보여줘야 할 때
                viewType = 7;
            } else if(isPhotoExist && isMyChat){ // 내가 작성한 사진이고 날짜를 보여줄 필요가 없을 때
                viewType = 4;
            } else if(isPhotoExist){ // 상대방이 작성한 사진이고 날짜를 보여줄 필요가 없을 때
                viewType = 5;
            } else if(isMyChat && (position == 0 || isDateShowed)){ // 내가 작성했고 날짜를 보여줘야 할 때
                viewType = 2;
            } else if(!isMyChat && (position == 0 || isDateShowed)){ // 상대방이 작성했고 날짜를 보여줘야 할 때
                viewType = 3;
            } else if(isMyChat){ // 내가 작성했고 날짜를 보여줄 필요가 없을 때
                viewType = 0;
            } else if(!isMyChat){ // 상대방이 작성했고 날짜를 보여줄 필요가 없을 때
                viewType = 1;
            }
        }

        return viewType;
    }


    // 채팅 메시지 추가
    public void addChatMsgInfo(ChatMsgInfo chatMsgInfo){
        Log.e("수신한 메시지 리사이클러뷰에 뿌리기 11. 리사에 추가 ", "" + chatMsgInfo.getMsg());

        //System.out.println("addChatMsgInfo 실행");
        // 3. 리사에 잘 넘어와서 이 메소드가 호출 되는가?
        int size = getItemCount();
        Log.e("수신한 메시지 리사이클러뷰에 뿌리기 12.리사 사이즈: ", "" + size);

        chattingMsgList.add(size,chatMsgInfo);
        Log.e("수신한 메시지 리사이클러뷰에 뿌리기 13.리사에 추가 후 사이즈: ", "" + size);

        notifyItemInserted(size);
        Log.e("수신한 메시지 리사이클러뷰에 뿌리기 14.리사에 추가 및 notify 후 사이즈:  ", "" + size);
    }

    // 채팅메시지 리스트 반환
    public ArrayList<ChatMsgInfo> getChatMsgList(){
        return chattingMsgList;
    }

    // 날짜(요일) 반환
    public String getDate(String creDatetime){
        System.out.println("11");
        String date1 = creDatetime.split(" ")[0].replace("-","");

        String date2 = GetDate.getDateWithYMD(date1);

        date2 += " " + GetDate.getDayOfWeek(date1) + "요일";

        return date2;
    }

    // 시간 반환
    public String getTime(String creDatetime){
        //System.out.println("22");

        //System.out.println("creDatetime: "  + creDatetime);

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
    public void updateCntAndIdx(int notReadUserCount, int msgIdx, String creDatetime, String savePath){
        System.out.println("updateCntAndIdx 실행");

        System.out.println("수신시간: " + creDatetime);

        // 가장 마지막에 등록된 메시지 - 내가 작성한 메시지 업데이트
        int position = chattingMsgList.size() - 1;

        chattingMsgList.get(position).setNotReadUserCount(notReadUserCount);
        chattingMsgList.get(position).setMsgIdx(msgIdx);
        chattingMsgList.get(position).setCreDatetime(creDatetime);
        chattingMsgList.get(position).setSavePath(savePath);

        notifyItemChanged(position);
    }

    // String(datetime형태) to date
    public Date makeDateFromDatetimeOfString(String datetime){
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = null;

        try {
            time = transFormat.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;
    }

    // 서버시간과의 차이 보정한 메시지 수신시간 리턴
    public String computeTimeDifferToServer(String datetime, long timeDiffer){
        Date currentTimeOfDate = makeDateFromDatetimeOfString(datetime);
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int timeDifferSec = (int)(timeDiffer/1000);

        Calendar cal = Calendar.getInstance();
        cal.setTime(currentTimeOfDate);
        cal.add(Calendar.SECOND, timeDifferSec);

        //System.out.println("보정시간; "+ sdFormat.format(cal.getTime()));

        //System.out.println("userID: " + userID);

        return sdFormat.format(cal.getTime());
    }

    public void setTimeDiffer(long timeDiffer) {
        this.timeDiffer = timeDiffer;
        System.out.println("timeDiffer111112222: " + timeDiffer);
    }
}
