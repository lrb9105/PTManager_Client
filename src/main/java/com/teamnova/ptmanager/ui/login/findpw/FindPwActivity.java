package com.teamnova.ptmanager.ui.login.findpw;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityFindPwBinding;
import com.teamnova.ptmanager.viewmodel.login.LoginViewModel;

public class FindPwActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityFindPwBinding binding;

    // loginViewModel
    private LoginViewModel loginViewModel;

    // Retrofit 통신의 결과를 전달하는 핸들러
    private Handler loginHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다ㅣ.
        binding = ActivityFindPwBinding.inflate(getLayoutInflater());

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
        // 뷰모델 초기화
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        binding.btnNext.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);

        // Retrofit 통신 핸들러
        loginHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what == 2){
                    String loginId = (String) msg.obj;
                    Log.d("로그인 id: ", loginId);

                    // 로그인 아이디가 "null"이 아니라면 다음 액티비티로 이동
                    if(!loginId.contains("null")){
                        binding.textViewRequirement1.setVisibility(View.GONE);
                        Log.d("로그인아이디:", loginId);

                        Intent intent = new Intent(FindPwActivity.this, FindPw2Activity.class);
                        intent.putExtra("loginId", loginId);
                        startActivity(intent);
                    } else{ //null 이라면 존재하지 않는다는 메시지 띄워주기
                        binding.textViewRequirement1.setVisibility(View.VISIBLE);
                        binding.textViewRequirement1.setText("존재하지 않는 아이디입니다.");
                        binding.textViewRequirement1.setTextColor(ResourcesCompat.getColor(getResources(),R.color.red,null));

                    }
                }
            }
        };
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

        switch(v.getId()){
            case  R.id.btn_next:
                loginViewModel.checkIsValidId(loginHandler, binding.loginId.getText().toString());
                break;
            case R.id.btn_back: //뒤로가기
                onBackPressed();
                break;
            default:
                break;
        }
    }
}