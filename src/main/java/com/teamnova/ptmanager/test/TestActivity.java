package com.teamnova.ptmanager.test;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityTestBinding;

public class TestActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityTestBinding binding;
    private TestViewModel testViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다ㅣ.
        binding = ActivityTestBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        // ViewModel 참조객체를 생성하는 부분
        testViewModel = new  ViewModelProvider(this).get(TestViewModel.class);

        // 소유자(액티비티티)가 활성화(ifecycle.State.STARTED, Lifecycle.State.RESUMED)상태에서 관찰하고 있는 LiveData의 값이 변경 시 콜백함수가 호출된다.
        testViewModel.getData().observe(this, data ->{
            if(data.getLoginId() != null){
                // 통신 성공 & 사용자 데이터 존재 시 가져온 사용자 데이터를 화면에 출력한다.
                binding.infoTextView.setText(data.toString());
            } else{
                // 통신 성공 & 사용자 데이터 존재 하지 않을 시 아래 문구를 출력한다.
                binding.infoTextView.setText("일치하는 데이터 없음");
                Toast.makeText(TestActivity.this, "데이터 없음", Toast.LENGTH_SHORT).show();
            }
        });

        initialize();
    }

    /**
     * 컴포넌트 등록
     * */

    /**
     *  1. 역할: 컴포넌트 초기화
     * */
    public void initialize(){
        binding.btnGetUserInfo.setOnClickListener(this);
    }


    /**
     * 이벤트 처리
     * */

    /**
     *  1. 이벤트 종류: onClick
     *  2. 역할: 버튼 클릭 시 발생하는 이벤트 처리
     *  3. 종류
     *      1) R.id.btn_get_user_info: //가져오기 버튼
     *      2) 아이디를 입력한 사용자가 존재한다면 API 서버에서 정보를 가져온다.
     * */
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case  R.id.btn_get_user_info:
                String id = binding.userId.getText().toString();
                testViewModel.getUserInfo(id);
                break;
            default:
                break;
        }
    }
}