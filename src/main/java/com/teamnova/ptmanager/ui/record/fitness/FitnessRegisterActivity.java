package com.teamnova.ptmanager.ui.record.fitness;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.adapter.lecture.LectureRegisteredMemberListAdapter;
import com.teamnova.ptmanager.databinding.ActivityFitnessRegisterBinding;
import com.teamnova.ptmanager.model.record.fitness.FitnessKinds;
import com.teamnova.ptmanager.model.record.fitness.FitnessRecord;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.ui.home.member.MemberHomeActivity;
import com.teamnova.ptmanager.ui.record.fitness.adapter.FitnessListAdapter;
import com.teamnova.ptmanager.ui.record.fitness.adapter.FitnessRecordListAdapter;
import com.teamnova.ptmanager.ui.schedule.lecture.LectureRegisterdMemberListActivity;
import com.teamnova.ptmanager.ui.schedule.lesson.LessonRegisterActivity;
import com.teamnova.ptmanager.viewmodel.schedule.lecture.LectureViewModel;

import java.util.ArrayList;

/** 운동기록 입력*/
public class FitnessRegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityFitnessRegisterBinding binding;

    // 운동기록 입력 화면에서 넘어옴
    private ActivityResultLauncher<Intent> startActivityResult;

    // 운동기록 수정/삭제 화면
    private ActivityResultLauncher<Intent> startActivityResult2;

    // 리사이클러뷰
    private FitnessRecordListAdapter fitnessRecordListAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    // 회원정보
    private FriendInfoDto memberInfo;

    // 등록할 운동 리스트
    private ArrayList<FitnessKinds> registeredFitnessList;

    // 등록할 날짜
    private String selectedDateYMD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 운동기록 데이터 등록 시
        startActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {

                        }
                    }
                });

        // 운동기록 데이터 수정/삭제 시
        startActivityResult2 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intent = result.getData();
                            // 수정 시
                            String isModified = intent.getStringExtra("isModified");
                            // 삭제 시
                            String isDeleted = intent.getStringExtra("isDeleted");

                            // 수정 시
                            /*if(isModified != null && isModified.equals("true")){
                                FitnessRecord modifiedFitnessRecord = (FitnessRecord)intent.getSerializableExtra("modifiedFitnessRecord");
                                int position = intent.getIntExtra("position", 9999);

                                // adapter의 modify실행
                                fitnessListAdapter.modifyFitnessInfo(position, modifiedFitnessRecord);
                            } else if(isDeleted != null && isDeleted.equals("true")){ // 삭제제 시
                                int position = intent.getIntExtra("position", 9999);
                                // adapter delete 실행
                                fitnessListAdapter.deleteInBodyInfo(position);
                            }*/
                        }
                    }
                });
        super.onCreate(savedInstanceState);

        binding = ActivityFitnessRegisterBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();

        setOnClickListener();
    }

    public void setOnClickListener(){
        // 등록하기
        binding.btnRegisterFitnessRecord.setOnClickListener(this);
        // 뒤로가기
        binding.btnBack.setOnClickListener(this);
    }

    // 초기화
    public void initialize(){
        Intent intent = getIntent();
        memberInfo = (FriendInfoDto) intent.getSerializableExtra("memberInfo");
        selectedDateYMD = (String) intent.getSerializableExtra("selectedDateYMD");
        registeredFitnessList = (ArrayList<FitnessKinds>) intent.getSerializableExtra("registeredFitnessList");


        /** 리사이클러뷰 초기화 */
        recyclerView = binding.recyclerviewFitnessList;
        layoutManager = new LinearLayoutManager(this);
        fitnessRecordListAdapter = new FitnessRecordListAdapter(registeredFitnessList, this, memberInfo, selectedDateYMD);
        recyclerView.setAdapter(fitnessRecordListAdapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_register_fitness_record: //
                Intent intent = new Intent(this, FitnessKindListActivity.class);
                intent.putExtra("selectedDateYMD", selectedDateYMD);

                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            case R.id.btn_back: // 뒤로가기
                onBackPressed();
        }
    }
}