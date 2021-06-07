package com.teamnova.ptmanager.ui.schedule.lecture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.adapter.friend.FriendAddAdapter;
import com.teamnova.ptmanager.adapter.lecture.LectureListAdapter;
import com.teamnova.ptmanager.databinding.ActivityLectureListBinding;
import com.teamnova.ptmanager.databinding.ActivityLessonRegisterBinding;
import com.teamnova.ptmanager.model.lecture.LectureInfoDto;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.ui.home.trainer.fragment.TrainerHomeFragment;
import com.teamnova.ptmanager.viewmodel.schedule.lecture.LectureViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * 1. [트레이너]내 강의 목록을 보여주는 액티비티
 * */
public class LectureListActivity extends AppCompatActivity {
    // binder
    private ActivityLectureListBinding binding;

    // LectureViewModel
    private LectureViewModel lectureViewModel;

    // Retrofit 통신의 결과를 전달하는 핸들러
    private Handler resultHandler;

    // 리사이클러뷰
    private LectureListAdapter lectureListAdapter;
    private RecyclerView recyclerView_lecture_list;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다.
        binding = ActivityLectureListBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();

    }

    // 초기화
    public void initialize(){
        // lectureiewModel 초기화
        lectureViewModel = new ViewModelProvider(this).get(LectureViewModel.class);

        // 트레이너 아이디
        String trainerLoginId = TrainerHomeActivity.staticLoginUserInfo.getLoginId();

        // Retrofit 통신 핸들러
        resultHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                // 강의목록 가져오기 성공
                if(msg.what == 0){
                    // 친구목록 가져오기
                    // 강의목록 데이터 가져 옴
                    ArrayList<LectureInfoDto> lectureList = (ArrayList<LectureInfoDto>)msg.obj;

                    // 리사이클러뷰 세팅
                    recyclerView_lecture_list = binding.recyclerviewMyLectureList;
                    layoutManager = new LinearLayoutManager(LectureListActivity.this);
                    recyclerView_lecture_list.setLayoutManager(layoutManager);

                    // 데이터 가져와서 adapter에 넘겨 줌
                    lectureListAdapter = new LectureListAdapter(lectureList, LectureListActivity.this);
                    recyclerView_lecture_list.setAdapter(lectureListAdapter);
                }
            }
        };

        // 강의목록 가져오기 통신
        lectureViewModel.getLectureList(resultHandler, trainerLoginId);
    }
}