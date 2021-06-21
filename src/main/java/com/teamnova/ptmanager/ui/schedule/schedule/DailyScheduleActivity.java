package com.teamnova.ptmanager.ui.schedule.schedule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityDailyScheduleBinding;
import com.teamnova.ptmanager.databinding.ActivityLessonRegisterBinding;
import com.teamnova.ptmanager.model.lesson.LessonInfo;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.ui.home.trainer.fragment.TrainerHomeFragment;
import com.teamnova.ptmanager.ui.member.MemberAddActivity;
import com.teamnova.ptmanager.ui.schedule.lesson.LessonRegisterActivity;
import com.teamnova.ptmanager.util.GetDate;

public class DailyScheduleActivity extends AppCompatActivity implements View.OnClickListener{
    // binder
    private ActivityDailyScheduleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다.
        binding = ActivityDailyScheduleBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();

    }

    // 초기화
    public void initialize(){
        // 인텐트
        Intent dataIntent = getIntent();
        // 일자
        String dateOfYYYYMMDD = dataIntent.getStringExtra("date");

        // 일자(년월일 포함)
        String dateWithYMD = GetDate.getDateWithYMD(dateOfYYYYMMDD);

        // 요일
        String dayOfWeek = GetDate.getDayOfWeek(dateOfYYYYMMDD);

        binding.date.setText(dateWithYMD+"(" + dayOfWeek + ")");

        // onClickListener 등록
        setOnclickListener();
    }

    public void setOnclickListener(){
        binding.btnAddLesson.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent;

        switch(v.getId()){
            case  R.id.btn_add_lesson: // 레슨 추가 버튼 클릭
                // 레슨추가 액티비티로 이동
                intent = new Intent(DailyScheduleActivity.this, LessonRegisterActivity.class);
                intent.putExtra("returnPath", "DailyScheduleActivity");

                startActivity(intent);
                break;

            default:
                break;
        }
    }

    // 새로운 인텐트 발생 시
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // 레슨정보가 있다면
        LessonInfo lessonInfo = (LessonInfo)intent.getSerializableExtra("lessonInfo");
        if(lessonInfo != null){
            Log.d("레슨정보 등록 완료", lessonInfo.toString());
        }
    }
}