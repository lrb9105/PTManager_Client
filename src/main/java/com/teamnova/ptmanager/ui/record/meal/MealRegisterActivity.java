package com.teamnova.ptmanager.ui.record.meal;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.roughike.swipeselector.SwipeItem;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityRegisterMealBinding;
import com.teamnova.ptmanager.model.record.meal.Meal;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.record.meal.MealService;
import com.teamnova.ptmanager.ui.changehistory.eyebody.EyeBodySaveActivity;
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

/** 식사 정보 저장 */
public class MealRegisterActivity extends AppCompatActivity implements View.OnClickListener{
    /** 식사사진 정보를 가져올 때 사용할 상수 */
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_ALBUM_PICK = 2;

    private ActivityRegisterMealBinding binding;

    /** 데이터 */
    private String dateToServer;
    private Meal meal;
    private ArrayList<MultipartBody.Part> mealImgList;
    private SwipeItem selectedItem; // 식사종류
    private FriendInfoDto memberInfo; //회원정보

    /*private String mCurrentPhotoPath;
    private ArrayList<Bitmap> mealImgList;*/

    // 식사 데이터 등록
    private ActivityResultLauncher<Intent> startActivityResult3;

    // 식사정보 상세화면으로 이동
    private ActivityResultLauncher<Intent> startActivityResult1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startActivityResult1 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // 데이터 넘기기
                            Intent intent = new Intent(MealRegisterActivity.this,MemberHomeActivity.class);
                            Meal meal = (Meal)result.getData().getSerializableExtra("meal");

                            Log.d("있나?", meal.getMealDate());

                            intent.putExtra("meal",meal);

                            setResult(Activity.RESULT_OK, intent);
                            finish();

                        }
                    }
                });

        startActivityResult3 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            //  눈바디 데이터 가져오기
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

                            binding.selectMeal.setVisibility(View.GONE);
                            binding.selectedMeal.setVisibility(View.VISIBLE);
                            binding.selectedMeal.setImageBitmap(selectedImage);
                        }
                    }
                });
        super.onCreate(savedInstanceState);

        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다.
        binding = ActivityRegisterMealBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();
    }

    // 초기화
    public void initialize(){
        /** 이미지 리스트 초기화*/
        mealImgList = new ArrayList<>();

        /** 검사일시에 오늘날짜 넣어주기 */
        String todayYMD = GetDate.getTodayDate();
        String todayYMDWithKorean = GetDate.getDateWithYMD(todayYMD);
        String today = GetDate.getDayOfWeek(todayYMD);

        binding.inbodyCheckDateInput.setText(todayYMDWithKorean + "(" + today + ")");
        // 서버로 보낼 날짜 정보
        dateToServer = todayYMD;

        /** 식사종류 세팅*/
        binding.swipeMealTypeSelector.setItems(
                new SwipeItem(0, "아침", null),
                new SwipeItem(1, "간식(아침-점심)", null),
                new SwipeItem(2, "점심", null),
                new SwipeItem(3, "간식(점심-저녁)", null),
                new SwipeItem(4, "저녁", null),
                new SwipeItem(5, "야식", null)
        );

        // The value is the first argument provided when creating the SwipeItem.
        //int value = (Integer) selectedItem.value;

        //Log.d("선택된 아이템: ", "" + value);

        /** onClickListener 등록 */
        setOnclickListener();
    }

    // 온클릭리스너 등록
    public void setOnclickListener(){
        binding.inbodyCheckDateInput.setOnClickListener(this);
        binding.btnMealRegister.setOnClickListener(this);
        binding.selectMeal.setOnClickListener(this);
        binding.selectedMeal.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        Intent intent;

        switch(v.getId()){
            case  R.id.btn_meal_register: // 식사정보 등록
                //meal = new Meal();
                /** 입력한 데이터 셋팅 */

                /** 식사정보 등록 */
                Retrofit retrofit= RetrofitInstance.getRetroClient();
                MealService service = retrofit.create(MealService.class);

                // 사용자 userId
                memberInfo = (FriendInfoDto)getIntent().getSerializableExtra("memberInfo");

                String userId = memberInfo.getUserId();
                selectedItem = binding.swipeMealTypeSelector.getSelectedItem();

                Log.d("여기로 안들어오나?","222");

                RequestBody userIdReq = RequestBody.create(MediaType.parse("text/plain"),userId);
                RequestBody mealDate = RequestBody.create(MediaType.parse("text/plain"),dateToServer);
                RequestBody mealType = RequestBody.create(MediaType.parse("text/plain"), "" +(int)selectedItem.value);
                RequestBody mealContents = RequestBody.create(MediaType.parse("text/plain"),binding.mealContents.getText().toString());

                // http request 객체 생성
                Call<Meal> call = service.registerMealInfo(userIdReq,mealDate, mealType, mealContents, mealImgList);
                //Call<String> call = service.registerMealInfo(userIdReq,mealDate, mealType, mealContents, mealImgList);

                new RegisterMealCall().execute(call);

                break;
            case R.id.inbody_check_date_input: //식사날짜 일시
                DatePickerDialog dialog = new DatePickerDialog(this);

                // 식사날짜 설정 완료 시
                dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateToServer = year + "" + (month <10 ? "0"+(month+1) : (month+1)) + (dayOfMonth <10 ? "0" + dayOfMonth : dayOfMonth);

                        String todayYMDWithKorean = GetDate.getDateWithYMD(dateToServer);
                        String today = GetDate.getDayOfWeek(dateToServer);

                        // 화면에 출력
                        binding.inbodyCheckDateInput.setText(todayYMDWithKorean + "(" + today + ")");
                    }
                });

               dialog.show();
                break;

            case R.id.select_meal: // 식사사진 선택
                startActivityResult3.launch(new Intent(this, EyeBodySaveActivity.class));
                binding.selectedMeal.setVisibility(View.GONE);
                break;
            case R.id.selected_meal: // 출력된 사진선택
                startActivityResult3.launch(new Intent(this, EyeBodySaveActivity.class));
                break;
            default:
                break;
        }
    }

    /** 식사정보 등록 */
    private class RegisterMealCall extends AsyncTask<Call, Void, String> {
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
            //Log.d("가져오냐?",response.body());
            Log.d("식사기록 잘가져오냐?", "" + meal.getImgList().size());
            // MealDetail화면으로 넘기기
            Intent intent = new Intent(MealRegisterActivity.this, MealDetailActivity.class);
            // 식사기록 정보
            intent.putExtra("meal", meal);
            intent.putExtra("memberInfo", memberInfo);

            startActivityResult1.launch(intent);

        }
    }

    /** 직접 촬영*/
    /*private void dispatchTakePictureIntent() {
        // 특정 권한에 대한 허용을 했는지 여부
        int permissionCheck = ContextCompat.checkSelfPermission(MealRegisterActivity.this, Manifest.permission.CAMERA);

        if(permissionCheck == PackageManager.PERMISSION_DENIED){ //카메라 권한 없음
            ActivityCompat.requestPermissions(MealRegisterActivity.this,new String[]{Manifest.permission.CAMERA},0);
        }else{ //카메라 권한 있음
            // 카메라 앱 액티비티를 여는 인텐트 생성
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) { }
                if(photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this, "com.teamnova.ptmanager.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }
    }*/

    /** 앨범에서 가져오기*/
    /*private void doTakeMultiAlbumAction(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        startActivityForResult(intent,REQUEST_ALBUM_PICK);
    }*/

    /** 사진을 찍거나 가져왔을 때 처리할 로직*/
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 직접 촬영
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (resultCode == RESULT_OK) {
                File mealImgInfoFile = new File(mCurrentPhotoPath);

                Bitmap mealImgInfo;

                if (Build.VERSION.SDK_INT > 27) {
                    ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), Uri.fromFile(mealImgInfoFile));
                    try {
                        Log.d("여기오나1", "11");
                        mealImgInfo = ImageDecoder.decodeBitmap(source);

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        // 사진을 압축
                        mealImgInfo.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);

                        ExifInterface exif = null;
                        try {
                            exif = new ExifInterface(mealImgInfoFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);

                        mealImgInfo = ImageUtil.rotateBitmap(mealImgInfo, orientation);

                        // 서버로 보낼 리스트에 추가
                        mealImgList.add(mealImgInfo);
                        binding.selectMeal.setImageBitmap(mealImgInfo);

                        // ViewPager에 추가

                        *//*RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), byteArrayOutputStream.toByteArray());
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                        uploadFile = MultipartBody.Part.createFormData("profileImg", "JPEG_" + timeStamp + "_.jpg" ,requestBody);*//*

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Log.d("여기오나2", "22");
                        mealImgInfoFile = new File(mCurrentPhotoPath);

                        mealImgInfo = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(mealImgInfoFile));

                        ExifInterface exif = null;
                        try {
                            exif = new ExifInterface(mealImgInfoFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);

                        mealImgInfo = ImageUtil.rotateBitmap(mealImgInfo, orientation);

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        // 사진을 압축
                        mealImgInfo.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);

                        // 서버로 보낼 리스트에 추가
                        mealImgList.add(mealImgInfo);
                        binding.selectMeal.setImageBitmap(mealImgInfo);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        } else if(requestCode == REQUEST_ALBUM_PICK && resultCode == RESULT_OK){
            // 갤러리에서 선택
            ClipData clipData = data.getClipData();

            // 선택한 사진이 한장이라면 clipData존재 X
            if(clipData == null){
                try {
                    // URI로부터 파일 만듬
                    File mealImgInfoFile = new File(data.getData().getPath());

                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap mealImgInfo = BitmapFactory.decodeStream(in);

                    ExifInterface exif = null;
                    try {
                        exif = new ExifInterface(mealImgInfoFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);

                    mealImgInfo = ImageUtil.rotateBitmap(mealImgInfo, orientation);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    // 사진을 압축
                    mealImgInfo.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);

                    // 리스트에 추가
                    mealImgList.add(mealImgInfo);
                    binding.selectMeal.setImageBitmap(mealImgInfo);

                    // viewPager에 추가
                    *//*RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), byteArrayOutputStream.toByteArray());

                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                    uploadFile = MultipartBody.Part.createFormData("profileImg", "JPEG_" + timeStamp + "_.jpg" ,requestBody);*//*                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else { //여러장을 선택했다면
                InputStream in = null;
                ArrayList<Bitmap> imgList = new ArrayList<>();

                for(int i = 0; i < clipData.getItemCount(); i++){
                    Uri uri =  clipData.getItemAt(i).getUri();
                    try {
                        in = getContentResolver().openInputStream(uri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap mealImgInfo = BitmapFactory.decodeStream(in);
                    mealImgList.add(mealImgInfo);
                    binding.selectMeal.setImageBitmap(mealImgInfo);
                }
                //viewPager에 추가
            }
        }
    }*/

    /** 촬영한 사진을 저장할 파일을 생성한다*/
    /*private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile( imageFileName, ".jpg", storageDir );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
*/
    /** 인바디 정보등록록 */
    /*private class RegisterInBodyCall extends AsyncTask<Call, Void, String> {
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
            Intent intent = new Intent(MealRegisterActivity.this, MemberHomeActivity.class);

            // 인바디 정보리스트
            intent.putExtra("inBody", inBody);

            setResult(Activity.RESULT_OK, intent);
            finish();

        }
    }*/
}