package com.teamnova.ptmanager.ui.home.member;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityHomeBinding;
import com.teamnova.ptmanager.databinding.ActivityMemberHomeBinding;

public class MemberHomeActivity extends AppCompatActivity {
    ActivityMemberHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다.
        binding = ActivityMemberHomeBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());
        
        super.onCreate(savedInstanceState);
    }
}