package com.teamnova.ptmanager.adapter.lecture;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 수강가능한 회원목록 보여주는 리사이클러뷰 아답터
 * */
public class LessonRegisteredMemberListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    // ViewHolder에 매칭시키기위한 회원목록 데이터
    private ArrayList<FriendInfoDto> memberList;
    private Context context;

    // 강의정보를 담을 뷰홀더
    public class LessonInfoViewHolder extends RecyclerView.ViewHolder {
        CircleImageView friendProfile;
        ConstraintLayout memberLayout;
        TextView name_gender_age;
        RadioGroup radioGroup;

        public LessonInfoViewHolder(View itemView) {
            super(itemView);
            friendProfile = itemView.findViewById(R.id.user_profile);
            memberLayout = itemView.findViewById(R.id.layout_friend);
            name_gender_age = itemView.findViewById(R.id.name_gender_age);
            radioGroup = itemView.findViewById(R.id.attendance_or_not);
        }
    }


    // Adapter를 생성할 때 받아오는 데이터와 컨텍스트
    public LessonRegisteredMemberListAdapter(ArrayList<FriendInfoDto> memberList, Context context) {
        this.memberList = memberList;
        this.context = context;

        // 출석여부 "Y"로 초기화
        for(int i = 0; i < this.memberList.size(); i++){
            this.memberList.get(i).setCheck("Y");
        }
    }

    // 새로운 뷰홀더를 생성한다.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 새로운 뷰(아이템(xml))을 인스턴스화한다.
        View itemMember = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lessoned_member, parent, false);

        LessonRegisteredMemberListAdapter.LessonInfoViewHolder vh = new LessonRegisteredMemberListAdapter.LessonInfoViewHolder(itemMember);

        return vh;
    }

    // 생성한 뷰홀더의 각각의 컴포넌트에 받아온 데이터를 매칭
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        FriendInfoDto memberInfo = memberList.get(position);

        if(memberInfo.getProfileId() != null){
            Log.d("있음","11");
            Glide.with(context).load("http://15.165.144.216" + memberInfo.getProfileId()).into(((LessonInfoViewHolder)holder).friendProfile);
        } else{
            Log.d("없음","22");
        }

        Log.d("Adapter수강권: ", memberInfo.getLecturePassId());

        // 뷰홀더에 데이터 매칭
        ConstraintLayout memberLayout = ((LessonInfoViewHolder)holder).memberLayout;

        // 성별
        String gender;
        if(memberInfo.getGender() == 0){
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
        String birthDay = memberInfo.getBirth();
        int birthYear = Integer.parseInt(birthDay.substring(0,4));
        int birthMonth = Integer.parseInt(birthDay.substring(4,6));
        int birthDayOfMonth = Integer.parseInt(birthDay.substring(6,birthDay.length()));

        int age = currentYear - birthYear;

        // 생일 안 지난 경우 -1
        if ((birthMonth * 100 + birthDayOfMonth) > (currentMonth * 100 + birthDayOfMonth)){
            age--;
        }

        ((LessonInfoViewHolder)holder).name_gender_age.setText(memberInfo.getUserName() + "(" + gender + "," + age + ")");

        // 클릭이벤트
        RadioGroup radioGroup = ((LessonInfoViewHolder)holder).radioGroup;

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                RadioButton select = radioGroup.findViewById(id);
                String selectStr = select.getText().toString();

                // 라디오버튼 선택 시 회원정보에 출석값 변경
                if(selectStr.equals("출석")){
                    memberInfo.setCheck("Y");
                } else{
                    memberInfo.setCheck("N");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public ArrayList<FriendInfoDto> getMemberList(){
        return this.memberList;
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
