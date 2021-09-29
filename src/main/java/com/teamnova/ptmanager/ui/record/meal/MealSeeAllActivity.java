package com.teamnova.ptmanager.ui.record.meal;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityEyeBodyChangeHistoryBinding;
import com.teamnova.ptmanager.databinding.ActivityMealSeeAllBinding;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBody;
import com.teamnova.ptmanager.model.changehistory.eyebody.EyeBodyHistoryInfo;
import com.teamnova.ptmanager.model.record.meal.Meal;
import com.teamnova.ptmanager.model.record.meal.MealDateWithCount;
import com.teamnova.ptmanager.model.record.meal.MealHistoryInfo;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.record.meal.MealService;
import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodyChangeHistoryActivity;
import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodySaveActivity;
import com.teamnova.ptmanager.ui.changehistory.eyebody.adapter.EyeBodyChangeHistoryAdapter;
import com.teamnova.ptmanager.ui.home.member.MemberHomeActivity;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.ui.record.meal.adapter.MealSeeAllAdapter;
import com.teamnova.ptmanager.viewmodel.changehistory.eyebody.EyeBodyViewModel;
import com.teamnova.ptmanager.viewmodel.friend.FriendViewModel;
import com.teamnova.ptmanager.viewmodel.record.MealViewModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

/** 식사기록 모아보기 화면*/
public class MealSeeAllActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityMealSeeAllBinding binding;

    // 식사기록 상세
    private ActivityResultLauncher<Intent> startActivityResult;

    // 회원 정보
    private FriendInfoDto memberInfo;
    
    // 핸들러
    private Handler resultHandler;

    // 리사이클러뷰
    private RecyclerView recyclerView;
    private MealSeeAllAdapter mealSeeAllAdapter;
    private RecyclerView.LayoutManager layoutManager;

    // 데이터 수정여부
    private boolean isModifiedMealInfo = false;

    // 데이터 삭제여부
    private boolean isDeletedMealInfo = false;

    // 추가한 식사기록 객체
    private Meal modifiedMealInfo;

    // 삭제한 식사기록 객체
    private Meal deletedMealInfo;

    // 식사기록 정보 리스트
    private ArrayList<Meal> mealList;

    // 식사기록(날짜별) 리스트
    private ArrayList<MealHistoryInfo> mealHistoryInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrofit 통신 핸들러
        resultHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                // 눈바디 저장 성공 성공
                if(msg.what == 0){/*
                    isAddedEyeBodyInfo = true;
                    addedEyeBody = (EyeBody)msg.obj;

                    *//**
                     * 저장한 눈바디 데이터 리스트에 저장
                     * 1. 저장한 데이터의 일자에 해당하는 데이터가 있는지 확인
                     * *//*
                    String eyeBodyDate = addedEyeBody.getCreDate();

                    *//**
                     * 1. 리스트는 시간순으로 가져온다(최근시간이 먼저)
                     * 2. 눈바디데이터는 오늘 날짜로만 등록할 수 있다.
                     * 3. 따라서 리스트의 첫번째 데이터의 날짜가 오늘 날짜가 아니라면 저장한 눈바디 데이터가 오늘 저장한 최초의 데이터이다.
                     * *//*
                    // 동RD일하다면 이미 존재
                    if(eyeBodyHistoryList != null && eyeBodyHistoryList.size() > 0 && eyeBodyDate.equals(eyeBodyHistoryList.get(0).getEyeBodyDate())){
                        *//**
                         * 오늘 날짜로 이미 저장된 데이터가 있다면 리스트의 첫번째 데이터의 ArrayList<EyeBody>의 첫번째 리스트에
                         * 새로 저장한 EyeBody데이터를 넣어줘야 한다.
                         * *//*
                        EyeBodyHistoryInfo tempEyeBodyHistoryInfo = eyeBodyHistoryList.get(0);
                        ArrayList<EyeBody> tempEyeBodyList = tempEyeBodyHistoryInfo.getEyeBodyListByDay();
                        tempEyeBodyList.add(0,addedEyeBody);

                        tempEyeBodyHistoryInfo.setEyeBodyListByDay(tempEyeBodyList);
                        eyeBodyHistoryList.set(0, tempEyeBodyHistoryInfo);

                        // 리사이클러뷰에 수정
                        eyeBodyChangeHistoryAdapter.modifyEyeBodyHistory(0,tempEyeBodyHistoryInfo);
                    } else{
                        *//**
                         * 오늘 날짜로 저장한 데이터가 없다면 리사이클러뷰에 새로운 EyeBodyHistoryInfo데이터를 만들어서
                         * 리사이클러뷰에 추가해야 한다.
                         * *//*
                        ArrayList<EyeBody> tempEyeBodyList = new ArrayList<>();
                        tempEyeBodyList.add(addedEyeBody);
                        EyeBodyHistoryInfo tempEyeBodyHistoryInfo = new EyeBodyHistoryInfo(eyeBodyDate, tempEyeBodyList);

                        // 데이터가 하나도 없다면
                        if(eyeBodyHistoryList.size() == 0){
                            // 리사이클러뷰 세팅
                            recyclerView = binding.recyclerviewEyebodyChangeHistory;
                            layoutManager = new LinearLayoutManager(EyeBodyChangeHistoryActivity.this);
                            recyclerView.setLayoutManager(layoutManager);

                            // 데이터 가져와서 adapter에 넘겨 줌
                            eyeBodyChangeHistoryAdapter = new EyeBodyChangeHistoryAdapter(eyeBodyHistoryList, EyeBodyChangeHistoryActivity.this,0,null,startActivityResult2);

                            recyclerView.setAdapter(eyeBodyChangeHistoryAdapter);
                        }else{
                            // 리사이클러뷰에 추가
                            eyeBodyChangeHistoryAdapter.addEyeBodyHistory(tempEyeBodyHistoryInfo);
                            recyclerView.scrollToPosition(0);
                        }


                        eyeBodyHistoryList.add(0, tempEyeBodyHistoryInfo);
                    }

                    Log.d("눈바디 데이터 저장 성공: ", addedEyeBody.getSavePath());*/
                }
            }
        };

        /** 식사 상세화면에서 넘어 왔을 때*/
        startActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            /*// isDeletedMealInfo = true
                            isDeletedMealInfo = true;

                            *//** 부모, 자식 리사이클러뷰의 위치값 받아옴  *//*
                            int parentPosition = result.getData().getIntExtra("parentPosition", 99999);
                            int childPosition = result.getData().getIntExtra("childPosition", 99999);

                            Log.d("부모리사위치: ", "" + parentPosition);
                            Log.d("자식리사위치: ", "" + childPosition);

                            *//** 리사이클러뷰 수정 혹은 삭제*//*
                            // 부모 객체
                            EyeBodyHistoryInfo eyeBodyHistoryInfo = mealSeeAllAdapter.getEyebodyInfoist().get(parentPosition);

                            // 해당 눈바디기록 정보에 있는 눈바디 객체리스트
                            ArrayList<EyeBody> eyeBodyList = eyeBodyHistoryInfo.getEyeBodyListByDay();

                            deletedEyeBody = eyeBodyList.get(childPosition);

                            // 1. 자식리사이클러뷰의 크기를 확인한다.
                            if(eyeBodyList.size() == 1){ // 삭제전 사이즈가 1이면 삭제하면 0이기 때문에 해당 리사이클러뷰를 삭제한다.
                                eyeBodyChangeHistoryAdapter.deleteEyeBodyHistory(parentPosition);
                            } else { // 사이즈가 1보다 크다면 리사이클러뷰를 수정한다.
                                // 자식 리사이클러뷰를 삭제한다.
                                eyeBodyList.remove(childPosition);
                                eyeBodyHistoryInfo.setEyeBodyListByDay(eyeBodyList);
                                // 수정한 눈바디기록정보로 adapter를 다시 세팅한다.
                                eyeBodyChangeHistoryAdapter.modifyEyeBodyHistory(parentPosition, eyeBodyHistoryInfo);
                            }*/
                        }
                    }
                });

        binding = ActivityMealSeeAllBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        super.onCreate(savedInstanceState);

        initialize();

        setOnClickListener();
    }

    public void setOnClickListener(){

    }

    // 초기화
    public void initialize(){
        /** 회원 정보*/
        Intent intent = getIntent();
        memberInfo = (FriendInfoDto) intent.getSerializableExtra("memberInfo");
        Log.d("memberInfo", memberInfo.getUserId());

        /** 리사이클러뷰 */
        recyclerView = binding.recyclerviewMealHistory;

        /** 전체식사기록 데이터 가져오기*/
        Retrofit retrofit= RetrofitInstance.getRetroClient();
        MealService service = retrofit.create(MealService.class);

        Call<ArrayList<Meal>> call = service.getMealListAll(memberInfo.getUserId());

        new GetMealListCall().execute(call);

        // 트레이너라면 버튼 비활성화
        /*if(TrainerHomeActivity.staticLoginUserInfo != null){
            binding.btnAddEyebody.setVisibility(View.GONE);
        }

        *//** 날짜별 눈바디 변화 정보 가공*//*
        // 눈바디 정보리스트
        eyeBodyList = (ArrayList<EyeBody>)getIntent().getSerializableExtra("eyeBodyList");

        int listIndex = 0;
        // 눈바디 변화기록리스트로 가공
        eyeBodyHistoryList = new ArrayList<>();

        if(eyeBodyList != null && eyeBodyList.size() > 0){
            for(int i = 0; i < eyeBodyList.size(); i++){
                EyeBody e = eyeBodyList.get(i);

                if(i > 0){ //i > 0이라면
                    String previousDate = eyeBodyList.get(i-1).getCreDate();

                    if(e.getCreDate().equals(previousDate)){ //이전 데이터와 날짜가 동일하다면
                        EyeBodyHistoryInfo tempHistoryInfo = eyeBodyHistoryList.get(listIndex);
                        ArrayList<EyeBody> tempList = tempHistoryInfo.getEyeBodyListByDay();
                        tempList.add(e);
                        tempHistoryInfo.setEyeBodyListByDay(tempList);
                        eyeBodyHistoryList.set(listIndex, tempHistoryInfo);
                    }else { // 이전데이터와 날짜가 동일하지 않다면
                        ArrayList<EyeBody> tempList = new ArrayList<>();
                        String date = e.getCreDate();
                        tempList.add(e);
                        EyeBodyHistoryInfo eyeBodyHistoryInfo = new EyeBodyHistoryInfo(date, tempList);
                        eyeBodyHistoryList.add(eyeBodyHistoryInfo);
                        listIndex++;
                    }
                } else { // i == 0이라면
                    ArrayList<EyeBody> tempList = new ArrayList<>();
                    String date = e.getCreDate();
                    tempList.add(e);
                    EyeBodyHistoryInfo eyeBodyHistoryInfo = new EyeBodyHistoryInfo(date, tempList);
                    eyeBodyHistoryList.add(eyeBodyHistoryInfo);
                }
            }

            // 리사이클러뷰 세팅
            recyclerView = binding.recyclerviewEyebodyChangeHistory;
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            // 데이터 가져와서 adapter에 넘겨 줌
            eyeBodyChangeHistoryAdapter = new EyeBodyChangeHistoryAdapter(eyeBodyHistoryList, this,0,null,startActivityResult2);

            recyclerView.setAdapter(eyeBodyChangeHistoryAdapter);


            // 출력테스트
            *//*for(EyeBodyHistoryInfo ehInfo : eyeBodyHistoryList){
                for(EyeBody e : ehInfo.getEyeBodyListByDay()){
                    Log.d(ehInfo.getEyeBodyDate() + "정보:", e.getEyeBodyChangeId() + " - " + e.getSavePath());
                }
            }*//*
        }


        // 회원정보
        memberInfo = (FriendInfoDto) getIntent().getSerializableExtra("memberInfo");

        // 눈바디 viewModel
        eyeBodyViewModel = new ViewModelProvider(this).get(EyeBodyViewModel.class);*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add_eyebody:
                //startActivityResult.launch(new Intent(EyeBodyChangeHistoryActivity.this, EyeBodySaveActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        // 눈바디 정보를 추가했다면
        /*if(isAddedEyeBodyInfo){
            Intent intent = null;

            intent = new Intent(this, MemberHomeActivity.class);

            // 눈바디 정보리스트
            intent.putExtra("addedEyeBody", addedEyeBody);

            setResult(Activity.RESULT_OK, intent);
            finish();
        } else if(isDeletedMealInfo){ // 눈바디 삭제
            Intent intent = null;

            intent = new Intent(this, MemberHomeActivity.class);

            // 눈바디 정보리스트
            intent.putExtra("removedEyeBody", deletedEyeBody);

            setResult(Activity.RESULT_OK, intent);
            finish();
        }*/

        super.onBackPressed();
    }

    /** 식사 정보리스트 가져오기 */
    public class GetMealListCall extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<ArrayList<Meal>> response;
        //private retrofit2.Response<String> response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPreExecute", "11");
        }

        @Override
        protected String  doInBackground(Call[] params) {
            try {
                Call<ArrayList<Meal>> call = params[0];
                //Call<String> call = params[0];
                response = call.execute();

                if(response.body() != null){
                    mealList = response.body();
                }

                return response.body().toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // 결과값을 가져 왔다면 ->  db에서 아무런 데이터를 조회해오지 못하면 "[]"값을 가져온다.
            if(!result.equals("[]")){
                if(mealList != null && mealList.size() > 0) {
                    mealHistoryInfoList = new ArrayList<>();

                    int listIndex = 0;

                    for (int i = 0; i < mealList.size(); i++) {
                        Meal m = mealList.get(i);

                        if (i > 0) { //i > 0이라면
                            String previousDate = mealList.get(i - 1).getMealDate();

                            if (m.getMealDate().equals(previousDate)) { //이전 데이터와 날짜가 동일하다면
                                MealHistoryInfo tempHistoryInfo = mealHistoryInfoList.get(listIndex);
                                ArrayList<Meal> tempList = tempHistoryInfo.getMealListByDay();
                                tempList.add(m);
                                tempHistoryInfo.setMealListByDay(tempList);
                                mealHistoryInfoList.set(listIndex, tempHistoryInfo);

                            } else { // 이전데이터와 날짜가 동일하지 않다면
                                ArrayList<Meal> tempList = new ArrayList<>();
                                String date = m.getMealDate();
                                tempList.add(m);
                                MealHistoryInfo mealHistoryInfo = new MealHistoryInfo(date, tempList);
                                mealHistoryInfoList.add(mealHistoryInfo);

                                listIndex++;
                            }
                        } else { // i == 0이라면
                            ArrayList<Meal> tempList = new ArrayList<>();
                            String date = m.getMealDate();

                            tempList.add(m);
                            MealHistoryInfo mealHistoryInfo = new MealHistoryInfo(date, tempList);
                            mealHistoryInfoList.add(mealHistoryInfo);
                        }
                    }

                    mealSeeAllAdapter = new MealSeeAllAdapter(mealHistoryInfoList, MealSeeAllActivity.this,startActivityResult, memberInfo);
                    recyclerView.setAdapter(mealSeeAllAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(MealSeeAllActivity.this));
                }
            }

            Log.d("사이즈: ", "" + mealHistoryInfoList.size());
        }
    }
}