package com.teamnova.ptmanager.ui.schedule.lesson;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityLessonDetailBinding;
import com.teamnova.ptmanager.ui.schedule.lecture.LectureListActivity;
import com.teamnova.ptmanager.ui.schedule.lecture.LectureRegisterdMemberListActivity;
import com.teamnova.ptmanager.ui.schedule.schedule.RepeatRegisterActivity;
import com.teamnova.ptmanager.util.GetDate;

public class LessonDetailActivity extends AppCompatActivity implements View.OnClickListener{
    // binder
    private ActivityLessonDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다.
        binding = ActivityLessonDetailBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();

        setOnclickListener();

    }

    // 초기화
    public void initialize(){
        setOnclickListener();
    }

    // 온클릭리스너 등록
    public void setOnclickListener(){
        binding.checkAttendance.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent;

        switch(v.getId()){
            case  R.id.check_attendance: // 출석체크
                // 출석체크 액티비티로 이동
                intent = new Intent(LessonDetailActivity.this, LessonAttendanceActivity.class);
                //String lessonId = ; - 시간별 일정에서 넘어올 때 레슨id를 가져옴!
                String lessonId = "1";

                intent.putExtra("lessonId", lessonId);

                startActivity(intent);
                break;
            default:
                break;
        }
    }
}