package com.teamnova.ptmanager.ui.record.fitness;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.teamnova.ptmanager.MainActivity;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityFitnessKindListBinding;
import com.teamnova.ptmanager.databinding.ActivityFitnessRegisterBinding;
import com.teamnova.ptmanager.model.record.fitness.FitnessKinds;
import com.teamnova.ptmanager.model.record.fitness.FitnessRecord;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.record.fitness.FitnessService;
import com.teamnova.ptmanager.ui.home.member.MemberHomeActivity;
import com.teamnova.ptmanager.ui.home.member.ViewPagerAdapter;
import com.teamnova.ptmanager.ui.record.fitness.adapter.FitnessKindsListVPAdapter;
import com.teamnova.ptmanager.ui.record.fitness.adapter.FitnessListAdapter;
import com.teamnova.ptmanager.viewmodel.record.fitness.FitnessViewModel;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FitnessKindListActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    // binder
    private ActivityFitnessKindListBinding binding;

    // 운동종류 리스트
    private ArrayList<FitnessKinds> fitnessKindsList;

    // 회원정보
    private FriendInfoDto memberInfo;

    // FitnessViewModel
    private FitnessViewModel fitnessViewModel;

    // 나만의 운동 만들기 다이얼로그
    private Dialog customFitnessCreateDialog;

    // 운동기록 데이터 등록 시 넘어오는 데이터 처리
    private ActivityResultLauncher<Intent> startActivityResult;

    // 기록을 등록할 날짜
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
                            Intent intent = new Intent(FitnessKindListActivity.this, MemberHomeActivity.class);

                            /** FitnessFragment에 운동을 기록한 날짜를 전달.*/
                            String selectedDateYMD = (String)result.getData().getSerializableExtra("selectedDateYMD");

                            intent.putExtra("selectedDateYMD", selectedDateYMD);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                    }
                });

        super.onCreate(savedInstanceState);

        binding = ActivityFitnessKindListBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();

        setOnClickListener();
        addTextChangedListListener();
    }

    public void setOnClickListener(){
        // 뒤로가기 버튼
        binding.btnBack.setOnClickListener(this);
        // 추가버튼
        binding.btnAddFitnessRecord.setOnClickListener(this);
        // 나만의 운동만들기
        binding.btnCustomFitnessCreate.setOnClickListener(this);

    }
    public void addTextChangedListListener(){
        binding.searchFitness.addTextChangedListener(this);
    }

    // 초기화
    public void initialize(){
        Intent intent = getIntent();
        memberInfo = (FriendInfoDto) intent.getSerializableExtra("memberInfo");
        // 선택한 날짜
        selectedDateYMD = (String) intent.getSerializableExtra("selectedDateYMD");

        // 뷰모델 초기화
        fitnessViewModel = new ViewModelProvider(this).get(FitnessViewModel.class);

        // 운동종류리스트  가져오기
        getFitnessKindsList();

        String[] titleArr = new String[]{"즐겨찾기","전체","가슴","어깨"
                                        ,"등","복근","삼두"
                                        ,"이두","엉덩이","전신"
                                        ,"코어"};

        TabLayout tabLayout = binding.tabLayout;
        ViewPager2 viewPager2 = binding.viewPager;

        FitnessKindsListVPAdapter adapter = new FitnessKindsListVPAdapter(this, memberInfo);

        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> tab.setText(titleArr[position])).attach();

        fitnessViewModel.getSelectedFitnessKindsList().observe(this, list -> {
            handleRecordBtn(list.size());
        });
    }


    // recordCnt값에 따라 버튼을 생성, 변경, 삭제한다.
    public void handleRecordBtn(int recordCnt){
        if(recordCnt == 0) { // 버튼 삭제
            binding.btnAddFitnessRecord.setVisibility(View.GONE);
        } else if(recordCnt == 1) { // 버튼 생성
            binding.btnAddFitnessRecord.setText(recordCnt + "개 추가");
            binding.btnAddFitnessRecord.setVisibility(View.VISIBLE);
        } else { // 버튼명 변경
            binding.btnAddFitnessRecord.setText(recordCnt + "개 추가");
        }
    }

    /** 다이얼로그 생성 */
    public void createDialog(){
        customFitnessCreateDialog = new Dialog(this);
        customFitnessCreateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        customFitnessCreateDialog.setContentView(R.layout.dialog_custom_fitness);
        customFitnessCreateDialog.show();

        customFitnessCreateDialog.findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customFitnessCreateDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_back: // 뒤로가기
                onBackPressed();
                break;
            case R.id.btn_add_fitness_record: // 기록 추가하기
                // 운동기록 입력화면으로 이동
                Intent intent = new Intent(this, FitnessRegisterActivity.class);
                intent.putExtra("registeredFitnessList", fitnessViewModel.getSelectedFitnessKindsList().getValue());
                intent.putExtra("memberInfo", memberInfo);
                intent.putExtra("selectedDateYMD", selectedDateYMD);

                startActivityResult.launch(intent);
                break;
            case R.id.btn_custom_fitness_create: // 나만의 운동만들기
                createDialog();
                break;
        }
    }

    /** 운동종류 가져오기 - 기본, 커스텀*/
    public void getFitnessKindsList(){
        Retrofit retrofit= RetrofitInstance.getRetroClient();
        FitnessService service = retrofit.create(FitnessService.class);

        // http request 객체 생성
        Call<ArrayList<FitnessKinds>> call = service.getFitnessKindsList();
        //Call<String> call = service.getFitnessKindsList2();

        new GetFitnessKindsListCall().execute(call);
    }

    /** 나만의 운동 만들기*/
    public void registerCustomFitness(){
        Retrofit retrofit= RetrofitInstance.getRetroClient();
        FitnessService service = retrofit.create(FitnessService.class);

        // 커스텀 운동
        FitnessKinds customFitness = new FitnessKinds();

        // 데이터 추가

        // http request 객체 생성
        Call<String> call = service.registerCustomFitness(customFitness);

        new RegisterCustomFitness(customFitness).execute(call);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // 입력 시 마다 viewModel업데이트
        Log.d("여기 오나?", "111");

        fitnessViewModel.getSearchText().setValue(s);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /** 운동종류 가져오기*/
    private class GetFitnessKindsListCall extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<ArrayList<FitnessKinds>> response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPreExecute", "11");
        }

        @Override
        protected String  doInBackground(Call[] params) {
            try {
                Call<ArrayList<FitnessKinds>> call = params[0];
                response = call.execute();

                if(response.body() != null){
                    fitnessKindsList = response.body();
                }

                return response.body().toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // viewModel에 데이터 저장
            fitnessViewModel.getFitnessKindsList().setValue(fitnessKindsList);
        }
    }

    /** 나만의 운동 만들기 */
    private class RegisterCustomFitness extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<String> response;
        private FitnessKinds customFitness;

        public RegisterCustomFitness(FitnessKinds customFitness) {
            this.customFitness = customFitness;
        }

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

                Log.d("에러", response.body());

                return response.body().toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // viewModel에 커스텀 운동 추가!
            ArrayList<FitnessKinds> currentList = fitnessViewModel.getFitnessKindsList().getValue();
            currentList.add(customFitness);
            fitnessViewModel.getFitnessKindsList().setValue(currentList);
            customFitnessCreateDialog.dismiss();
        }
    }
}