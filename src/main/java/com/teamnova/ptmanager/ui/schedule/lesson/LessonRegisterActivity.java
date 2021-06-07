package com.teamnova.ptmanager.ui.schedule.lesson;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityLessonRegisterBinding;
import com.teamnova.ptmanager.ui.schedule.lecture.LectureListActivity;
import com.teamnova.ptmanager.ui.schedule.lecture.LectureRegisterActivity;
import com.teamnova.ptmanager.ui.schedule.lecture.LectureRegisterdMemberListActivity;
import com.teamnova.ptmanager.ui.schedule.schedule.RepeatRegisterActivity;
import com.teamnova.ptmanager.util.GetDate;

public class LessonRegisterActivity extends AppCompatActivity implements View.OnClickListener{
    // binder
    private ActivityLessonRegisterBinding binding;

    // 시작일자
    private String startDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다.
        binding = ActivityLessonRegisterBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();

    }

    // 초기화
    public void initialize(){
        setOnclickListener();
    }

    // 온클릭리스너 등록
    public void setOnclickListener(){
        binding.layoutLectureTypeSelect.setOnClickListener(this);
        binding.layoutMemberSelect.setOnClickListener(this);
        binding.layoutStartDateSelect.setOnClickListener(this);
        binding.layoutProcessTimeSelect.setOnClickListener(this);
        binding.layoutRepeatSelect.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent;

        switch(v.getId()){
            case  R.id.layout_lecture_type_select: // 강의종류
                // 강의목록 액티비티로 이동
                intent = new Intent(LessonRegisterActivity.this, LectureListActivity.class);
                startActivity(intent);
                break;
            case  R.id.layout_member_select: // 회원
                // 이 강의 등록회원 액티비티로 이동
                intent = new Intent(LessonRegisterActivity.this, LectureRegisterdMemberListActivity.class);

                //String lectureId = binding.lectureType.getText().toString();
                // 테스트
                String lectureId = "1";
                intent.putExtra("lectureId", lectureId);

                startActivity(intent);
                break;
            case  R.id.layout_start_date_select: // 시작일자
                /**
                 * 1. datePicker
                 * */
                DatePickerDialog dialog = new DatePickerDialog(this);

                // 생년월일 설정 완료 시
                dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String birthDayForDisplay = year + "년 " + (month <10 ? "0"+(month+1) : (month+1)) + "월 " + (dayOfMonth <10 ? "0" + dayOfMonth : dayOfMonth) + "일";
                        startDate = year + "" + (month <10 ? "0"+(month+1) : (month+1)) + (dayOfMonth <10 ? "0" + dayOfMonth : dayOfMonth);
                        String dayOfWeek = GetDate.getDayOfWeek(startDate);

                        // 화면에 출력
                        binding.startDate.setText(birthDayForDisplay + "(" + dayOfWeek + ")");
                    }
                });

                dialog.show();

                break;
            case  R.id.layout_process_time_select: // 진행시간
                // timePicker - 시작, 종료시간 선택

                break;
            case  R.id.layout_repeat_select: // 반복
                // 반복설정 액티비티로 이동
                intent = new Intent(LessonRegisterActivity.this, RepeatRegisterActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}