package com.teamnova.ptmanager.adapter.friend;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.model.chatting.ChattingMemberDto;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.model.userInfo.UserInfoDto;
import com.teamnova.ptmanager.model.userInfo.UserInfoDtoWithUserId;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.changehistory.inbody.InBodyService;
import com.teamnova.ptmanager.ui.changehistory.inbody.InBodyModifyActivity;
import com.teamnova.ptmanager.ui.chatting.ChattingActivity;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.ui.login.LoginActivity;
import com.teamnova.ptmanager.ui.login.findpw.FindPw3Activity;
import com.teamnova.ptmanager.ui.member.memberinfo.MemberInfoActivity;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Retrofit;

/** 트레이너 홈 화면에서 회원목록 표시 할 리사이클러뷰와 연결해주는 어댑터*/
public class FriendAddAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    // ViewHolder에 매칭시키기위한 친구목록 데이터
    private ArrayList<FriendInfoDto> friendsList;
    private Context context;

    // 친구정보 담을 뷰홀더
    public class FriendAddViewHolder extends RecyclerView.ViewHolder {
        CircleImageView friendProfile;
        ConstraintLayout friendLayout;
        TextView name_gender_age;
        TextView remain_cnt_exp_date;

        public FriendAddViewHolder(View itemView) {
            super(itemView);
            friendProfile = itemView.findViewById(R.id.user_profile);
            friendLayout = itemView.findViewById(R.id.layout_friend);
            name_gender_age = itemView.findViewById(R.id.name_gender_age);
            remain_cnt_exp_date = itemView.findViewById(R.id.remain_cnt_exp_date);
        }
    }


    // Adapter를 생성할 때 받아오는 데이터와 컨텍스트
    public FriendAddAdapter(ArrayList<FriendInfoDto> friendsList,  Context context) {
        this.friendsList = friendsList;
        this.context = context;
    }

    // 새로운 뷰홀더를 생성한다.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 새로운 뷰(아이템(xml))을 인스턴스화한다.
        View itemFriend = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);

        FriendAddAdapter.FriendAddViewHolder vh = new FriendAddAdapter.FriendAddViewHolder(itemFriend);

        return vh;
    }

    // 생성한 뷰홀더읭 각각의 컴포넌트에 받아온 데이터를 매칭
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        FriendInfoDto friendInfo = friendsList.get(position);

        // 뷰홀더에 데이터 매칭
        ConstraintLayout friendLayout = ((FriendAddViewHolder)holder).friendLayout;

        int remainCnt = friendInfo.getTotalCnt() - friendInfo.getUsedCnt();

        // 성별
        String gender;
        if(friendInfo.getGender() == 0){
            gender = "남";
        } else{
            gender = "여";
        }

        /**
         * 나이 구하기
         * */
        // 현재 날짜
        Calendar current = Calendar.getInstance();
        int currentYear  = current.get(Calendar.YEAR);
        int currentMonth = current.get(Calendar.MONTH) + 1;
        int currentDay   = current.get(Calendar.DAY_OF_MONTH);

        //태어난 날짜
        String birthDay = friendInfo.getBirth();
        int birthYear = Integer.parseInt(birthDay.substring(0,4));
        int birthMonth = Integer.parseInt(birthDay.substring(4,6));
        int birthDayOfMonth = Integer.parseInt(birthDay.substring(6,birthDay.length()));

        int age = currentYear - birthYear;

        // 생일 안 지난 경우 -1
        if ((birthMonth * 100 + birthDayOfMonth) > (currentMonth * 100 + birthDayOfMonth)){
            age--;
        }

        ((FriendAddViewHolder)holder).friendProfile.setImageDrawable(context.getDrawable(R.drawable.profile_boy));

        if(friendInfo.getProfileId() != null){
            Glide.with(context).load("http://15.165.144.216" + friendInfo.getProfileId()).into(((FriendAddViewHolder)holder).friendProfile);
        }

        ((FriendAddViewHolder)holder).name_gender_age.setText(friendInfo.getUserName() + "(" + gender + "," + age + ")");

        // 수강중이고 잔여횟수가 0이상이면
        if(friendInfo.getLectureName() != null && !friendInfo.getLectureName().equals("") && remainCnt > 0){
            ((FriendAddViewHolder)holder).remain_cnt_exp_date.setText(friendInfo.getLectureName() + "(" + friendInfo.getUsedCnt() + "/" + friendInfo.getTotalCnt() + "회)");
            ((FriendAddViewHolder)holder).remain_cnt_exp_date.setVisibility(View.VISIBLE);
        } else{
            ((FriendAddViewHolder)holder).remain_cnt_exp_date.setVisibility(View.GONE);
        }

        /** 회원 클릭 시 다이얼로그 생성*/
        friendLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle(friendInfo.getUserName());

                builder.setItems(new String[]{"회원정보 보기", "채팅 하기"}, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int pos)
                    {
                        if(pos == 0){
                            /** 회원정보 보기 */
                            Intent intent = new Intent(context, MemberInfoActivity.class);

                            intent.putExtra("memberInfo",friendInfo);

                            context.startActivity(intent);
                        }else{
                            /** 채팅화면 생성*/
                            Intent intent = new Intent(context, ChattingActivity.class);

                            /** 채팅참여자 정보 생성 */
                            ArrayList<ChattingMemberDto> chatMemberList = new ArrayList<>();

                            // 트레이너 정보
                            UserInfoDto trainerInfo = TrainerHomeActivity.staticLoginUserInfo;
                            // 회원 정보
                            FriendInfoDto memberInfo = friendInfo;

                            chatMemberList.add(ChattingMemberDto.makeChatMemberInfo(trainerInfo));
                            chatMemberList.add(ChattingMemberDto.makeChatMemberInfo(memberInfo));

                            intent.putExtra("chatMemberList",chatMemberList);

                            context.startActivity(intent);
                        }
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    // 친구추가
    public void addFriend(FriendInfoDto friendInfoDto){
        friendsList.add(0,friendInfoDto);
        notifyItemInserted(getItemCount());
    }

   /* // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return anniversaryList.size();

    }

    // 친구정보를 리싸이클러뷰에 추가하기위한 메소드 => 해당 메소드 호출 시 자동갱신한다.
    public void addAnniversaryInfo(AnniversaryDto schInfo){
        anniversaryList.add(schInfo);
        notifyItemInserted(getItemCount());
    }

    // 일정 삭제
    private void deleteAnniversary(int position){
        anniversaryList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, anniversaryList.size());
    }

    // 일정 수정
    public void modifyAnniversary(int position, AnniversaryDto schInfo){
        anniversaryList.set(position,schInfo);
        notifyItemChanged(position);
    }

    public void clearAllItem(){
        int size = anniversaryList.size();
        anniversaryList.clear();
        notifyItemRangeRemoved(0, size);
    }*/
}
