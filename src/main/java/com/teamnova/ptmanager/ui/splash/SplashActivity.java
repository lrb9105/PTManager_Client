package com.teamnova.ptmanager.ui.splash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;
import com.teamnova.ptmanager.MainActivity;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityLoginBinding;
import com.teamnova.ptmanager.databinding.ActivitySplashBinding;
import com.teamnova.ptmanager.ui.home.member.MemberHomeActivity;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.ui.login.LoginActivity;
import com.teamnova.ptmanager.viewmodel.login.LoginViewModel;

/**
* 스플래시 화면 - 자동로그인을 위해 필요하다.
* */
public class SplashActivity extends AppCompatActivity {
    // Binder
    private ActivitySplashBinding binding;

    // ViewModel
    private LoginViewModel loginViewModel;

    private String loginId;
    private String encPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다ㅣ.
        binding = ActivitySplashBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();

    }

    /**
     *  1. 역할: 컴포넌트 초기화
     * */
    public void initialize(){
        // 새로운 토큰 생성
        FirebaseInstallations.getInstance().getToken(false).addOnCompleteListener(new OnCompleteListener<InstallationTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<InstallationTokenResult> task) {
                if(!task.isSuccessful()){
                    return;
                }
                // Get new Instance ID token
                String token = task.getResult().getToken();

                // 서버에 토큰 저장
                saveToken(token);

                System.out.println("token: " + token);
            }
        });

        /** 초기화 */
        // ViewModel 참조객체를 생성하는 부분
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // 관찰로직
        observe();

        Intent intent = getIntent();


        /** 자동로그인*/
        //처음에는 SharedPreferences에 아무런 정보도 없으므로 값을 저장할 키들을 생성한다.
        // getString의 첫 번째 인자는 저장될 키, 두 번쨰 인자는 값입니다.
        // 첨엔 값이 없으므로 키값은 원하는 것으로 하시고 값을 null을 줍니다.
        SharedPreferences auto = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
        loginId = auto.getString("loginId",null);

        if(loginId != null){
            // 암호화된 비밀번호
            encPw = auto.getString("encPw",null);

            Log.d("가져온 id: ", loginId);
            Log.d("가져온 pw: ", encPw);

            loginViewModel.login(loginId, encPw);
            return;
        }else{
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    /**
     *  1. 역할: 로그인 성공 시 액티비티 전환
     * */
    public void observe(){
        // 소유자(액티비티티)가 활성화(ifecycle.State.STARTED, Lifecycle.State.RESUMED)상태에서 관찰하고 있는 LiveData의 값이 변경 시 콜백함수가 호출된다.
        loginViewModel.getLoginResult().observe(this, loginResult ->{
            // 로그인 성공 시("ok userType"을 보냄)
            if(loginResult.contains("ok")){
                Intent intent;
                // 트레이너 계정으로 로그인
                if(loginResult.split(" ")[1].contains("0")){
                    intent = new Intent(this, TrainerHomeActivity.class);
                } else{ // 회원계정으로 로그인
                    intent = new Intent(this, MemberHomeActivity.class);
                }

                // 로그인 아이디 홈화면으로 보내 줌
                intent.putExtra("loginId", loginId);

                /** 로그인 성공 시 shared에 정보가 없는 것이므로 정보 저장*/
                SharedPreferences auto = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
                SharedPreferences.Editor autoLogin = auto.edit();
                autoLogin.putString("loginId", loginId);
                autoLogin.putString("encPw", encPw);

                Log.d("저장한 id: ", loginId);
                Log.d("저장한 pw: ", encPw);

                //꼭 commit()을 해줘야 값이 저장됩니다
                autoLogin.commit();

                startActivity(intent);
                finish();
            } else if(loginResult.contains("fail")){ // 로그인 실패시
                Toast.makeText(this, "로그인에 실패했습니다. 아이디와 비밀번호를 확인해주세요.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void saveToken(String token ){

    }
}