package com.teamnova.ptmanager.adapter.lecture;

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

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.model.lecture.LectureInfoDto;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.ui.schedule.lecture.pass.PassRegisterActivity;
import com.teamnova.ptmanager.ui.schedule.lesson.LessonRegisterActivity;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 강의목록을 보여주는 리사이클러뷰 아답터
 * */
public class LectureListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    // ViewHolder에 매칭시키기위한 친구목록 데이터
    private ArrayList<LectureInfoDto> lectureList;
    private Context context;
    // setResult를 adapter에서 사용하기 위해 부모 act의 참조를 받아옴
    private Activity activity;
    // 결과값을 요청 액티비티에 전달하기 위해 activity의 name값을 받아옴
    private String toActivityName;

    // 강의정보를 담을 뷰홀더
    public class LectureInfoViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutLectureInfo;
        TextView lectureName;
        TextView lectureTime;
        TextView lecturedMemberCnt;

        public LectureInfoViewHolder(View itemView) {
            super(itemView);
            layoutLectureInfo = itemView.findViewById(R.id.layout_lecture_info);
            lectureName = itemView.findViewById(R.id.lecture_name);
            lectureTime = itemView.findViewById(R.id.lecture_time);
            lecturedMemberCnt = itemView.findViewById(R.id.be_lectured_member_cnt);
        }
    }


    // Adapter를 생성할 때 받아오는 데이터와 컨텍스트
    public LectureListAdapter(ArrayList<LectureInfoDto> lectureList, Context context, Activity activity, String toActivityName) {
        this.lectureList = lectureList;
        this.context = context;
        this.activity = activity;
        this.toActivityName = toActivityName;
    }

    // 새로운 뷰홀더를 생성한다.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 새로운 뷰(아이템(xml))을 인스턴스화한다.
        View itemFriend = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_lecture, parent, false);

        LectureListAdapter.LectureInfoViewHolder vh = new LectureListAdapter.LectureInfoViewHolder(itemFriend);

        return vh;
    }

    // 생성한 뷰홀더의 각각의 컴포넌트에 받아온 데이터를 매칭
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        LectureInfoDto lectureInfo = lectureList.get(position);

        Log.d("lectureInfo", lectureInfo.toString());

        // 뷰홀더에 데이터 매칭
        // 레슨등록 화면에 강의정보를 추가하기 위해 이벤트를 걸어줄 레이아웃이 필요
        LinearLayout lectureIntoLayout = ((LectureInfoViewHolder)holder).layoutLectureInfo;

        // 강의정보
        ((LectureInfoViewHolder)holder).lectureName.setText(lectureInfo.getLectureName());

        // 레슨 시간
        ((LectureInfoViewHolder)holder).lectureTime.setText("수업시간: " + lectureInfo.getLectureDuration() + "분");

        // 진행 중 인원
        ((LectureInfoViewHolder)holder).lecturedMemberCnt.setText("진행중: " + lectureInfo.getCurrentLecturedCnt() + "/" + lectureInfo.getTotalParticipationCnt() + "명");


        // 강의정보 클릭 시 강의정보를 요청한 화면으로 선택 된 강의정보를 보낸다.
        lectureIntoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 다이얼로그 출력
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setMessage("[" + lectureInfo.getLectureName() +"] \n선택하신 강의를 추가하시겠습니까?");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Intent intent = null;

                        // 수강권 등록
                        if(toActivityName.equals("PassRegisterActivity")){
                            intent = new Intent(activity, PassRegisterActivity.class);
                        } else if(toActivityName.equals("LessonRegisterActivity")){ // 강의 등록
                            intent = new Intent(activity, LessonRegisterActivity.class);
                        }

                        // 강의정보를 보내 줌
                        intent.putExtra("lectureInfo", lectureInfo);
                        activity.setResult(Activity.RESULT_OK, intent);
                        activity.finish();

                        Log.d("강의 선택 완료", "11");
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return lectureList.size();
    }

    // 강의정보 추가
    public void addFriend(LectureInfoDto lectureInfoDto){
        lectureList.add(lectureInfoDto);
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
