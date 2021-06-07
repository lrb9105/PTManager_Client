package com.teamnova.ptmanager.ui.register;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityRegisterBinding;
import com.teamnova.ptmanager.databinding.ActivitySelectProfileBinding;
import com.teamnova.ptmanager.model.userInfo.UserInfoDto;
import com.teamnova.ptmanager.viewmodel.login.LoginViewModel;
import com.teamnova.ptmanager.viewmodel.register.RegisterViewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SelectProfile extends AppCompatActivity implements View.OnClickListener{
    // 다른 액티비티에 보낼 reqeuestCode
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_ALBUM_PICK = 2;

    // 해당 액티비티에서 사용할 Binder
    private ActivitySelectProfileBinding binding;

    // 레지스터 뷰모델
    private RegisterViewModel registerViewModel;

    //
    private String mCurrentPhotoPath;

    // 사용자 정보
    private UserInfoDto userInfoDto;

    // 가져온 이미지 비트맵
    private Bitmap img;

    // 프로필 이미지 파일
    private File profileImgFile;

    // retrofit통신의 결과를 전달하는 핸들러
    private Handler retrofitResultHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다ㅣ.
        binding = ActivitySelectProfileBinding.inflate(getLayoutInflater());

        // 뷰모델 초기화
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();
    }


    /**
     *  1. 역할: 컴포넌트 초기화
     * */
    public void initialize(){
        // 리스너 등록
        binding.userProfile.setOnClickListener(this);
        binding.btnProfileSettingCompl.setOnClickListener(this);

        // 인텐트에서 유저정보 가져오기
        userInfoDto = (UserInfoDto) getIntent().getSerializableExtra("userInfo");

        // Retrofit 통신 핸들러
        retrofitResultHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                // 이미지 전송결과
                if(msg.what == 0){
                    Toast.makeText(SelectProfile.this, (String)msg.obj,Toast.LENGTH_SHORT);
                }
            }
        };
    }

    /**
     * 클릭이벤트 처리
     * */
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.user_profile: // 프로필 사진 설정
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("프로필 설정");

                Log.d("프로필 설정","111");

                Toast.makeText(this, "프로필 설정",Toast.LENGTH_SHORT);

                builder.setItems(R.array.photoOrImageOrRemove, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int pos)
                    {
                        if(pos == 0){ //직접촬영
                            dispatchTakePictureIntent();
                        } else { //갤러리에서 가져오기
                            doTakeMultiAlbumAction();
                        }
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return;
            case R.id.btn_profile_setting_compl: // 사진 설정 완료
                Toast.makeText(this, "프로필 설정",Toast.LENGTH_SHORT);

                // 프로필 이미지를 가져왔다면
                if(profileImgFile != null){
                    registerViewModel.transferImgToServer(retrofitResultHandler, profileImgFile);
                }
                break;
            default:
                break;
        }
    }

    // 직접촬영
    private void dispatchTakePictureIntent() {
        // 특정 권한에 대한 허용을 했는지 여부
        int permissionCheck = ContextCompat.checkSelfPermission(SelectProfile.this, Manifest.permission.CAMERA);

        if(permissionCheck == PackageManager.PERMISSION_DENIED){ //카메라 권한 없음
            ActivityCompat.requestPermissions(SelectProfile.this,new String[]{Manifest.permission.CAMERA},0);
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
    }

    private void doTakeMultiAlbumAction(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        // 프로필 사진 고를 땐 여러장 못고르도록.
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        startActivityForResult(intent,REQUEST_ALBUM_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (resultCode == RESULT_OK) {
                profileImgFile = new File(mCurrentPhotoPath);
                Bitmap bitmap;
                if (Build.VERSION.SDK_INT >= 29) {
                    ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), Uri.fromFile(profileImgFile));
                    try {
                        bitmap = ImageDecoder.decodeBitmap(source);
                        if (bitmap != null) {
                            binding.userProfile.setImageBitmap(bitmap);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        } else if(requestCode == REQUEST_ALBUM_PICK && resultCode == RESULT_OK){
            ClipData clipData = data.getClipData();

            // 선택한 사진이 한장이라면 clipData존재 X
            if(clipData == null){
                try {
                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    img = BitmapFactory.decodeStream(in);

                    // 이미지 표시
                    binding.userProfile.setImageBitmap(img);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                InputStream in = null;
                ArrayList<Bitmap> imgList = new ArrayList<>();

                for(int i = 0; i < clipData.getItemCount(); i++){
                    Uri uri =  clipData.getItemAt(i).getUri();
                    try {
                        in = getContentResolver().openInputStream(uri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    img = BitmapFactory.decodeStream(in);
                    imgList.add(img);
                    binding.userProfile.setImageBitmap(img);
                }
            }
        }
    }

    // 촬영한 사진을 저장할 파일을 생성한다.
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile( imageFileName, ".jpg", storageDir );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}