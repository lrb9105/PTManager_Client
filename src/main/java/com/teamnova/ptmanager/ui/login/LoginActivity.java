package com.teamnova.ptmanager.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityLoginBinding;
import com.teamnova.ptmanager.ui.home.member.MemberHomeActivity;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.ui.register.RegisterActivity;
import com.teamnova.ptmanager.ui.login.findid.FindIdActivity;
import com.teamnova.ptmanager.ui.login.findpw.FindPwActivity;
import com.teamnova.ptmanager.util.Sha256;
import com.teamnova.ptmanager.viewmodel.login.LoginViewModel;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    // Binder
    private ActivityLoginBinding binding;
    // ViewModel
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다ㅣ.
        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();
    }

    /**
     * 컴포넌트 등록
     * */

    /**
     *  1. 역할: 컴포넌트 초기화
     * */
    public void initialize(){
        Intent intent = getIntent();

        // 아이디 찾기 후 로그인 화면에 왔을 때 loginId를 가지고 있다.
        // 아이디에 세팅
        if(intent != null){
            String loginId = getIntent().getStringExtra("loginId");
            binding.id.setText(loginId);
        }

        binding.btnRegister.setOnClickListener(this);
        binding.findId.setOnClickListener(this);
        binding.findPw.setOnClickListener(this);
        binding.btnLogin.setOnClickListener(this);

        // ViewModel 참조객체를 생성하는 부분
        loginViewModel = new  ViewModelProvider(this).get(LoginViewModel.class);

        // 관찰로직
        observe();
    }


    /**
     * 이벤트 처리
     * */

    /**
     *  1. 이벤트 종류: onClick
     *  2. 역할: 트레이너/일반회원 버튼 클릭 시 발생하는 이벤트 정의
     *  3. 종류
     *      1) R.id.btn_login: //로그인버튼
     *          1> id/pw 유효성체크
     *          2> 입력한 id에 해당하는 pw 서버에서 가져와서 입력한 pw와 동일한지 확인 후 로그인
     *          3> HomeActivity로 이동
     *      2) R.id.textView_register: //회원가입
     *          1> RegisterActivity로 이동
     *      3) R.id.textView_find_id_pw: //아이디비밀번호찾기 링크
     *          1> FindIdAndPwActivity로 이동
     * */
    @Override
    public void onClick(View v) {
        Intent intent;

        switch(v.getId()){
            case  R.id.btn_register: // 회원가입 버튼
                intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
            case  R.id.find_id: // 아이디 찾기
                intent = new Intent(this, FindIdActivity.class);
                startActivity(intent);
                break;
            case  R.id.find_pw: // 비밀번호 찾기
                intent = new Intent(this, FindPwActivity.class);
                startActivity(intent);
                break;
            case  R.id.btn_login: // 로그인
                String encPw = Sha256.encrypt(binding.pw.getText().toString());

                loginViewModel.login(binding.id.getText().toString(), encPw);
                finish();
                break;
            default:
                break;
        }
    }

    /**
     *  1. 역할: 로그인 성공 시 액티비티 전환
     * */
    public void observe(){
        // 소유자(액티비티티)가 활성화(ifecycle.State.STARTED, Lifecycle.State.RESUMED)상태에서 관찰하고 있는 LiveData의 값이 변경 시 콜백함수가 호출된다.
        loginViewModel.getLoginResult().observe(this, loginResult ->{
            Log.d("11aaa", "" + loginResult.contains("ok"));
            Log.d("11aaa33", "" + loginResult);

            Log.d("11", loginResult.getClass().getName());

            // 로그인 성공 시
            if(loginResult.contains("ok")){
                Intent intent;
                // 트레이너 계정으로 로그인
                if(loginResult.split(" ")[1].contains("0")){
                    intent = new Intent(this, TrainerHomeActivity.class);
                } else{
                    intent = new Intent(this, MemberHomeActivity.class);
                }

                // 로그인 아이디 홈화면으로 보내 줌
                intent.putExtra("loginId", binding.id.getText().toString());

                // debug: 로그인 시 유저타입에 따라 메인화면 변경
                Log.d("로그인 시 유저타입에 따라 메인화면 변경", loginResult.split(" ")[1]);

                startActivity(intent);
            } else if(loginResult.contains("fail")){ // 로그인 실패시
                Toast.makeText(this, "로그인에 실패했습니다. 아이디와 비밀번호를 확인해주세요.", Toast.LENGTH_LONG).show();
            }
        });
    }
}