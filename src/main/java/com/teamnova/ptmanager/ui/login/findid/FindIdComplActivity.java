package com.teamnova.ptmanager.ui.login.findid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityFindIdComplBinding;
import com.teamnova.ptmanager.ui.login.LoginActivity;
import com.teamnova.ptmanager.viewmodel.login.LoginViewModel;

public class FindIdComplActivity extends AppCompatActivity {
    private ActivityFindIdComplBinding binding;
    private String name;
    private String phoneNum;
    private String loginId;

    // loginViewModel
    private LoginViewModel loginViewModel;

    // Retrofit 통신의 결과를 전달하는 핸들러
    private Handler loginHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다ㅣ.
        binding = ActivityFindIdComplBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();



    }

    // 초기화
    public void initialize(){
        Intent intent = getIntent();
        // 뷰모델 초기화
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // 아이디, 전화번호
        name = intent.getStringExtra("name");
        phoneNum = intent.getStringExtra("phoneNum");

        // Retrofit 통신 핸들러
        loginHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                // loginId
                if(msg.what == 0){
                    loginId = ((String) msg.obj).replace("\"","");

                    Log.d("로그인 id: ", loginId);

                    binding.loginId.setText(loginId);
                }
            }
        };

        loginViewModel.getLoginId(loginHandler, name, phoneNum);

        // 로그인하러가기 버튼 클릭 시 로그인 화면으로 이동
        binding.btnRegisterComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FindIdComplActivity.this, LoginActivity.class);

                intent.putExtra("loginId", loginId);

                startActivity(intent);
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
    }
}