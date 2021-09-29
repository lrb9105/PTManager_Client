package com.teamnova.ptmanager.ui.record.meal;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import com.bumptech.glide.Glide;
import com.roughike.swipeselector.SwipeItem;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityMealModifyBinding;
import com.teamnova.ptmanager.databinding.ActivityRegisterMealBinding;
import com.teamnova.ptmanager.model.changehistory.inbody.InBody;
import com.teamnova.ptmanager.model.record.meal.Meal;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.changehistory.inbody.InBodyService;
import com.teamnova.ptmanager.network.record.meal.MealService;
import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodySaveActivity;
import com.teamnova.ptmanager.ui.changehistory.inbody.InBodyModifyActivity;
import com.teamnova.ptmanager.ui.home.member.MemberHomeActivity;
import com.teamnova.ptmanager.util.GetDate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class MealModifyActivity extends AppCompatActivity implements View.OnClickListener{
    /** 식사사진 정보를 가져올 때 사용할 상수 */
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_ALBUM_PICK = 2;

    private ActivityMealModifyBinding binding;

    /** 데이터 */
    private String dateToServer;
    private Meal meal;
    private ArrayList<MultipartBody.Part> mealImgList;
    private SwipeItem selectedItem; // 식사종류
    private FriendInfoDto memberInfo; //회원정보
    private boolean isImgChanged;


    /*private String mCurrentPhotoPath;
    private ArrayList<Bitmap> mealImgList;*/
    
    /** 식사사진 선택 */
    private ActivityResultLauncher<Intent> startActivityResult;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** 식사사진 선택 */
        startActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            //  식사 데이터 가져오기
                            String filePath = result.getData().getStringExtra("eyeBodyPath");

                            // 서버로 전송하기 위한 MultipartData 만들기
                            Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            // 사진을 압축
                            selectedImage.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);

                            RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), byteArrayOutputStream.toByteArray());
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                            MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("mealFileImg[]", "JPEG_" + timeStamp + "_.jpg" ,requestBody);

                            if(mealImgList.size() > 0){
                                for(int i = 0; i < mealImgList.size(); i++){
                                    mealImgList.remove(i);
                                }
                            }

                            mealImgList.add(uploadFile);

                            Log.d("수정전", mealImgList.get(0).toString());

                            binding.selectedMeal.setImageBitmap(selectedImage);

                            isImgChanged = true;
                        }
                    }
                });
        super.onCreate(savedInstanceState);

        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다.
        binding = ActivityMealModifyBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();
    }

    // 초기화
    public void initialize(){
        /** 식사정보 받아오기*/
        Intent intent = getIntent();
        meal = (Meal)intent.getSerializableExtra("meal"); // 삭시기록
        memberInfo = (FriendInfoDto)getIntent().getSerializableExtra("memberInfo"); // 사용자 userId

        /** 이미지 리스트 초기화*/
        mealImgList = new ArrayList<>();

        /** onClickListener 등록 */
        setOnclickListener();
        setMealInfo();

        isImgChanged = false;
    }

    // 온클릭리스너 등록
    public void setOnclickListener(){
        binding.btnMealModify.setOnClickListener(this);
        binding.selectedMeal.setOnClickListener(this);
    }

    /** 식사정보 세팅 */
    public void setMealInfo(){
        /** 검사일시에 오늘날짜 넣어주기 */
        String day  = meal.getMealDate();
        String dayYMDWithKorean = GetDate.getDateWithYMD(day);
        String today = GetDate.getDayOfWeek(day);

        binding.inbodyCheckDateInput.setText(dayYMDWithKorean + "(" + today + ")");

        /** 식사종류 */
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

        binding.mealTypeInput.setText(mealType);

        /** 식사사진 */
        if(meal.getSavePath() != null){
            Glide.with(this).load("http://15.165.144.216" +  meal.getSavePath()).into(binding.selectedMeal);
        }else if(meal.getImgList() != null && meal.getImgList().size() > 0){
            Glide.with(this).load("http://15.165.144.216" +  meal.getImgList().get(0)).into(binding.selectedMeal);
        }

        /** 식사기록 */
        binding.mealContentsInput.setText(meal.getMealContents());
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_meal_modify: // 식사정보 수정
                /** 수정완료 버튼 클릭*/
                AlertDialog.Builder builder = new AlertDialog.Builder(MealModifyActivity.this);

                builder.setMessage("식사정보를 수정하시겠습니까?");

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        /** 식사정보 수정 */
                        Retrofit retrofit = RetrofitInstance.getRetroClient();
                        MealService service = retrofit.create(MealService.class);

                        RequestBody mealId = RequestBody.create(MediaType.parse("text/plain"), meal.getMealRecordId());
                        String isImgChangedStr = "" + isImgChanged;
                        RequestBody isImgChanged = RequestBody.create(MediaType.parse("text/plain"), isImgChangedStr);
                        RequestBody mealContents = RequestBody.create(MediaType.parse("text/plain"), binding.mealContentsInput.getText().toString());

                        // http request 객체 생성
                        Call<Meal> call = service.modifyMealInfo(mealId, mealContents, isImgChanged, mealImgList);
                        //Call<String> call = service.modifyMealInfo(mealId, mealContents, isImgChanged, mealImgList);

                        new ModifyMealCall().execute(call);
                    }
                }).setNegativeButton("취소",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            { }
                        }
                );

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                break;
            case R.id.selected_meal: // 출력된 사진선택
                startActivityResult.launch(new Intent(this, EyeBodySaveActivity.class));
                break;
        }
    }

    /** 식사정보 수정 */
    private class ModifyMealCall extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<Meal> response;
        //private retrofit2.Response<String> response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPreExecute", "11");
        }

        @Override
        protected String  doInBackground(Call[] params) {
            try {
                Call<Meal> call = params[0];
                //Call<String> call = params[0];

                response = call.execute();

                if(response.errorBody()!=null){
                    Log.d("에러바디:", response.errorBody().string());
                }
                Log.d("메시지", response.message());

                Log.d("에러코드", "" + response.code());

                meal = response.body();
                return response.body().toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // MealDetail화면으로 넘기기
            Intent intent = new Intent(MealModifyActivity.this, MealDetailActivity.class);
            // 식사기록 정보
            intent.putExtra("meal", meal);
            intent.putExtra("memberInfo", memberInfo);

            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }
}