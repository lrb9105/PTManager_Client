package com.teamnova.ptmanager.ui.schedule.lecture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.adapter.lecture.LectureListAdapter;
import com.teamnova.ptmanager.adapter.lecture.LectureRegisteredMemberListAdapter;
import com.teamnova.ptmanager.databinding.ActivityLectureRegisterdMemberListBinding;
import com.teamnova.ptmanager.databinding.ActivityLessonRegisterBinding;
import com.teamnova.ptmanager.model.lecture.LectureInfoDto;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.viewmodel.schedule.lecture.LectureViewModel;

import java.util.ArrayList;

public class LectureRegisterdMemberListActivity extends AppCompatActivity {
    // binder
    private ActivityLectureRegisterdMemberListBinding binding;

    // LectureViewModel
    private LectureViewModel lectureViewModel;

    // Retrofit 통신의 결과를 전달하는 핸들러
    private Handler resultHandler;

    // 리사이클러뷰
    private LectureRegisteredMemberListAdapter lectureRegisteredMemberListAdapter;
    private RecyclerView recyclerView_member_list;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다.
        binding = ActivityLectureRegisterdMemberListBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();

    }

    // 초기화
    public void initialize(){
        // lectureiewModel 초기화
        lectureViewModel = new ViewModelProvider(this).get(LectureViewModel.class);

        // 강의 ID
        Intent intent = getIntent();
        String lectureId = null;

        if(intent != null){
            // 강의 id
            lectureId = intent.getStringExtra("lectureId");
        }

        // Retrofit 통신 핸들러
        resultHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                // 회원 목록 가져오기 성공
                if(msg.what == 1){
                    // 회원 목록 가져오기
                    // 회원 목록 데이터 가져 옴
                    ArrayList<FriendInfoDto> registeredMemberList = (ArrayList<FriendInfoDto>)msg.obj;

                    // 리사이클러뷰 세팅
                    recyclerView_member_list = binding.recyclerviewMemberList;
                    layoutManager = new LinearLayoutManager(LectureRegisterdMemberListActivity.this);
                    recyclerView_member_list.setLayoutManager(layoutManager);

                    // 데이터 가져와서 adapter에 넘겨 줌
                    lectureRegisteredMemberListAdapter = new LectureRegisteredMemberListAdapter(registeredMemberList, LectureRegisterdMemberListActivity.this);
                    recyclerView_member_list.setAdapter(lectureRegisteredMemberListAdapter);
                }
            }
        };

        // 넘어온 강의 id가 있다면
        if(lectureId != null){
            // 강의를 수강할 수 있는 회원목록 가져오기 통신
            lectureViewModel.getLectureRegisteredMemberList(resultHandler, lectureId);
        }
    }
}