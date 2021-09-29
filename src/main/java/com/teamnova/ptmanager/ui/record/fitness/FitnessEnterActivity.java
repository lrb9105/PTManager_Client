package com.teamnova.ptmanager.ui.record.fitness;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityFitnessEnterBinding;
import com.teamnova.ptmanager.databinding.ActivityFitnessKindListBinding;

public class FitnessEnterActivity extends AppCompatActivity implements View.OnClickListener{
    // binder
    private ActivityFitnessEnterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFitnessEnterBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();

        setOnClickListener();
    }

    public void setOnClickListener(){
        binding.btnBack.setOnClickListener(this);
    }

    // 초기화
    public void initialize(){

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_back: // 뒤로가기
                onBackPressed();
                break;
        }
    }
}