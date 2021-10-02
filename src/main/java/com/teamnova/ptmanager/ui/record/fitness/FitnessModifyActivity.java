package com.teamnova.ptmanager.ui.record.fitness;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityFitnessModifyBinding;
import com.teamnova.ptmanager.databinding.ActivityFitnessRegisterBinding;
import com.teamnova.ptmanager.model.record.fitness.FitnessKinds;
import com.teamnova.ptmanager.model.record.fitness.FitnessRecord;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.record.fitness.FitnessService;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.ui.record.fitness.adapter.FitnessListAdapter;
import com.teamnova.ptmanager.ui.record.fitness.adapter.FitnessRecordListAdapter;
import com.teamnova.ptmanager.util.GetDate;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/** 운동기록 수정*/
public class FitnessModifyActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityFitnessModifyBinding binding;

    // 리사이클러뷰
    private FitnessRecordListAdapter fitnessRecordListAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    // 회원정보
    private FriendInfoDto memberInfo;

    // 수정할 날짜
    private String selectedDateYMD;

    // 조회한 운동기록 리스트
    private ArrayList<FitnessRecord> fitnessRecordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFitnessModifyBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();

        setOnClickListener();
    }

    public void setOnClickListener(){
        // 수정하기
        binding.btnModifyFitnessRecord.setOnClickListener(this);
        // 삭제하기
        binding.btnDeleteFitnessRecord.setOnClickListener(this);
        // 뒤로가기
        binding.btnBack.setOnClickListener(this);
    }

    // 초기화
    public void initialize(){
        Intent intent = getIntent();

        memberInfo = (FriendInfoDto) intent.getSerializableExtra("memberInfo");
        fitnessRecordList = (ArrayList<FitnessRecord>) intent.getSerializableExtra("fitnessRecordList");
        selectedDateYMD = (String) intent.getSerializableExtra("exerciseDate");

        if(this.selectedDateYMD == null) {
            this.selectedDateYMD = GetDate.getTodayDate();
        }

        // 운동기록 리스트 생성
        //retrieveFitnessRecordList(memberInfo, selectedDateYMD);

        // 운동날짜 입력
        binding.fitnessSelectedDate.setText(GetDate.getDateWithYMD(selectedDateYMD) + "(" + GetDate.getDayOfWeek(selectedDateYMD) + ")");

        // 트레이너는 수정/삭제 못하도록 버튼 막기
        hideBtnForModifyOrDelete();

        /** 리사이클러뷰 초기화 */
        recyclerView = binding.recyclerviewFitnessList;
        layoutManager = new LinearLayoutManager(FitnessModifyActivity.this);
        fitnessRecordListAdapter = new FitnessRecordListAdapter(fitnessRecordList, FitnessModifyActivity.this, memberInfo, selectedDateYMD);
        recyclerView.setAdapter(fitnessRecordListAdapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onClick(View v) {
        // 기록리스트 반환하기 - 사용자와 날짜는 운동기록 객체가 가지고 있는 데이터를 사용하기
        ArrayList<FitnessRecord> fitnessRecordList = fitnessRecordListAdapter.getFitnessList();

        switch(v.getId()){
            case R.id.btn_modify_fitness_record: // 수정하기
                // 서버에 데이터 전달하기
                /**
                 * 1. 서버에 운동기록 데이터 전달하기
                 */
                Retrofit retrofit= RetrofitInstance.getRetroClient();
                FitnessService service = retrofit.create(FitnessService.class);

                // http request 객체 생성
                Call<String> call = service.modifyFitnessRecordList(fitnessRecordList);

                new ModifyFitnessRecordList().execute(call);

                break;
            case R.id.btn_delete_fitness_record: // 삭제하기
                /**
                 * 1. 서버에 운동기록 삭제요청하기
                 */
                retrofit= RetrofitInstance.getRetroClient();
                service = retrofit.create(FitnessService.class);

                // http request 객체 생성
               call = service.deleteFitnessRecordList(fitnessRecordList);

                new DeleteFitnessRecordList().execute(call);

                break;
            case R.id.btn_back: // 뒤로가기
                onBackPressed();
                break;
        }
    }

    /** 운동기록 조회*/
    public ArrayList<FitnessRecord> retrieveFitnessRecordList(FriendInfoDto memberInfo, String selectedDateYMD){
        Retrofit retrofit= RetrofitInstance.getRetroClient();
        FitnessService service = retrofit.create(FitnessService.class);

        // http request 객체 생성
        Call<ArrayList<FitnessRecord>> call = service.getFitnessInfoByDay(memberInfo.getUserId(),selectedDateYMD);

        new GetFitnessListCall().execute(call);

        System.out.println("기록리스트 크기:" + fitnessRecordList.size());

        return fitnessRecordList;
    }

    /** 수정한 운동기록 서버에 전달*/
    private class ModifyFitnessRecordList extends AsyncTask<Call, Void, String> {
        private Response<String> response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPreExecute", "11");
        }

        @Override
        protected String  doInBackground(Call[] params) {
            try {
                Call<String> call = params[0];
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
                    // 테스트
                    /*Toast.makeText(FitnessModifyActivity.this, "운동기록 수정에 성공했습니다-1 " + result, Toast.LENGTH_SHORT).show();
                    Log.d("객체:", result);*/

                     // 완료 시 이전 화면으로 이동하기
                    Intent intent = new Intent(FitnessModifyActivity.this, FitnessKindListActivity.class);
                    intent.putExtra("isModified", "true");
                    intent.putExtra("modifiedFitnessRecordList", fitnessRecordList);

                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else{
                    Toast.makeText(FitnessModifyActivity.this, "운동기록 수정에 실패했습니다-1 " + result, Toast.LENGTH_SHORT).show();
                    Log.d("객체:", result);
                }
            }else{
                Toast.makeText(FitnessModifyActivity.this, "운동기록 수정에 실패했습니다-2 " + result, Toast.LENGTH_SHORT).show();
                Log.d("객체:", result);
            }
        }
    }

    /** 운동기록 삭제 요청*/
    private class DeleteFitnessRecordList extends AsyncTask<Call, Void, String> {
        private Response<String> response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPreExecute", "11");
        }

        @Override
        protected String  doInBackground(Call[] params) {
            try {
                Call<String> call = params[0];
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
                    // 테스트
                    /*Toast.makeText(FitnessModifyActivity.this, "운동기록 삭제에 성공했습니다-1 " + result, Toast.LENGTH_SHORT).show();
                    Log.d("객체:", result);*/

                    // 완료 시 이전 화면으로 이동하기
                    Intent intent = new Intent(FitnessModifyActivity.this, FitnessKindListActivity.class);
                    intent.putExtra("isDeleted", "true");
                    intent.putExtra("deletedDay", selectedDateYMD);

                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else{
                    Toast.makeText(FitnessModifyActivity.this, "운동기록 삭제에 실패했습니다-1 " + result, Toast.LENGTH_SHORT).show();
                    Log.d("객체:", result);
                }
            }else{
                Toast.makeText(FitnessModifyActivity.this, "운동기록 삭제에 실패했습니다-2 " + result, Toast.LENGTH_SHORT).show();
                Log.d("객체:", result);
            }
        }
    }

    /**  운동 정보 가져오기 */
    public class GetFitnessListCall extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<ArrayList<FitnessRecord>> response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPreExecute", "11");
        }

        @Override
        protected String  doInBackground(Call[] params) {
            try {
                Call<ArrayList<FitnessRecord>> call = params[0];
                response = call.execute();

                if(response.body() != null){
                    fitnessRecordList = response.body();
                }

                return response.body().toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // 리사이클러뷰 세팅
        }
    }

    /** 트레이너의 경우 수정 못하도록 관련 버튼 안보이게 하기*/
    public void hideBtnForModifyOrDelete() {
        // 트레이너 라면
        if (TrainerHomeActivity.staticLoginUserInfo != null) {
            binding.btnModifyFitnessRecord.setVisibility(View.GONE);
            binding.btnDeleteFitnessRecord.setVisibility(View.GONE);
        }
    }
}