package com.teamnova.ptmanager.ui.home.member;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.framgia.library.calendardayview.CalendarDayView;
import com.framgia.library.calendardayview.EventView;
import com.framgia.library.calendardayview.data.IEvent;
import com.framgia.library.calendardayview.decoration.CdvDecorationDefault;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityFindPw3Binding;
import com.teamnova.ptmanager.databinding.ActivityTrainerDayViewBinding;
import com.teamnova.ptmanager.model.lesson.LessonSchInfo;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.schedule.lesson.LessonService;
import com.teamnova.ptmanager.ui.schedule.lesson.LessonDetailActivity;
import com.teamnova.ptmanager.ui.schedule.lesson.LessonRegisterActivity;
import com.teamnova.ptmanager.ui.schedule.lesson.LessonReserveActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Retrofit;

public class DayViewActivity extends AppCompatActivity {
    private CalendarDayView dayView;

    private ArrayList<IEvent> events;

    private ArrayList<LessonSchInfo> lessonSchList;

    private String memberLoginId;

    private ActivityTrainerDayViewBinding binding;

    // LessonReserveAct(예약 화면)에서 예약완료 시 다시 넘어옴
    private ActivityResultLauncher<Intent> startActivityResult;
    // LessonDetail(레슨내역)에서 예약취소요청 완료 시 다시 넘어옴
    private ActivityResultLauncher<Intent> startActivityResult2;

    // 일반회원 정보
    private FriendInfoDto memberInfo;

    // 예약정보 저장했는지 여부
    private boolean isReserved;
    // 예약취소했는지 여부
    private boolean isCanceled;

    // 예약 or 예약취소한 레슨정보
    private LessonSchInfo lesson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // events에 추가
                            lesson = (LessonSchInfo)result.getData().getSerializableExtra("lessonSchInfo");
                            Log.d("lesson:", lesson.toString());

                            Event e = makeEvent(lesson);

                            //dayView.refresh();
                            if(events == null){
                                events = new ArrayList<>();
                            }
                            // 예약완료 시 예약완료여부 true넣어줌
                            isReserved = true;

                            events.add(e);
                            dayView.setEvents(events);
                        }
                    }
                });

        // 예약취소 완료 시
        startActivityResult2 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Log.d("예약취소 완료 시 데이터 넘어오는지 확인", result.getData().getStringExtra("lessonSchId"));

                            /**
                             * 1) 예약취소 완료 시 LessonSchId를 넘겨 준다.
                             * 2) evenets에서 해당 ID를 가진 데이터의 내용을 변경 한다
                             * 3) 다시 set해준다.
                             * */
                            String lessonSchId = result.getData().getStringExtra("lessonSchId");

                            for(int i = 0; i < events.size(); i++){
                                Event e = (Event) events.get(i);
                                // 변경해야할 event라면
                                if(e.getLessonSchId().equals(lessonSchId)){
                                    e.setmName(memberInfo.getUserName() + "(취소대기)");
                                    events.set(i,e);
                                    //events.remove(i);
                                    break;
                                }
                            }

                            /**
                             * lesson데이터 변경한다 => 취소사유는 레슨내역화면에서 확인한다. 결국 확인해야할 건 레슨상태이기 때문에 취소여부만 업데이트 해준다.
                             * 1. 취소여부: M
                             * */
                            for(int i = 0; i < lessonSchList.size(); i++){
                                LessonSchInfo tempInfo = lessonSchList.get(i);
                                if(tempInfo.getLessonSchId().equals(lessonSchId)){
                                    tempInfo.setCancelYn("M");
                                    lesson = tempInfo;
                                    lessonSchList.set(i, tempInfo);
                                    break;
                                }
                            }

                            isCanceled = true;

                            dayView.setEvents(events);
                        }
                    }
                });
        super.onCreate(savedInstanceState);

        // 해당 액티비티에서 사용할 xml파일에대한 클래스가 자동으로 생성되고 inflate함수를 이용해서 인스턴스를 생성한다ㅣ.
        binding = ActivityTrainerDayViewBinding.inflate(getLayoutInflater());

        // 기존에는 layout이 들어갔는데 대신 getRoot를 넣어준다.
        setContentView(binding.getRoot());

        initiate();
    }

    public void initiate(){
        // 오늘 날짜(yyyy년 mm월 dd일)
        String todayDate = getIntent().getStringExtra("todayDate");
        // 오늘 날짜(yyyyMMdd)
        String todayDateForServer = getIntent().getStringExtra("todayDateForServer");
        // 트레이너 id(USER_ID)
        String trainerId = getIntent().getStringExtra("trainerId");
        // 일반회원 id(USER_ID)
        String memberId = getIntent().getStringExtra("memberId");
        // 일반회원 id(loginId)
        memberLoginId = getIntent().getStringExtra("memberLoginId");
        // 일반회원 정보
        memberInfo = (FriendInfoDto) getIntent().getSerializableExtra("memberInfo");

        Log.d("트레이너 id: ", trainerId);
        Log.d("memberId: ", memberId);
        Log.d("memberLoginId id: ", memberLoginId);

        dayView = binding.calendar;
        dayView.setLimitTime(0, 23);

        // 오늘 날짜 셋팅
        binding.todayDate.setText(todayDate);

        // 예약하기 버튼 클릭
        binding.btnReserveLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DayViewActivity.this, LessonReserveActivity.class);

                /**
                 * 에약화면으로 데이터 보내기
                 * */
                // 오늘 날짜(yyyy년 mm월 dd일)
                intent.putExtra("todayDate", todayDate);
                // 오늘 날짜(yyyyMMdd)
                intent.putExtra("todayDateForServer", todayDateForServer);
                // 트레이너 id(USER_ID)
                intent.putExtra("trainerId", trainerId);
                // 일반회원 id(USER_ID)
                intent.putExtra("memberId", memberId);
                // 일반회원 정보
                intent.putExtra("memberInfo",memberInfo);

                startActivityResult.launch(intent);
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ((CdvDecorationDefault) (dayView.getDecoration())).setOnEventClickListener(
                new EventView.OnEventClickListener() {
                    @Override
                    public void onEventClick(EventView view, IEvent data) {
                        Log.e("TAG", "onEventClick:" + data.getName());
                    }

                    // 내 레슨 일정 클릭 시 레슨내역 화면으로 이동
                    @Override
                    public void onEventViewClick(View view, EventView eventView, IEvent data) {
                        Log.e("TAG", "onEventViewClick:" + data.getName());
                        if (data instanceof Event) {
                            // 로그인한 회원의 일정이라면
                            if(memberLoginId.equals(((Event) data).getLoginId())){
                                Intent intent = new Intent(DayViewActivity.this, LessonDetailActivity.class);
                                // 레슨 일정 ID 보냄
                                intent.putExtra("lessonSchId", ((Event) data).getLessonSchId());

                                // 액티비티명
                                intent.putExtra("actName","DayViewActivity");

                                // 사용자 타입- 0: 트레이너, 1: 일반회원
                                intent.putExtra("userType", "1");

                                // intent전송
                                startActivityResult2.launch(intent);
                            }
                        }
                    }
                });

        // 레슨일정 데이터 가져오기
        Retrofit retrofit= RetrofitInstance.getRetroClient();
        LessonService service = retrofit.create(LessonService.class);

        // http request 객체 생성
        Call<ArrayList<LessonSchInfo>> call = service.getLessonList(trainerId,todayDateForServer);

        new NetworkCall().execute(call);
    }

    private class NetworkCall extends AsyncTask<Call, Void, String> {
        private retrofit2.Response<ArrayList<LessonSchInfo>> response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPreExecute", "11");
        }

        @Override
        protected String  doInBackground(Call[] params) {
            try {
                retrofit2.Call<ArrayList<LessonSchInfo>> call = params[0];
                response = call.execute();

                if(response.body() != null){
                    lessonSchList = response.body();
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
                events = new ArrayList<>();

                // 레슨일정 화면에 보여주기 위해 세팅
                for(LessonSchInfo lesson : lessonSchList){
                    Event event = makeEvent(lesson);

                    events.add(event);
                }

                dayView.setEvents(events);
            }
        }
    }

    private Event makeEvent(LessonSchInfo lesson){
        int myLessonColor = ContextCompat.getColor(DayViewActivity.this, R.color.light_yellow);
        int otherLessonColor = ContextCompat.getColor(DayViewActivity.this, R.color.light_blue);

        // 시작시간
        Calendar timeStart = Calendar.getInstance();
        timeStart.set(Calendar.HOUR_OF_DAY, Integer.parseInt(lesson.getLessonSrtTime().split(":")[0]));
        timeStart.set(Calendar.MINUTE, Integer.parseInt(lesson.getLessonSrtTime().split(":")[1]));

        // 종료시간
        Calendar timeEnd = (Calendar) timeStart.clone();
        timeEnd.set(Calendar.HOUR_OF_DAY, Integer.parseInt(lesson.getLessonEndTime().split(":")[0]));
        timeEnd.set(Calendar.MINUTE, Integer.parseInt(lesson.getLessonEndTime().split(":")[1]));

        Event event = null;

        // 나의 레슨일정이라면
        if(memberLoginId.equals(lesson.getLoginId())){
            // 레슨상태정보(예약대기, 예약, 출석, 결석)
            String attendanceYn = lesson.getAttendanceYn();
            String attendanceYnName = "";
            String confirmYnName = "";
            String attendanceYnOrConfirmYn = "";

            if((attendanceYn == null || attendanceYn.isEmpty()) && lesson.getCancelYn().equals("N")){
                if("Y".equals(lesson.getConfirmYn())){
                    confirmYnName = "(예약)";
                } else if(lesson.getReservationConfirmYn().equals("M")){
                    confirmYnName = "(예약대기)";
                }
                attendanceYnOrConfirmYn = confirmYnName;
            } else if(!lesson.getCancelYn().equals("N")){
                if(lesson.getCancelYn().equals("M")){
                    attendanceYnOrConfirmYn = "(취소대기)";
                } else if(lesson.getCancelYn().equals("Y")){
                    attendanceYnOrConfirmYn = "(예약취소)";
                }
            } else if(lesson.getReservationConfirmYn().equals("N")){
                attendanceYnOrConfirmYn = "(예약취소)";
            } else{
                attendanceYnName = lesson.getAttendanceYnName();
                attendanceYnOrConfirmYn = attendanceYnName;
            }

            event = new Event(1, timeStart, timeEnd, lesson.getUserName()+attendanceYnOrConfirmYn, "", myLessonColor,lesson.getLessonSchId(), lesson.getLoginId());
        } else{ // 나의 레슨일정이 아니라면
            event = new Event(1, timeStart, timeEnd, "", "", otherLessonColor,lesson.getLessonSchId(),"");
        }
        return event;
    }

    @Override
    public void onBackPressed() {
        //  예약했거나 취소했다면
        if(isReserved || isCanceled){
            Intent intent = null;

            intent = new Intent(this, MemberHomeActivity.class);

            if(isReserved){
                intent.putExtra("reserveOrCancel","reserve");
            } else if(isCanceled){
                intent.putExtra("reserveOrCancel","cancel");
            }
            // 테스트 데이터
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            intent.putExtra("lessonSchInfo", lesson);

            Log.d("DayViewAct에서 취소여부 잘나오나?", lesson.getCancelYn());

            setResult(Activity.RESULT_OK, intent);
            finish();
        }

        super.onBackPressed();
    }
}