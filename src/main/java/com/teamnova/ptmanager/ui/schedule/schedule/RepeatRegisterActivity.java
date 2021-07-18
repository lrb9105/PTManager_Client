package com.teamnova.ptmanager.ui.schedule.schedule;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityRepeatRegisterBinding;
import com.teamnova.ptmanager.model.schedule.RepeatSchInfo;
import com.teamnova.ptmanager.ui.schedule.lesson.LessonRegisterActivity;
import com.teamnova.ptmanager.util.GetDate;

/**
 * RepeatRegisterActivity - 반복일정 등록 액티비티
 * */
public class RepeatRegisterActivity extends AppCompatActivity implements View.OnClickListener{
    // binder
    private ActivityRepeatRegisterBinding binding;

    // 반복종료일
    private String repeatExpireDateForDisplay;
    private String repeatExpireDateForSave;

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
    public void initialize() {
        setOnclickListener();
    }

    // onClickListener 세팅
    public void setOnclickListener() {
        binding.addRepeatSch.setOnClickListener(this);
        binding.layoutRepeatExpireDateSelect.setOnClickListener(this);
    }


    // 클릭
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case  R.id.layout_repeat_expire_date_select: // 반복종료일자
                /**
                 * 1. datePicker
                 * */
                DatePickerDialog dialog = new DatePickerDialog(this);

                // 반복종료일자 설정 완료 시
                dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String repeatExpireDateForDisplay = year + "년 " + (month <10 ? "0"+(month+1) : (month+1)) + "월 " + (dayOfMonth <10 ? "0" + dayOfMonth : dayOfMonth) + "일";
                        repeatExpireDateForSave = year + "" + (month <10 ? "0"+(month+1) : (month+1)) + (dayOfMonth <10 ? "0" + dayOfMonth : dayOfMonth);
                        binding.repeatExpireDate.setText(repeatExpireDateForDisplay);
                    }
                });

                dialog.show();

                break;
            case R.id.add_repeat_sch: // 반복일정 등록
                String repeatDay = "";
                // 체크한 요일 추가
                if(binding.checkboxMonday.isChecked()){
                    repeatDay += "월,";
                }

                if(binding.checkboxTuesday.isChecked()){
                    repeatDay += "화,";
                }

                if(binding.checkboxWednesday.isChecked()){
                    repeatDay += "수,";

                }

                if(binding.checkboxThursday.isChecked()){
                    repeatDay += "목,";

                }

                if(binding.checkboxFriday.isChecked()){
                    repeatDay += "금,";

                }

                if(binding.checkboxSaturday.isChecked()){
                    repeatDay += "토,";

                }

                if(binding.checkboxSunday.isChecked()){
                    repeatDay += "일";
                }

                // ,로 끝난다면 ,제거
                if(repeatDay.endsWith(",")){
                    repeatDay = repeatDay.substring(0, repeatDay.length() - 1);
                }

                // 반복 종료일 추가
                RepeatSchInfo repeatSchInfo = new RepeatSchInfo(repeatDay, repeatExpireDateForSave);

                // 레슨등록 액티비티로 보냄
                Intent intent = null;

                intent = new Intent(this, LessonRegisterActivity.class);

                // 강의정보를 보내 줌
                intent.putExtra("repeatSchInfo", repeatSchInfo);

                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
    }
}