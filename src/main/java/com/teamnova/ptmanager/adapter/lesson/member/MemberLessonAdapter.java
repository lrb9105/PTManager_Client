package com.teamnova.ptmanager.adapter.lesson.member;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.model.lecture.LectureInfoDto;
import com.teamnova.ptmanager.model.lesson.LessonSchInfo;
import com.teamnova.ptmanager.ui.home.member.DayViewActivity;
import com.teamnova.ptmanager.ui.home.member.Event;
import com.teamnova.ptmanager.ui.schedule.lecture.pass.PassRegisterActivity;
import com.teamnova.ptmanager.ui.schedule.lesson.LessonDetailActivity;
import com.teamnova.ptmanager.ui.schedule.lesson.LessonRegisterActivity;
import com.teamnova.ptmanager.util.GetDate;

import java.util.ArrayList;

/**
 *  회원의 레슨목록을 보여주는 아답터
 * */
public class MemberLessonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    // ViewHolder에 매칭시키기위한 회원 레슨목록 데이터
    private ArrayList<LessonSchInfo> lessonSchList;
    private Context context;
    private ActivityResultLauncher<Intent> startActivityResult;

    // 회원레슨정보를 담을 뷰홀더
    public class LessonInfoViewHolder extends RecyclerView.ViewHolder {
        // 클릭 시 레슨내역으로 이동시키기 위해 필요한 레이아웃
        ConstraintLayout constLayoutMemberSch;
        TextView lessonDate;
        TextView lessonName;
        TextView lessonState;

        public LessonInfoViewHolder(View itemView) {
            super(itemView);
            constLayoutMemberSch = itemView.findViewById(R.id.const_layout_member_sch);
            lessonDate = itemView.findViewById(R.id.lesson_date);
            lessonName = itemView.findViewById(R.id.lesson_name);
            lessonState = itemView.findViewById(R.id.lesson_state);
        }
    }


    // Adapter를 생성할 때 받아오는 데이터와 컨텍스트
    public MemberLessonAdapter(ArrayList<LessonSchInfo> lessonSchList, Context context, ActivityResultLauncher<Intent> startActivityResult) {
        this.lessonSchList = lessonSchList;
        this.context = context;
        this.startActivityResult = startActivityResult;
    }

    // 새로운 뷰홀더를 생성한다.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 새로운 뷰(아이템(xml))을 인스턴스화한다.
        View itemLesson = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_sch, parent, false);

        MemberLessonAdapter.LessonInfoViewHolder vh = new MemberLessonAdapter.LessonInfoViewHolder(itemLesson);

        return vh;
    }

    // 생성한 뷰홀더의 각각의 컴포넌트에 받아온 데이터를 매칭
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        LessonSchInfo lessonSchInfo = lessonSchList.get(position);

        // 뷰홀더에 데이터 매칭
        // 레슨등록 화면에 강의정보를 추가하기 위해 이벤트를 걸어줄 레이아웃이 필요
        ConstraintLayout constLayoutMemberSch = ((LessonInfoViewHolder)holder).constLayoutMemberSch;

        // 레슨일정(yyyy.mm.dd(요일)\nhh:mm~hh:mm)
        String lessonDate = "";
        lessonDate += GetDate.getDateWithYMD(lessonSchInfo.getLessonDate());
        lessonDate += "(" + GetDate.getDayOfWeek(lessonSchInfo.getLessonDate()) + ")\n";
        String srtTime = lessonSchInfo.getLessonSrtTime();
        String endTime = lessonSchInfo.getLessonEndTime();

        if(srtTime.endsWith(":0")){
            srtTime = srtTime.replace(":0",":00");
        }

        if(endTime.endsWith(":0")){
            endTime = endTime.replace(":0",":00");
        }

        lessonDate += srtTime + "~" + endTime;

        ((LessonInfoViewHolder)holder).lessonDate.setText(lessonDate);

        // 강의 명
        ((LessonInfoViewHolder)holder).lessonName.setText(lessonSchInfo.getLectureName());

        // 레슨상태(예약대기, 예약, 출석, 결석)
        String attendanceYn = lessonSchInfo.getAttendanceYn();
        String attendanceYnName = "";
        String confirmYnName = "";
        String attendanceYnOrConfirmYn = "";


        // 출석체크를 하지 않았고 취소를 하지 않았다면
        if((attendanceYn == null || attendanceYn.isEmpty()) && lessonSchInfo.getCancelYn().equals("N")){
            if("Y".equals(lessonSchInfo.getConfirmYn())){
                confirmYnName = "예약";
            } else if(lessonSchInfo.getReservationConfirmYn().equals("M")){
                confirmYnName = "예약대기";
            }
            attendanceYnOrConfirmYn = confirmYnName;
        } else if(!lessonSchInfo.getCancelYn().equals("N")){ // 취소여부가 N(기본값)이아니라면 -> M: 취소대기, Y:예약 취소
            if(lessonSchInfo.getCancelYn().equals("M")){
                attendanceYnOrConfirmYn = "취소대기";
            } else if(lessonSchInfo.getCancelYn().equals("Y")){
                attendanceYnOrConfirmYn = "예약취소";
            }
        } else { // 나머진 출석관련
            attendanceYnName = lessonSchInfo.getAttendanceYnName();
            attendanceYnOrConfirmYn = attendanceYnName;
        }

        ((LessonInfoViewHolder)holder).lessonState.setText(attendanceYnOrConfirmYn);


        // 강의정보 클릭 시 강의정보를 요청한 화면으로 선택 된 강의정보를 보낸다.
        constLayoutMemberSch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("회원 레슨일정 정보 클릭", "1111");
                Intent intent = new Intent(context, LessonDetailActivity.class);
                // 레슨 일정 ID 보냄
                intent.putExtra("lessonSchId", lessonSchInfo.getLessonSchId());
                // 다시 돌아올 때 구분할 액티비티명 같이 보냄
                intent.putExtra("actName", "MemberHomeActivity");

                // 사용자 타입- 0: 트레이너, 1: 일반회원
                intent.putExtra("userType", "1");

                // intent전송
                startActivityResult.launch(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return  lessonSchList.size();
    }

    // 레슨정보 추가
    public void addLesson(LessonSchInfo lessonSchInfo){
        lessonSchList.add(0,lessonSchInfo);
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
