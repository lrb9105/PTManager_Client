package com.teamnova.ptmanager.ui.login.findpw;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityFindPw3Binding;
import com.teamnova.ptmanager.databinding.ActivityFindPwBinding;
import com.teamnova.ptmanager.ui.login.LoginActivity;
import com.teamnova.ptmanager.util.Sha256;
import com.teamnova.ptmanager.viewmodel.login.LoginViewModel;

public class FindPw3Activity extends AppCompatActivity{
    private ActivityFindPw3Binding binding;

    // loginViewModel
    private LoginViewModel loginViewModel;

    // Retrofit 통신의 결과를 전달하는 핸들러
    private Handler loginHandler;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다ㅣ.
        binding = ActivityFindPw3Binding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();
    }

    /**
     *  1. 역할: 컴포넌트 초기화
     * */
    public void initialize(){
        intent = getIntent();

        // 뷰모델 초기화
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Retrofit 통신 핸들러
        loginHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what == 0){
                    String result = (String) msg.obj;
                    Log.d("비밀번호 초기화 결과: ", result);

                    // 로그인 아이디가 "null"이 아니라면 다음 액티비티로 이동
                    if(!result.contains("null")){
                        Log.d("로그인아이디:", result);

                        // 다이얼로그빌더
                        AlertDialog.Builder builder = new AlertDialog.Builder(FindPw3Activity.this);

                        builder.setMessage("비밀번호 초기화가 완료되었습니다.");

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                Intent intent = new Intent(FindPw3Activity.this, LoginActivity.class);
                                intent.putExtra("loginId", getIntent().getStringExtra("loginId"));
                                startActivity(intent);                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else{ //null 이라면 존재하지 않는다는 메시지 띄워주기

                    }
                }
            }
        };

        // 비밀번호 초기화 완료 버튼
        binding.btnRegisterComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 일치한다면 비밀번호 업데이트
                if (binding.pw.getText().toString().equals(binding.pwConfirm.getText().toString())) {
                    String loginId = intent.getStringExtra("loginId");

                    loginViewModel.updatePw(loginHandler, loginId, Sha256.encrypt(binding.pw.getText().toString()));
                } else {
                    binding.textViewRequirement2.setVisibility(View.VISIBLE);
                }
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