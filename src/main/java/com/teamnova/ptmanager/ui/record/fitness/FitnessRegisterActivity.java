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
import android.os.AsyncTask;
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
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBodyCompare;
import com.teamnova.ptmanager.model.record.fitness.FitnessKinds;
import com.teamnova.ptmanager.model.record.fitness.FitnessRecord;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.changehistory.eyebody.EyeBodyService;
import com.teamnova.ptmanager.network.record.fitness.FitnessService;
import com.teamnova.ptmanager.test.TestActivity;
import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodyCompareHistoryActivity;
import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodySelectActivity;
import com.teamnova.ptmanager.ui.home.member.MemberHomeActivity;
import com.teamnova.ptmanager.ui.record.fitness.adapter.FitnessListAdapter;
import com.teamnova.ptmanager.ui.record.fitness.adapter.FitnessRecordListAdapter;
import com.teamnova.ptmanager.ui.schedule.lecture.LectureRegisterdMemberListActivity;
import com.teamnova.ptmanager.ui.schedule.lesson.LessonRegisterActivity;
import com.teamnova.ptmanager.util.GetDate;
import com.teamnova.ptmanager.viewmodel.schedule.lecture.LectureViewModel;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/** 운동기록 입력 화면*/
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

    // 가져온 운동 리스트
    private ArrayList<FitnessKinds> registeredFitnessList;

    // 등록할 날짜
    private String selectedDateYMD;

    // 운동기록 리스트
    private ArrayList<FitnessRecord> fitnessRecordList;

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

        if(this.selectedDateYMD == null) {
            this.selectedDateYMD = GetDate.getTodayDate();
        }

        // 운동기록 리스트 생성
        fitnessRecordList = createFitnessRecordList(registeredFitnessList);

        // 운동날짜 입력
        binding.fitnessSelectedDate.setText(GetDate.getDateWithYMD(selectedDateYMD) + "(" + GetDate.getDayOfWeek(selectedDateYMD) + ")");


        /** 리사이클러뷰 초기화 */
        recyclerView = binding.recyclerviewFitnessList;
        layoutManager = new LinearLayoutManager(this);
        fitnessRecordListAdapter = new FitnessRecordListAdapter(fitnessRecordList, this, memberInfo, selectedDateYMD);
        recyclerView.setAdapter(fitnessRecordListAdapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    /** 운동기록 생성*/
    public ArrayList<FitnessRecord> createFitnessRecordList(ArrayList<FitnessKinds> fitnessKindsList){
        ArrayList<FitnessRecord> fitnessRecordList = new ArrayList<>();
        String userId = memberInfo.getUserId();

        // fitnessKindsList의 갯수만큼 반복
        for(FitnessKinds fitness : fitnessKindsList){
            // fitness의 정보
            String fitnessKindsId = fitness.getFitnessKindId();
            String part = fitness.getPart();
            String fitnessKindName = fitness.getFitnessKindName();
            String fitnessType = fitness.getFitnessType();
            String fitnessKindsType = fitness.getFitnessKindsType();

            // 운동기록 객체 만듬
            FitnessRecord fitnessRecord = new FitnessRecord(null, userId, fitnessKindsId, selectedDateYMD, part, fitnessKindName, fitnessType, fitnessKindsType, new ArrayList<>());

            // 운동기록 리스트에 넣어줌
            fitnessRecordList.add(fitnessRecord);
        }

        System.out.println("기록리스트 크기:" + fitnessRecordList.size());

        return fitnessRecordList;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_register_fitness_record: // 등록하기
                // 기록리스트 반환하기 - 사용자와 날짜는 운동기록 객체가 가지고 있는 데이터를 사용하기
                ArrayList<FitnessRecord> fitnessRecordList = fitnessRecordListAdapter.getFitnessList();

                // 서버에 데이터 전달하기
                /**
                 * 1. 서버에 운동기록 데이터 전달하기
                 */
                Retrofit retrofit= RetrofitInstance.getRetroClient();
                FitnessService service = retrofit.create(FitnessService.class);

                // http request 객체 생성
                Call<String> call = service.registerFitnessRecordList(fitnessRecordList);

                new RegisterFitnessRecordList().execute(call);

                break;
            case R.id.btn_back: // 뒤로가기
                onBackPressed();
        }
    }

    /** 운동기록 서버에 전달*/
    private class RegisterFitnessRecordList extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<String> response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPreExecute", "11");
        }

        @Override
        protected String  doInBackground(Call[] params) {
            try {
                retrofit2.Call<String> call = params[0];
                response = call.execute();

                return response.body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // 성공 시 SUCCESS 문자열 반환
            if(!result.equals("")){
                if(result.contains("SUCCESS")){
                    Toast.makeText(FitnessRegisterActivity.this, "운동기록 저장에 성공했습니다. " + result, Toast.LENGTH_SHORT).show();
                    Log.d("객체:", result);

                     // 완료 시 이전 화면으로 이동하기
                    Intent intent = new Intent(FitnessRegisterActivity.this, FitnessKindListActivity.class);
                    intent.putExtra("selectedDateYMD", selectedDateYMD);
                    intent.putExtra("fitnessRecordList", fitnessRecordList);

                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else{
                    Toast.makeText(FitnessRegisterActivity.this, "운동기록 저장에 실패했습니다-1 " + result, Toast.LENGTH_SHORT).show();
                    Log.d("객체:", result);
                }
            }else{
                Toast.makeText(FitnessRegisterActivity.this, "운동기록 저장에 실패했습니다-2 " + result, Toast.LENGTH_SHORT).show();
                Log.d("객체:", result);
            }
        }
    }
}