package com.teamnova.ptmanager.ui.schedule.lecture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityLectureRegisterBinding;
import com.teamnova.ptmanager.ui.schedule.lesson.LessonRegisterActivity;
import com.teamnova.ptmanager.ui.schedule.schedule.DailyScheduleActivity;

public class LectureRegisterActivity extends AppCompatActivity implements View.OnClickListener{
    // binder
    private ActivityLectureRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다.
        binding = ActivityLectureRegisterBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();

    }

    // 초기화
    public void initialize(){

    }

    // 온클릭리스너 등록
    public void setOnclickListener(){
        binding.layoutLectureType.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent;

        switch(v.getId()){
            case  R.id.layout_lecture_type_select: // 강의종류
                // 강의목록 액티비티로 이동
                intent = new Intent(LectureRegisterActivity.this, LectureListActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }
}