package com.teamnova.ptmanager.ui.schedule.lesson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.adapter.lecture.LectureRegisteredMemberListAdapter;
import com.teamnova.ptmanager.adapter.lecture.LessonRegisteredMemberListAdapter;
import com.teamnova.ptmanager.databinding.ActivityLessonAttendanceBinding;
import com.teamnova.ptmanager.model.lesson.AttendanceInfo;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.ui.login.LoginActivity;
import com.teamnova.ptmanager.ui.login.findpw.FindPw3Activity;
import com.teamnova.ptmanager.ui.member.MemberAddActivity;
import com.teamnova.ptmanager.ui.schedule.lecture.LectureRegisterdMemberListActivity;
import com.teamnova.ptmanager.ui.schedule.schedule.DailyScheduleActivity;
import com.teamnova.ptmanager.util.GetDate;
import com.teamnova.ptmanager.viewmodel.schedule.lecture.LectureViewModel;
import com.teamnova.ptmanager.viewmodel.schedule.lesson.LessonViewModel;

import java.util.ArrayList;
/**
 *  레슨 출석체크 액티비티
 * */
public class LessonAttendanceActivity extends AppCompatActivity implements View.OnClickListener{
    // binder
    private ActivityLessonAttendanceBinding binding;

    // LectureViewModel
    private LectureViewModel lectureViewModel;

    // LessonViewModel
    private LessonViewModel lessonViewModel;

    // Retrofit 통신의 결과를 전달하는 핸들러
    private Handler resultHandler;

    // 리사이클러뷰
    private LessonRegisteredMemberListAdapter lessonRegisteredMemberListAdapter;
    private RecyclerView recyclerView_lessoned_member_list;
    private RecyclerView.LayoutManager layoutManager;

    // 레슨 id
    private String lessonId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다.
        binding = ActivityLessonAttendanceBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();

    }

    public void setOnClickListener(){
        binding.selectMember.setOnClickListener(this);
    }

    // 초기화
    public void initialize(){
        // lectureiewModel 초기화
        lectureViewModel = new ViewModelProvider(this).get(LectureViewModel.class);
        lessonViewModel = new ViewModelProvider(this).get(LessonViewModel.class);

        // 강의 ID
        Intent intent = getIntent();

        if(intent != null){
            // 강의 id
            lessonId = intent.getStringExtra("lessonId");
        }

        // Retrofit 통신 핸들러
        resultHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                // 회원 목록 가져오기 성공
                if(msg.what == 2){
                    // 회원 목록 가져오기
                    // 회원 목록 데이터 가져 옴
                    ArrayList<FriendInfoDto> registeredMemberList = (ArrayList<FriendInfoDto>)msg.obj;

                    Log.d("프로필사진 잘 가져오나?", registeredMemberList.get(0).toString());

                    // 리사이클러뷰 세팅
                    recyclerView_lessoned_member_list = binding.recyclerviewLessonedMemberList;
                    layoutManager = new LinearLayoutManager(LessonAttendanceActivity.this);
                    recyclerView_lessoned_member_list.setLayoutManager(layoutManager);

                    // 데이터 가져와서 adapter에 넘겨 줌
                    lessonRegisteredMemberListAdapter = new LessonRegisteredMemberListAdapter(registeredMemberList, LessonAttendanceActivity.this);
                    recyclerView_lessoned_member_list.setAdapter(lessonRegisteredMemberListAdapter);
                } else if(msg.what == 3) { //츨석정보 저장 완료
                    String result = (String)msg.obj;

                    Log.d("출석정보 저장 완료", result);

                    Intent intent = new Intent(LessonAttendanceActivity.this, LessonDetailActivity.class);

                    // 사용자 타입- 0: 트레이너, 1: 일반회원
                    intent.putExtra("userType", "0");

                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        };

        // 넘어온 강의 id가 있다면
        if(lessonId != null){
            // 강의를 수강할 수 있는 회원목록 가져오기 통신
            lectureViewModel.getLessonRegisteredMemberList(resultHandler, lessonId);
        }

        setOnClickListener();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case  R.id.select_member: // 출석체크 완료
                // 다이얼로그빌더
                AlertDialog.Builder builder = new AlertDialog.Builder(LessonAttendanceActivity.this);

                builder.setMessage("출석체크를 완료하시겠습니까?");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        ArrayList<FriendInfoDto> memberList = lessonRegisteredMemberListAdapter.getMemberList();
                        // 서버로 보낼 출석정보
                        ArrayList<AttendanceInfo> attendanceInfoList = new ArrayList<>();

                        // 출석정보 만들어 줌
                        for(FriendInfoDto memberInfo : memberList){
                            Log.d("Act수강권: ", memberInfo.getLecturePassId());
                            AttendanceInfo tempInfo = new AttendanceInfo(lessonId, memberInfo.getLecturePassId(), memberInfo.getCheck());
                            attendanceInfoList.add(tempInfo);
                        }

                        // 서버로 출석정보 보냄
                        lessonViewModel.checkAttendance(resultHandler, attendanceInfoList);
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
            default:
                break;
        }
    }
}