package com.teamnova.ptmanager.ui.schedule.schedule;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityRepeatRegisterBinding;

public class RepeatRegisterActivity extends AppCompatActivity {
    // binder
    private ActivityRepeatRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다.
        binding = ActivityRepeatRegisterBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();

    }

    // 초기화
    public void initialize(){

    }
}