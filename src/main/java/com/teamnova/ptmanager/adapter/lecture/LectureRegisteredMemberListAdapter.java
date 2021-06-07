package com.teamnova.ptmanager.adapter.lecture;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.model.lecture.LectureInfoDto;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 수강가능한 회원목록 보여주는 리사이클러뷰 아답터
 * */
public class LectureRegisteredMemberListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    // ViewHolder에 매칭시키기위한 회원목록 데이터
    private ArrayList<FriendInfoDto> memberList;
    private Context context;

    // 강의정보를 담을 뷰홀더
    public class LectureInfoViewHolder extends RecyclerView.ViewHolder {
        CircleImageView friendProfile;
        ConstraintLayout memberLayout;
        TextView name_gender_age;
        TextView remain_cnt_exp_date;
        CheckBox register_to_lesson;

        public LectureInfoViewHolder(View itemView) {
            super(itemView);
            memberLayout = itemView.findViewById(R.id.layout_friend);
            name_gender_age = itemView.findViewById(R.id.name_gender_age);
            remain_cnt_exp_date = itemView.findViewById(R.id.remain_cnt_exp_date);
            register_to_lesson = itemView.findViewById(R.id.register_to_lesson);
        }
    }


    // Adapter를 생성할 때 받아오는 데이터와 컨텍스트
    public LectureRegisteredMemberListAdapter(ArrayList<FriendInfoDto> memberList, Context context) {
        this.memberList = memberList;
        this.context = context;
    }

    // 새로운 뷰홀더를 생성한다.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 새로운 뷰(아이템(xml))을 인스턴스화한다.
        View itemFriend = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);

        LectureRegisteredMemberListAdapter.LectureInfoViewHolder vh = new LectureRegisteredMemberListAdapter.LectureInfoViewHolder(itemFriend);

        return vh;
    }

    // 생성한 뷰홀더의 각각의 컴포넌트에 받아온 데이터를 매칭
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        FriendInfoDto memberInfo = memberList.get(position);

        // 뷰홀더에 데이터 매칭
        ConstraintLayout memberLayout = ((LectureInfoViewHolder)holder).memberLayout;

        // 체크박스 보이게
        ((LectureInfoViewHolder)holder).register_to_lesson.setVisibility(View.VISIBLE);

        int remainCnt = memberInfo.getTotalCnt() - memberInfo.getUsedCnt();

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

        ((LectureInfoViewHolder)holder).name_gender_age.setText(memberInfo.getUserName() + "(" + gender + "," + age + ")");

        // 수강중이고 잔여횟수가 0이상이면
        if(memberInfo.getLectureName() != null && !memberInfo.getLectureName().equals("") && remainCnt > 0){
            ((LectureInfoViewHolder)holder).remain_cnt_exp_date.setText(memberInfo.getLectureName() + "(" + memberInfo.getUsedCnt() + "/" + memberInfo.getTotalCnt() + "회)");
            ((LectureInfoViewHolder)holder).remain_cnt_exp_date.setVisibility(View.VISIBLE);
        } else{
            ((LectureInfoViewHolder)holder).remain_cnt_exp_date.setVisibility(View.GONE);
        }

        /*memberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("목록에서 아이템 클릭 시 해당하는 회원의 회원정보 액티비티로 이동 USER_ID 출력", friendInfo.getUserId());

                // 다이얼로그 출력
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setMessage("비밀번호 초기화가 완료되었습니다.");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {

                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return memberList.size();
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
