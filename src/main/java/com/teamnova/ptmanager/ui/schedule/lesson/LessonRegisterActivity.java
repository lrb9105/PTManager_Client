package com.teamnova.ptmanager.ui.schedule.lesson;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityLessonRegisterBinding;
import com.teamnova.ptmanager.model.lecture.LectureInfoDto;
import com.teamnova.ptmanager.model.lesson.LessonInfo;
import com.teamnova.ptmanager.model.schedule.RepeatSchInfo;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.test.TestActivity;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.ui.schedule.lecture.LectureListActivity;
import com.teamnova.ptmanager.ui.schedule.lecture.LectureRegisterActivity;
import com.teamnova.ptmanager.ui.schedule.lecture.LectureRegisterdMemberListActivity;
import com.teamnova.ptmanager.ui.schedule.lecture.pass.PassRegisterActivity;
import com.teamnova.ptmanager.ui.schedule.reservation.ReservationApprovementActivity;
import com.teamnova.ptmanager.ui.schedule.schedule.DailyScheduleActivity;
import com.teamnova.ptmanager.ui.schedule.schedule.RepeatRegisterActivity;
import com.teamnova.ptmanager.util.GetDate;
import com.teamnova.ptmanager.viewmodel.schedule.lesson.LessonViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class LessonRegisterActivity extends AppCompatActivity implements View.OnClickListener{
    // binder
    private ActivityLessonRegisterBinding binding;

    // 시작일자
    private String startDate;

    // 시작시간
    private int startHour;
    private int startMinute;
    private String startTimeWithoutAmPm;
    private String currentTime;

    //종료시간
    private int endHour;
    private int endMinute;
    private String endTimeWithoutAmPm;

    // 다른 액티비티로 이동 후 다시 돌아와서 결과값을 받기 위한 객체
    private ActivityResultLauncher<Intent> startActivityResult;

    // 강의 정보
    private LectureInfoDto lectureInfo;

    // 반복 정보
    private RepeatSchInfo repeatSchInfo;

    // 다른 액티비티로 이동 후 다시 돌아와서 결과값을 받기 위한 객체
    private ActivityResultLauncher<Intent> startActivityResultToLRML;

    // 반복일정 정보를 받아올 객체
    private ActivityResultLauncher<Intent> startActivityResultToLRRA;

    // 회원 정보
    private ArrayList<FriendInfoDto> memberInfoList;

    // 시작, 종료시간(저장용)
    private String startTime;
    private String endTime;

    // 레슨정보
    private LessonInfo lessonInfo;

    // 레슨 viewModel
    private LessonViewModel lessonViewModel;

    // Retrofit 통신의 결과를 전달하는 핸들러
    private Handler resultHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 결과값을 받기 위해 사용하는 메소드
        // super.onCreate 이전에 배치해야 함
        startActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {

                            lectureInfo = (LectureInfoDto)result.getData().getSerializableExtra("lectureInfo");

                            Log.d("강의목록 화면에서 강의정보 보내 옴", lectureInfo.toString());

                            // 1. 화면에 강의명 보여주기
                            binding.lectureType.setText(lectureInfo.getLectureName());

                            // 2. 레슨정보 dto에 강의정보, 트레이너 정보 넣기
                            lessonInfo.setLectureId(lectureInfo.getLectureId());
                            lessonInfo.setTrainerId(lectureInfo.getTrainerId());

                            Log.d("lectureInfo", lectureInfo.getLectureName());
                        }
                    }
                });

        startActivityResultToLRML = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {

                            // 회원목록 가져오기
                            memberInfoList = (ArrayList<FriendInfoDto>)result.getData().getSerializableExtra("memberInfoList");

                            Log.d("등뢱된 회원 목록 화면에서 회원정보 보내 옴123123", memberInfoList.get(0).toString());

                            /**
                             *     회원목록 만들기
                             *  1. 강의 수강하는 멤버수만큼 반복
                             * */
                            String memberList = "";

                            int size = memberInfoList.size();

                            // 동일한 유저명이 두번 출력됨


                           for(int i = 0; i < size; i++){
                                memberList += memberInfoList.get(i).getUserName();

                                if(size > 1 && i != memberInfoList.size() - 1){
                                    memberList += ",";
                                }
                            }

                            // 1. 화면에 회원명 보여주기
                            binding.memberSelect.setText(memberList);

                            // 2. 레슨정보 dto에 회원정보 넣기
                            lessonInfo.setMemberList(memberInfoList);

                            Log.d("회원정보:" , "" + memberInfoList.size());

                            Log.d("memberInfoList", memberInfoList.get(0).getLectureName());
                        }
                    }
                });

        // 반복일정
        startActivityResultToLRRA = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {

                            repeatSchInfo = (RepeatSchInfo) result.getData().getSerializableExtra("repeatSchInfo");

                            Log.d("반복일정 화면에서 반복정보 보내 옴", repeatSchInfo.toString());

                            // 1. 화면에 반복일자 보여주기
                            binding.repeat.setText(repeatSchInfo.getRepeatDay());

                            // 시작일자 등록
                            repeatSchInfo.setRepeatSrtDate(startDate);

                            // 2. 레슨정보 dto에 강의정보, 트레이너 정보 넣기
                            lessonInfo.setRepeatInfo(repeatSchInfo);

                            Log.d("repeatSchInfo", repeatSchInfo.getRepeatDay());
                        }
                    }
                });

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

        // 레슨정보 dto 초기화
        lessonInfo = new LessonInfo();

        // 뷰모델 초기화
        lessonViewModel = new ViewModelProvider(this).get(LessonViewModel.class);

        // 시작시간 세팅
        try {
            // 시작시간
            currentTime = GetDate.getCurrentTime(false);
            Calendar calendar = Calendar.getInstance();
            startHour = 13;
            startMinute= 0;
            startTime = startHour + ":" + startMinute;

            // 종료시간
            calendar.add(Calendar.HOUR,1);
            endTimeWithoutAmPm =  GetDate.getCurrentTime(true);
            endHour = 14;
            endMinute= 0;
            endTime = endHour + ":" + endMinute;

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Retrofit 통신 핸들러
        resultHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                // 레슨 등록 성공
                if(msg.what == 0){
                    // 등록 성공여부
                    String result = (String)msg.obj;

                    Log.d("레슨 등록 완료", result);

                    if(result.contains("success")){ // 성공
                        // 다이얼로그빌더
                        AlertDialog.Builder builder = new AlertDialog.Builder(LessonRegisterActivity.this);

                        builder.setMessage("레슨 등록이 완료되었습니다.");

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                /**
                                 * returnPath에 따라 이동
                                 * 1. DailyScheduleActivity
                                 * 2. TrainerHomeActivity
                                 * */
                                Intent intent = null;
                                String returnPath = getIntent().getStringExtra("returnPath");

                                if(returnPath.equals("DailyScheduleActivity")){
                                    intent = new Intent(LessonRegisterActivity.this, DailyScheduleActivity.class);
                                } else if(returnPath.equals("TrainerHomeActivity")){
                                    intent = new Intent(LessonRegisterActivity.this, TrainerHomeActivity.class);
                                }

                                // 이동할 액티비티가 이미 작업에서 실행중이라면 기존 인스턴스를 가져오고 위의 모든 액티비티를 삭제
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                                // 레슨정보 실어서 보냄
                                intent.putExtra("lessonInfo",lessonInfo);

                                startActivity(intent);
                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else if(result.contains("ALREADY REGISTERD")) {
                        Toast.makeText(LessonRegisterActivity.this, "선택하신 시간은 이미 레슨이 등록되어 있습니다.",Toast.LENGTH_SHORT).show();
                    } else {  //실패
                        Log.d("수강권 등록 실패","fail");
                    }
                }
            }
        };

        changeTextColor();
    }

    // 온클릭리스너 등록
    private void setOnclickListener(){
        binding.layoutLectureTypeSelect.setOnClickListener(this);
        binding.layoutMemberSelect.setOnClickListener(this);
        binding.layoutStartDateSelect.setOnClickListener(this);
        binding.layoutProcessTimeSelect.setOnClickListener(this);
        binding.layoutRepeatSelect.setOnClickListener(this);
        binding.startTime.setOnClickListener(this);
        binding.endTime.setOnClickListener(this);
        binding.addLesson.setOnClickListener(this);
        binding.layoutRepeatSelect.setOnClickListener(this);
    }

    // *색상 빨간색으로 변경
    private void changeTextColor(){
        SpannableStringBuilder ssb = new SpannableStringBuilder(binding.lectureTypeWithStar.getText().toString());
        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.lectureTypeWithStar.setText(ssb);

        SpannableStringBuilder ssb2 = new SpannableStringBuilder(binding.memberWithStar.getText().toString());
        ssb2.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.memberWithStar.setText(ssb2);

        SpannableStringBuilder ssb3 = new SpannableStringBuilder(binding.lessonDateWithStar.getText().toString());
        ssb3.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.lessonDateWithStar.setText(ssb3);

        SpannableStringBuilder ssb4 = new SpannableStringBuilder(binding.processTimeWithStar.getText().toString());
        ssb4.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.processTimeWithStar.setText(ssb4);
    }




    @Override
    public void onClick(View v) {
        Intent intent;

        switch(v.getId()){
            case  R.id.layout_lecture_type_select: // 강의종류
                // 강의목록 액티비티로 이동
                intent = new Intent(LessonRegisterActivity.this, LectureListActivity.class);
                // 강의목록 액티비티에서 결과값을 받는 act가 두군데이므로 구분 위해 activityName을 보내줌
                intent.putExtra("activityName", "LessonRegisterActivity");

                startActivityResult.launch(intent);
                break;
            case  R.id.layout_member_select: // 회원
                // 강의 id 넣기
                String lectureId = "";

                if(lectureInfo != null){
                    // 이 강의 등록회원 액티비티로 이동
                    intent = new Intent(LessonRegisterActivity.this, LectureRegisterdMemberListActivity.class);

                   lectureId = lectureInfo.getLectureId();

                    // 테스트
                    /*Log.d("여기 안들어옴?", "123123123");
                    intent = new Intent(this, ReservationApprovementActivity.class);
                    startActivity(intent);
*/
                } else {
                    Toast.makeText(this, "강의를 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 테스트
                //String lectureId = "1";
                intent.putExtra("lectureId", lectureId);

                startActivityResultToLRML.launch(intent);
                break;
            case  R.id.layout_start_date_select: // 시작일자
                /**
                 * 1. datePicker
                 * */
                DatePickerDialog dialog = new DatePickerDialog(this);

                // 시작일자 설정 완료 시
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
            case  R.id.start_time: // 시작시간
                TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String hour = (hourOfDay < 10)? "0" + hourOfDay : ""+hourOfDay;
                        // 시작시간 세팅
                        startHour = Integer.parseInt(hour);
                        startMinute = minute;

                        // 저장할 시작시간
                        startTime = hour + ":" +minute;

                        // 종료시간 세팅
                        endHour = startHour + 1;

                        endMinute = startMinute;

                        // 저장할 종료시간
                        endTime = endHour + ":" +endMinute;

                        startTimeWithoutAmPm = ((hourOfDay < 10)? "0"+hourOfDay : hourOfDay) + ":" + minute;
                        endTimeWithoutAmPm = startTimeWithoutAmPm;
                        binding.startTime.setText(GetDate.getAmPmTime(hourOfDay,minute) + " 부터");
                        binding.endTime.setText(GetDate.getAmPmTime(hourOfDay + 1,minute) + " 까지");
                    }
                },startHour, startMinute, false);

                timePickerDialog.show();

                break;
            case R.id.end_time: // 종료시간
                Log.d("종료시각:",  "" + endHour);
                Log.d("종료분:", "" + endMinute);

                timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if(hourOfDay < startHour){
                            hourOfDay = startHour;
                        }

                        String hour = (hourOfDay < 10)? "0" + hourOfDay : ""+hourOfDay;
                        endHour = Integer.parseInt(hour);
                        endMinute = minute;

                        // 저장할 종료시간
                        endTime = endHour + ":" + endMinute;


                        endTimeWithoutAmPm = ((hourOfDay < 10)? "0"+hourOfDay : hourOfDay) + ":" + minute;
                        binding.endTime.setText(GetDate.getAmPmTime(hourOfDay,minute) + " 까지");
                    }
                },endHour, endMinute, false);

                timePickerDialog.show();
                break;
            case  R.id.layout_repeat_select: // 반복
                // 반복설정 액티비티로 이동
                intent = new Intent(LessonRegisterActivity.this, RepeatRegisterActivity.class);

                startActivityResultToLRRA.launch(intent);

                break;
            case  R.id.add_lesson: // 레슨 등록버튼 클릭
                Log.d("레슨", "1111");

                // 1. 레슨일, 시작시간, 종료시간, 확정여부(Y), 취소여부(N), 메모 설정
                lessonInfo.setLessonDate(startDate);
                lessonInfo.setLessonSrtTime(startTime);
                lessonInfo.setLessonEndTime(endTime);
                lessonInfo.setConfirmYn("Y");
                lessonInfo.setCancelYn("N");
                lessonInfo.setMemo(binding.memo.getText().toString());

                // 레슨정보 등록
                lessonViewModel.registerLessonInfo(resultHandler, lessonInfo);

                Log.d("레슨정보", lessonInfo.toString());
                Log.d("수강권 정보", lessonInfo.getMemberList().get(0).getLecturePassId());

                break;
            default:
                break;
        }
    }
}