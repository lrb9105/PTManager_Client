package com.teamnova.ptmanager.ui.record.meal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityDetailMealBinding;
import com.teamnova.ptmanager.model.changehistory.inbody.InBody;
import com.teamnova.ptmanager.model.record.meal.Meal;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.changehistory.inbody.InBodyService;
import com.teamnova.ptmanager.network.record.meal.MealService;
import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodyCompareHistoryActivity;
import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodySelectActivity;
import com.teamnova.ptmanager.ui.changehistory.inbody.InBodyModifyActivity;
import com.teamnova.ptmanager.ui.home.member.MemberHomeActivity;
import com.teamnova.ptmanager.util.DialogUtil;
import com.teamnova.ptmanager.util.GetDate;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;

/** 식사 정보 상세보기 */
public class MealDetailActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityDetailMealBinding binding;

    /** 데이터 */
    private String dateToServer;
    private Meal meal;
    private FriendInfoDto memberInfo; //회원정보

    /** 리사이클러뷰의 위치값*/
    private int parentRecPosition;
    private int childRecPosition;

    /** 식사기록 수정*/
    private ActivityResultLauncher<Intent> startActivityResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            meal = (Meal)result.getData().getSerializableExtra("meal");
                            FriendInfoDto memberInfo = (FriendInfoDto)result.getData().getSerializableExtra("memberInfo");

                            setMealInfo(meal, memberInfo);
                        }
                    }
                });

        super.onCreate(savedInstanceState);

        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다.
        binding = ActivityDetailMealBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();
    }

    // 초기화
    public void initialize(){
        Intent intent = getIntent();

        memberInfo = (FriendInfoDto)intent.getSerializableExtra("memberInfo");

        // 등록 후 넘어왔다면
        if(intent.getStringExtra("mealId") == null){
            meal = (Meal)intent.getSerializableExtra("meal");
            // 값 세팅
            setMealInfo(meal,memberInfo);

            // 식사기록 모아보기에서 넘어왔다면
            parentRecPosition = intent.getIntExtra("parentRecPosition",99999);
            childRecPosition = intent.getIntExtra("childRecPosition",99999);
        } else{ // 홈화면에서 넘어왔다면
            Retrofit retrofit= RetrofitInstance.getRetroClient();
            MealService service = retrofit.create(MealService.class);
            Call<Meal> call = service.getMealInfoById(intent.getStringExtra("mealId"));
            new GetMealInfoCall().execute(call);
        }


        /** onClickListener 등록 */
        setOnclickListener();
    }

    /** 식사정보를 세팅한다. */
    public void setMealInfo(Meal meal, FriendInfoDto memberInfo){
        /** 값 세팅*/
        // 식사일
        String dayYMDWithKorean = GetDate.getDateWithYMD(meal.getMealDate());
        String day = GetDate.getDayOfWeek(meal.getMealDate());
        String mealType = "";

        switch (meal.getMealType()){
            case 0:
                mealType = "아침";
                break;
            case 1:
                mealType = "간식(아침-점심)";
                break;
            case 2:
                mealType = "점심";
                break;
            case 3:
                mealType = "간식(점심-저녁)";
                break;
            case 4:
                mealType = "저녁";
                break;
            case 5:
                mealType = "야식";
                break;
        }

        binding.mealDateInput.setText(dayYMDWithKorean + "(" + day + ") - " + mealType);

        // 식사사진
        if(meal.getImgList() != null && meal.getImgList().size() > 0){
            Glide.with(this).load("http://15.165.144.216" +  meal.getImgList().get(0)).into(binding.mealImg);
        }else if(meal.getSavePath() != null){
            Glide.with(this).load("http://15.165.144.216" +  meal.getSavePath()).into(binding.mealImg);
        }else{
            binding.mealImg.setVisibility(View.GONE);
        }

        // 식사기록
        binding.mealContentsInput.setText(meal.getMealContents());

        /*//댓글 사진
        if(memberInfo.getProfileId() != null && !memberInfo.getProfileId().equals("")){
            Glide.with(this).load("http://15.165.144.216" +  memberInfo.getProfileId()).into(binding.userProfile);
        }else{
            binding.userProfile.setImageDrawable(getApplicationContext().getDrawable(R.drawable.background_date));
        }*/
    }

    // 온클릭리스너 등록
    public void setOnclickListener(){
        // 수정화면으로 이동
        binding.btnModifyMeal.setOnClickListener(this);

        // 식사기록 삭제
        binding.btnDeleteMeal.setOnClickListener(this);

        binding.btnBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        Intent intent;

        switch(v.getId()){
            case R.id.btn_modify_meal: // 수정
                /** 식사기록 수정화면으로 이동 */
                intent = new Intent(this, MealModifyActivity.class);

                intent.putExtra("meal", meal);
                intent.putExtra("memberInfo", memberInfo);

                startActivityResult.launch(intent);
                break;
            case R.id.btn_delete_meal: // 삭제
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage("식사기록을 삭제하시겠습니까?");

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        /** 식사기록 데이터 삭제*/
                        //식사기록 삭제
                        Retrofit retrofit= RetrofitInstance.getRetroClient();
                        MealService service = retrofit.create(MealService.class);

                        // http request 객체 생성
                        Call<String> call = service.deleteMealInfo(meal.getMealRecordId());

                        new DeleteMealInfoCall().execute(call);
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
            case R.id.btn_back: // 뒤로가기
                onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = null;

        intent = new Intent(this, MealRegisterActivity.class);

        // 식사정보 정보리스트
        intent.putExtra("meal", meal);

        setResult(Activity.RESULT_OK, intent);
        finish();

        super.onBackPressed();
    }

    /** 식사기록 가져오기 */
    private class GetMealInfoCall extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<Meal> response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPreExecute", "11");
        }

        @Override
        protected String  doInBackground(Call[] params) {
            try {
                Call<Meal> call = params[0];
                response = call.execute();
                meal = response.body();
                return response.body().toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            setMealInfo(meal, memberInfo);
        }
    }

    /** 식사기록 삭제 */
    public class DeleteMealInfoCall extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<String> response;

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

                return response.body().toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.contains("DELETE FAIL")){
                // 눈바디정보 삭제를 실패했다면 - "DELETE FAIL"반환
                new DialogUtil().msgDialog(MealDetailActivity.this,"식사기록 삭제에 실패했습니다.");

            } else if(result.contains("DELETE SUCCESS")){
                Log.d("삭제성공!","1111");
                Log.d("에러", response.body());

                Intent intent = new Intent(MealDetailActivity.this, MemberHomeActivity.class);

                // 삭제한 식사정보
                intent.putExtra("deletedMealInfo", meal);

                setResult(Activity.RESULT_OK, intent);
                finish();
            } else{
                Log.d("에러", result);
            }
        }
    }
}