package com.teamnova.ptmanager.ui.schedule.lesson;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
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
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityLessonRegisterBinding;
import com.teamnova.ptmanager.databinding.ActivityLessonReserveBinding;
import com.teamnova.ptmanager.model.lecture.LectureInfoDto;
import com.teamnova.ptmanager.model.lecture.pass.PassInfoDto;
import com.teamnova.ptmanager.model.lesson.LessonInfo;
import com.teamnova.ptmanager.model.lesson.LessonSchInfo;
import com.teamnova.ptmanager.model.schedule.RepeatSchInfo;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.schedule.lecture.LectureService;
import com.teamnova.ptmanager.network.schedule.lesson.LessonService;
import com.teamnova.ptmanager.ui.home.member.DayViewActivity;
import com.teamnova.ptmanager.ui.home.member.Event;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.ui.schedule.lecture.LectureListActivity;
import com.teamnova.ptmanager.ui.schedule.lecture.LectureRegisterdMemberListActivity;
import com.teamnova.ptmanager.ui.schedule.schedule.DailyScheduleActivity;
import com.teamnova.ptmanager.ui.schedule.schedule.RepeatRegisterActivity;
import com.teamnova.ptmanager.util.GetDate;
import com.teamnova.ptmanager.viewmodel.schedule.lesson.LessonViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * 레슨예약 화면(일반회원)
 * */
public class LessonReserveActivity extends AppCompatActivity implements View.OnClickListener{
    // binder
    private ActivityLessonReserveBinding binding;

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

    // 반복 정보
    private RepeatSchInfo repeatSchInfo;

    // 반복일정 정보를 받아올 객체
    private ActivityResultLauncher<Intent> startActivityResultToLRRA;

    // 회원 정보
    private ArrayList<FriendInfoDto> memberInfoList;

    // 시작, 종료시간(저장용)
    private String startTime;
    private String endTime;

    // 레슨정보
    private LessonInfo lessonInfo;

    // Retrofit 통신의 결과를 전달하는 핸들러
    private Handler resultHandler;

    // 수강권 정보
    private PassInfoDto passInfo;

    // 오늘 날짜(yyyy년 mm월 dd일)
    private String todayDate;
    // 오늘 날짜(yyyyMMdd)
    private String todayDateForServer;
    // 트레이너 id(USER_ID)
    private String trainerId;
    // 일반회원 id(USER_ID)
    private String memberId;

    // 예약정보를 저장 후 레슨정보를 가져옴
    private LessonSchInfo lessonSchInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // super.onCreate 이전에 배치해야 함


        // 반복일정
       /* startActivityResultToLRRA = registerForActivityResult(
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
                });*/

        super.onCreate(savedInstanceState);
        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다.
        binding = ActivityLessonReserveBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initialize();

    }

    // 초기화
    public void initialize(){
        setOnclickListener();

        // DayViewAct에서 데이터 가져오기
        // 오늘 날짜(yyyy년 mm월 dd일)
        todayDate = getIntent().getStringExtra("todayDate");
        // 오늘 날짜(yyyyMMdd)
        todayDateForServer = getIntent().getStringExtra("todayDateForServer");
        // 트레이너 id(USER_ID)
        trainerId = getIntent().getStringExtra("trainerId");
        // 일반회원 id(USER_ID)
        memberId = getIntent().getStringExtra("memberId");

        // 레슨정보 dto 초기화
        lessonInfo = new LessonInfo();

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
        /*resultHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                // 레슨 등록 성공
                if(msg.what == 0){
                    // 등록 성공여부
                    String result = (String)msg.obj;

                    Log.d("레슨 예약 완료", result);

                    if(result.contains("success")){ // 성공
                        // 다이얼로그빌더
                        AlertDialog.Builder builder = new AlertDialog.Builder(LessonReserveActivity.this);

                        builder.setMessage("레슨 등록이 완료되었습니다.");

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                *//**
                                 * returnPath에 따라 이동
                                 * 1. DailyScheduleActivity
                                 * 2. TrainerHomeActivity
                                 * *//*
                                Intent intent = null;
                                String returnPath = getIntent().getStringExtra("returnPath");

                                if(returnPath.equals("DailyScheduleActivity")){
                                    intent = new Intent(LessonReserveActivity.this, DailyScheduleActivity.class);
                                } else if(returnPath.equals("TrainerHomeActivity")){
                                    intent = new Intent(LessonReserveActivity.this, TrainerHomeActivity.class);
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
                        Toast.makeText(LessonReserveActivity.this, "선택하신 시간은 이미 레슨이 등록되어 있습니다.",Toast.LENGTH_SHORT).show();
                    } else {  //실패
                        Log.d("수강권 등록 실패","fail");
                    }
                }
            }
        };*/

        changeTextColor();

        // 수강권 정보 가져오기
        Retrofit retrofit= RetrofitInstance.getRetroClient();
        LectureService service = retrofit.create(LectureService.class);

        // http request 객체 생성
        Call<PassInfoDto> call = service.getLecturePassInfo(memberId);

        new NetworkCall().execute(call);
    }

    // 온클릭리스너 등록
    private void setOnclickListener(){
        binding.layoutLectureTypeSelect.setOnClickListener(this);
        binding.layoutStartDateSelect.setOnClickListener(this);
        binding.layoutProcessTimeSelect.setOnClickListener(this);
        //binding.layoutRepeatSelect.setOnClickListener(this);
        binding.startTime.setOnClickListener(this);
        binding.endTime.setOnClickListener(this);
        binding.requestReservation.setOnClickListener(this);
        //binding.layoutRepeatSelect.setOnClickListener(this);
    }

    // *색상 빨간색으로 변경
    private void changeTextColor(){
        SpannableStringBuilder ssb = new SpannableStringBuilder(binding.lecturePassWithStar.getText().toString());
        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.lecturePassWithStar.setText(ssb);

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
            /**
             * 1. datePicker
             * */
            /*case  R.id.layout_start_date_select: // 시작일자
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

                break;*/
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
            /*case  R.id.layout_repeat_select: // 반복
                // 반복설정 액티비티로 이동
                intent = new Intent(LessonReserveActivity.this, RepeatRegisterActivity.class);

                startActivityResultToLRRA.launch(intent);

                break;*/
            case  R.id.btn_reserve_lesson: // 레슨 예약버튼 클릭
                Log.d("레슨", "1111");

                // 1. 레슨일, 시작시간, 종료시간, 확정여부(Y), 취소여부(N), 메모 설정
                /*lessonInfo.setLessonDate(startDate);
                lessonInfo.setLessonSrtTime(startTime);
                lessonInfo.setLessonEndTime(endTime);
                lessonInfo.setConfirmYn("Y");
                lessonInfo.setCancelYn("N");
                lessonInfo.setMemo(binding.memo.getText().toString());*/

                // 레슨 예약

                break;
            case R.id.request_reservation: //예약 요청
                // 다이얼로그 출력
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage("예약요청");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // 레슨정보 만들기
                        lessonInfo.setLectureId(passInfo.getLectureId());
                        lessonInfo.setTrainerId(trainerId);

                        ArrayList<FriendInfoDto> memberList = new ArrayList<>();
                        memberList.add((FriendInfoDto)getIntent().getSerializableExtra("memberInfo"));
                        lessonInfo.setMemberList(memberList);
                        lessonInfo.setLessonDate(todayDateForServer);
                        lessonInfo.setLessonSrtTime(startTime);
                        lessonInfo.setLessonEndTime(endTime);
                        lessonInfo.setConfirmYn("N"); // 예약이므로 확정이 아님!
                        lessonInfo.setCancelYn("N");
                        lessonInfo.setRepeatYn("N");
                        lessonInfo.setMemo(binding.memo.getText().toString());

                        // 서버에 레슨예약 데이터 저장
                        Retrofit retrofit= RetrofitInstance.getRetroClient();
                        LessonService service = retrofit.create(LessonService.class);

                        Log.d("lessonInfo", lessonInfo.toString());

                        // http request 객체 생성
                        Call<LessonSchInfo> call = service.registerReservation(lessonInfo);
                        //Call<String> call = service.registerReservation(lessonInfo);

                        new RegisterReservation().execute(call);
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
            default:
                break;
        }
    }

    // 수강권 정보 가져오기
    private class NetworkCall extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<PassInfoDto> response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPreExecute", "11");
        }

        @Override
        protected String  doInBackground(Call[] params) {
            try {
                retrofit2.Call<PassInfoDto> call = params[0];
                response = call.execute();

                if(response.body() != null){
                    passInfo = response.body();
                }

                return response.body().toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // 결과값을 가져 왔다면 ->  db에서 아무런 데이터를 조회해오지 못하면 "[]"값을 가져온다.
            if(!result.equals("[]")){
                // 수강권 정보 세팅
                binding.lecturePassInfo.setText(passInfo.getLectureName() + "(남은횟수: " + (passInfo.getTotalCnt() - passInfo.getUsedCnt()) + "회)");
                binding.startDate.setText(todayDate);
            }
        }
    }

    // 예약요청
    private class RegisterReservation extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<LessonSchInfo> response;
        //private retrofit2.Response<String> response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPreExecute", "11");
        }

        @Override
        protected String  doInBackground(Call[] params) {
            try {
                retrofit2.Call<LessonSchInfo> call = params[0];
                //retrofit2.Call<String> call = params[0];
                response = call.execute();

                /*Log.d("11",response.body());
                Log.d("11",response.message());*/

                if(response.body() != null){
                    lessonSchInfo = response.body();
                    return response.body().toString();
                } else{
                    return "";
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // 예약요청을 완료했다면
            Log.d("예약요청 완료", "11");

            if(lessonSchInfo == null){
                Toast.makeText(LessonReserveActivity.this, "선택하신 시간은 이미 레슨이 등록되어 있습니다.",Toast.LENGTH_SHORT).show();
            }else{
                // DayViewAct로 이동
                Intent intent = new Intent(LessonReserveActivity.this, DayViewActivity.class);
                intent.putExtra("lessonSchInfo", lessonSchInfo);

                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    }
}