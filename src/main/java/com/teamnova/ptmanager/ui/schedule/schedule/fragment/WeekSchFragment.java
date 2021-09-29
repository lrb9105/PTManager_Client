package com.teamnova.ptmanager.ui.schedule.schedule.fragment;

import android.content.Intent;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.teamnova.mylibrary.DateTimeInterpreter;
import com.teamnova.mylibrary.WeekDayView;
import com.teamnova.mylibrary.WeekHeaderView;
import com.teamnova.mylibrary.WeekView;
import com.teamnova.mylibrary.WeekViewEvent;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.model.lecture.LectureInfoDto;
import com.teamnova.ptmanager.model.lesson.LessonSchInfo;
import com.teamnova.ptmanager.model.userInfo.FriendInfoDto;
import com.teamnova.ptmanager.network.RetrofitInstance;
import com.teamnova.ptmanager.network.schedule.lesson.LessonService;
import com.teamnova.ptmanager.test.WeekViewActivity6;
import com.teamnova.ptmanager.ui.home.trainer.TrainerHomeActivity;
import com.teamnova.ptmanager.ui.home.trainer.fragment.TrainerScheduleFragment;
import com.teamnova.ptmanager.ui.schedule.lesson.LessonDetailActivity;
import com.teamnova.ptmanager.viewmodel.schedule.lesson.LessonViewModel;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeekSchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeekSchFragment extends Fragment implements WeekDayView.MonthChangeListener, WeekDayView.EventClickListener, WeekDayView.EventLongPressListener,WeekDayView.ScrollListener, WeekView.EmptyViewClickListener,WeekView.EmptyViewLongPressListener{
    //view
    private WeekDayView mWeekView;
    private WeekHeaderView mWeekHeaderView;
    private TextView mTv_date;
    private LessonViewModel lessonViewModel;
    private List<WeekViewEvent> events;
    private long idOfLesson;

    // Retrofit 통신의 결과를 전달하는 핸들러
    private Handler resultHandler;

    // 레슨정보를 가져온 년, 월을 저장하기 위한 map
    // map을 사용하는 이유는 map은 중복저장이 되지 않고 key를 사용해서 이지 정보를 가져온 년,월인지 확인할 수 있기 때문이다.
    private HashMap<String,String> usedMonthMap;

    //
    private HashMap<String,List<WeekViewEvent>> usedEventsMap;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // 일정을 클릭했는지 여부
    private boolean isClicked = false;

    public WeekSchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WeekSchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeekSchFragment newInstance() {
        WeekSchFragment fragment = new WeekSchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week_sch, container, false);

        // 부모 액티비티의 라이프사이클에 따르게 하려고 getActivity()를 해줬는데 맞는지 모르겠다 => 찾아보기
        lessonViewModel = new ViewModelProvider(getActivity()).get(LessonViewModel.class);

        // 이미 레슨정보를 가져온 년, 월을 따로 저장하기 위한 HashMap
        usedMonthMap = new HashMap<>();
        // 이미 레슨정보를 가져온 년, 월의 리스트를 저장하기 위한 HashMap
        usedEventsMap = new HashMap<>();
        // Populate the week view with some events.
        //events = new ArrayList<>();

        // Inflate the layout for this fragment
        assignViews(view);

        /**
         * 월별 일정에서 주간일정으로 이동한 것이라면
         * 1. 이동할 날짜로 주간 일정 이동
         * */
        if(TrainerScheduleFragment.moveDate != null){
            Calendar moveDate = TrainerScheduleFragment.moveDate;

            //mWeekHeaderView.goToNextWeek();

            mWeekHeaderView.setmFirstVisibleDay(Calendar.getInstance());
            mWeekHeaderView.setmLastVisibleDay(Calendar.getInstance());

            mWeekHeaderView.goToNextWeek();

            //mWeekHeaderView.setSelectedDay(moveDate);

            TrainerScheduleFragment.moveDate = null;
        }

        return view;
    }

    private void assignViews(View view) {
        mWeekView = view.findViewById(R.id.week_day_view);
        mWeekHeaderView= view.findViewById(R.id.week_header_view);
        mTv_date = view.findViewById(R.id.tv_date);

        //init WeekView
        mWeekView.setMonthChangeListener(this);
        mWeekView.setEventLongPressListener(this);
        mWeekView.setOnEventClickListener(this);
        mWeekView.setScrollListener(this);

        // 날짜 선택 시 호출되는 함수
        mWeekHeaderView.setDateSelectedChangeListener(new WeekHeaderView.DateSelectedChangeListener() {
            @Override
            public void onDateSelectedChange(Calendar oldSelectedDay, Calendar newSelectedDay) {
                mWeekView.goToDate(newSelectedDay);
                mTv_date.setText(newSelectedDay.get(Calendar.YEAR)+"년"+(newSelectedDay.get(Calendar.MONTH)+1)+"월");
            }
        });

        // 날짜 스크롤 시 호출되는 함수
        mWeekHeaderView.setScrollListener(new WeekHeaderView.ScrollListener() {
            @Override
            public void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay) {
                Log.d("언제 호출되는가?","11");

                mWeekView.goToDate(mWeekHeaderView.getSelectedDay());
                mTv_date.setText(newFirstVisibleDay.get(Calendar.YEAR)+"년"+(newFirstVisibleDay.get(Calendar.MONTH)+1)+"월");
            }
        });

        setupDateTimeInterpreter(false);

    }


    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     *
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        final String[] weekLabels={"일","월","화","수","목","금","토"};
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat("d", Locale.getDefault());
                return format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return String.format("%02d:00", hour);

            }

            @Override
            public String interpretWeek(int date) {
                if(date>7||date<1){
                    return null;
                }
                return weekLabels[date-1];
            }
        });
    }

    // 일 변경 시마다 실행된다.
    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        //List<WeekViewEvent> events;

        newMonth = newMonth-1;

        Log.d("onMonthChange호출 시점","111");
        Log.d("onMonthChange호출 시점","" + (events == null));

        // 트레이너 로그인 id
        String loginId = TrainerHomeActivity.staticLoginUserInfo.getLoginId();
        // 레슨일정 가져 올 년,월
        String yearMonth = newYear + "" + (newMonth <10 ? "0"+newMonth : newMonth);

        // 아직 가져오지 않은 년,월의 레슨정보라면 가져오기
        if(usedMonthMap.get(yearMonth) == null) {
            events = new ArrayList<>();

            //lessonViewModel.getLessonList(resultHandler, loginId, yearMonth);

            Retrofit retrofit= RetrofitInstance.getRetroClient();
            LessonService service = retrofit.create(LessonService.class);

            // http request 객체 생성
            Call<ArrayList<LessonSchInfo>> call = service.getLessonList(loginId,yearMonth);

            System.out.println(loginId);
            System.out.println(yearMonth);

            // 서버에서 데이터를 가져오는 동기 함수의 쓰레드
            SyncRetrofitThread t = new SyncRetrofitThread(yearMonth , call);
            t.start();

            try {
                usedMonthMap.put(yearMonth, yearMonth);
                // 쓰레드에서 데이터를 가져올 때 main쓰레드는 중지를 시켜야 하므로 join()사용
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return events;
        } else { // 이미 가져온 적 있는 년월 데이터일 경우 저장된 리스트 출력
            return usedEventsMap.get(yearMonth);
        }
    }

    private String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH) + 1, time.get(Calendar.DAY_OF_MONTH));
    }

    // 레슨 일정 클릭 시 레슨내역 화면으로 이동
    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        isClicked = true;

        // 액티비티 이동할 INTENT
        Intent intent = new Intent(getActivity(), LessonDetailActivity.class);

        // 사용자 타입- 0: 트레이너, 1: 일반회원
        intent.putExtra("userType", "0");

        // 레슨 일정 ID 보냄
        intent.putExtra("lessonSchId", event.getLessonSchId());

        // intent전송
        startActivity(intent);
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(getActivity(), "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onEmptyViewClicked(Calendar time) {
        Toast.makeText(getActivity(), "Empty View clicked " + time.get(Calendar.YEAR) + "/" + time.get(Calendar.MONTH) + "/" + time.get(Calendar.DAY_OF_MONTH), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        Toast.makeText(getActivity(), "Empty View long  clicked " + time.get(Calendar.YEAR) + "/" + time.get(Calendar.MONTH) + "/" + time.get(Calendar.DAY_OF_MONTH), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay) {

    }

    @Override
    public void onSelectedDaeChange(Calendar selectedDate) {
        mWeekHeaderView.setSelectedDay(selectedDate);
        mTv_date.setText(selectedDate.get(Calendar.YEAR)+"년"+(selectedDate.get(Calendar.MONTH)+1)+"월");
    }

    // 서버에서 레슨일정 데이터를 가져와서 가공하는 쓰레드
    private class SyncRetrofitThread extends Thread {
        private Call<ArrayList<LessonSchInfo>> call;
        private String yearMonth;

        public SyncRetrofitThread(String yearMonth, Call<ArrayList<LessonSchInfo>> call){
            this.yearMonth = yearMonth;
            this.call = call;
        }

        @Override
        public void run() {
            try {
                Response response = call.execute();
                ArrayList<LessonSchInfo> lessonList = (ArrayList<LessonSchInfo>)response.body();
                int i = 0;

                // 데이터가 존재한다면!
                if(!lessonList.get(0).isNoData()){
                    for(LessonSchInfo lesson : lessonList) {
                        Calendar startTime = Calendar.getInstance();
                        startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(lesson.getLessonSrtTime().split(":")[0]));
                        startTime.set(Calendar.MINUTE, Integer.parseInt(lesson.getLessonSrtTime().split(":")[1]));
                        startTime.set(Calendar.MONTH, Integer.parseInt(lesson.getLessonDate().substring(4, 6)) - 1);
                        startTime.set(Calendar.YEAR, Integer.parseInt(lesson.getLessonDate().substring(0, 4)));
                        startTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(lesson.getLessonDate().substring(6, 8)));

                        Calendar endTime = (Calendar) startTime.clone();
                        endTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(lesson.getLessonEndTime().split(":")[0]));
                        endTime.set(Calendar.MINUTE, Integer.parseInt(lesson.getLessonEndTime().split(":")[1]));

                        Log.d("레슨정보" + idOfLesson, lesson.getLessonSchId() + ":" + lesson.getUserName() + "-" + startTime.getTime() + "-" + endTime.getTime());

                        // 레슨상태정보(예약대기, 예약, 출석, 결석)
                        String attendanceYn = lesson.getAttendanceYn();
                        String attendanceYnName = "";
                        String confirmYnName = "";
                        String attendanceYnOrConfirmYn = "";

                        if((attendanceYn == null || attendanceYn.isEmpty()) && lesson.getCancelYn().equals("N")){
                            Log.d("레슨상태 확인: ", lesson.getCancelYn() + " : " + lesson.getConfirmYn());

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
                        } else if(lesson.getReservationConfirmYn() != null && lesson.getReservationConfirmYn().equals("N")){
                            attendanceYnOrConfirmYn = "(예약취소)";
                        } else{
                            attendanceYnName = lesson.getAttendanceYnName();
                            attendanceYnOrConfirmYn = "(" + attendanceYnName + ")";
                        }


                        WeekViewEvent event = new WeekViewEvent(idOfLesson, lesson.getUserName() + attendanceYnOrConfirmYn + "\n\n" + lesson.getMemo(), startTime, endTime, lesson.getLessonSchId());

                        Log.d("동일한 일정이 몇번 호출되는가 확인하고 싶음:", lesson.getUserName() + "-" + idOfLesson);

                        // 색 변경
                        Log.d("시작시간 어떻게 나오나?", event.getStartTime().toString());

                        int colorNum = i%2;

                        switch (colorNum) {
                            case 0:
                                event.setColor(getResources().getColor(R.color.event_color_01));
                                break;
                            case 1:
                                event.setColor(getResources().getColor(R.color.event_color_02));
                                break;
                        }

                        events.add(event);

                        idOfLesson++;
                        i++;
                    }

                    usedEventsMap.put(yearMonth, events);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

/*
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
                    listOfLessonList.add(response.body());
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
                //events.clear();

                Log.d("잘가저오는가? " +  (listOfLessonList.size() - 1), listOfLessonList.get(listOfLessonList.size() - 1).get(0).getLessonDate());

                // 레슨리스트
                ArrayList<LessonSchInfo> lessonList = listOfLessonList.get(listOfLessonList.size() - 1);

                // 레슨일정 화면에 보여주기 위해 세팅
                for(LessonSchInfo lesson : lessonList){
                    Calendar startTime = Calendar.getInstance();
                    startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(lesson.getLessonSrtTime().split(":")[0]));
                    startTime.set(Calendar.MINUTE, Integer.parseInt(lesson.getLessonSrtTime().split(":")[1]));
                    startTime.set(Calendar.MONTH, Integer.parseInt(lesson.getLessonDate().substring(4,6)) - 1);
                    startTime.set(Calendar.YEAR, Integer.parseInt(lesson.getLessonDate().substring(0,4)));
                    startTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(lesson.getLessonDate().substring(6,8)));

                    Calendar endTime = (Calendar) startTime.clone();
                    endTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(lesson.getLessonEndTime().split(":")[0]));
                    endTime.set(Calendar.MINUTE, Integer.parseInt(lesson.getLessonEndTime().split(":")[1]));

                    Log.d("레슨정보"+idOfLesson,lesson.getLessonSchId() + ":" + lesson.getUserName() + "-" + startTime.getTime() + "-" + endTime.getTime());

                    WeekViewEvent event = new WeekViewEvent(idOfLesson, lesson.getUserName() + "\n" + lesson.getMemo(), startTime, endTime, lesson.getLessonSchId());

                    Log.d("동일한 일정이 몇번 호출되는가 확인하고 싶음:", lesson.getUserName() + "-" + idOfLesson);

                    // 색 변경
                    Random random = new Random();
                    int colorNum = random.nextInt(6) + 1;
                    switch (colorNum){
                        case 1:
                            event.setColor(getResources().getColor(R.color.event_color_01));
                            break;
                        case 2:
                            event.setColor(getResources().getColor(R.color.event_color_02));
                            break;
                        case 3:
                            event.setColor(getResources().getColor(R.color.event_color_03));
                            break;
                        case 4:
                            event.setColor(getResources().getColor(R.color.event_color_04));
                            break;
                        case 5:
                            event.setColor(getResources().getColor(R.color.event_color_05));
                            break;
                        default:
                            event.setColor(getResources().getColor(R.color.event_color_06));
                            break;
                    }

                    events.add(event);

                    idOfLesson ++;
                }

                Log.d("onPostExecute 현재시간:", "" + new Date(System.currentTimeMillis()));
            }
        }
    }*/

    //events.addAll(mNewEvent);

                /*
                int newMonth = 6;
                    int newYear = 2021;

                    Calendar startTime = Calendar.getInstance();

                    startTime.set(Calendar.HOUR_OF_DAY, 3);
                    startTime.set(Calendar.MINUTE, 0);
                    startTime.set(Calendar.MONTH, newMonth - 1);
                    startTime.set(Calendar.YEAR, newYear);
                    Calendar endTime = (Calendar) startTime.clone();
                    endTime.add(Calendar.HOUR, 1);
                    endTime.set(Calendar.MONTH, newMonth - 1);
                    WeekViewEvent event = new WeekViewEvent(1, "This is a Event!!", startTime, endTime);
                    event.setColor(getResources().getColor(R.color.event_color_01));
                    events.add(event);

                    startTime = Calendar.getInstance();
                    startTime.set(Calendar.HOUR_OF_DAY, 3);
                    startTime.set(Calendar.MINUTE, 30);
                    startTime.set(Calendar.MONTH, newMonth - 1);
                    startTime.set(Calendar.YEAR, newYear);
                    endTime = (Calendar) startTime.clone();
                    endTime.set(Calendar.HOUR_OF_DAY, 4);
                    endTime.set(Calendar.MINUTE, 30);
                    endTime.set(Calendar.MONTH, newMonth - 1);
                    event = new WeekViewEvent(10, getEventTitle(startTime), startTime, endTime);
                    event.setColor(getResources().getColor(R.color.event_color_02));
                    events.add(event);

                    startTime = Calendar.getInstance();
                    startTime.set(Calendar.HOUR_OF_DAY, 4);
                    startTime.set(Calendar.MINUTE, 20);
                    startTime.set(Calendar.MONTH, newMonth - 1);
                    startTime.set(Calendar.YEAR, newYear);
                    endTime = (Calendar) startTime.clone();
                    endTime.set(Calendar.HOUR_OF_DAY, 5);
                    endTime.set(Calendar.MINUTE, 0);
                    event = new WeekViewEvent(10, getEventTitle(startTime), startTime, endTime);
                    event.setColor(getResources().getColor(R.color.event_color_03));
                    events.add(event);



                    startTime = Calendar.getInstance();
                    startTime.set(Calendar.HOUR_OF_DAY, 5);
                    startTime.set(Calendar.MINUTE, 30);
                    startTime.set(Calendar.MONTH, newMonth - 1);
                    startTime.set(Calendar.YEAR, newYear);
                    endTime = (Calendar) startTime.clone();
                    endTime.add(Calendar.HOUR_OF_DAY, 2);
                    endTime.set(Calendar.MONTH, newMonth - 1);
                    event = new WeekViewEvent(2, "dddd", startTime, endTime);
                    event.setColor(getResources().getColor(R.color.event_color_01));
                    events.add(event);

                    startTime = Calendar.getInstance();
                    startTime.set(Calendar.HOUR_OF_DAY, 5);
                    startTime.set(Calendar.MINUTE, 0);
                    startTime.set(Calendar.MONTH, newMonth - 1);
                    startTime.set(Calendar.YEAR, newYear);
                    startTime.add(Calendar.DATE, 1);
                    endTime = (Calendar) startTime.clone();
                    endTime.add(Calendar.HOUR_OF_DAY, 3);
                    endTime.set(Calendar.MONTH, newMonth - 1);
                    event = new WeekViewEvent(3, getEventTitle(startTime), startTime, endTime);
                    event.setColor(getResources().getColor(R.color.event_color_03));
                    events.add(event);

                    startTime = Calendar.getInstance();
                    startTime.set(Calendar.DAY_OF_MONTH, 15);
                    startTime.set(Calendar.HOUR_OF_DAY, 3);
                    startTime.set(Calendar.MINUTE, 0);
                    startTime.set(Calendar.MONTH, newMonth - 1);
                    startTime.set(Calendar.YEAR, newYear);
                    endTime = (Calendar) startTime.clone();
                    endTime.add(Calendar.HOUR_OF_DAY, 3);
                    event = new WeekViewEvent(4, getEventTitle(startTime), startTime, endTime);
                    event.setColor(getResources().getColor(R.color.event_color_04));
                    events.add(event);

                    startTime = Calendar.getInstance();
                    startTime.set(Calendar.DAY_OF_MONTH, 1);
                    startTime.set(Calendar.HOUR_OF_DAY, 3);
                    startTime.set(Calendar.MINUTE, 0);
                    startTime.set(Calendar.MONTH, newMonth - 1);
                    startTime.set(Calendar.YEAR, newYear);
                    endTime = (Calendar) startTime.clone();
                    endTime.add(Calendar.HOUR_OF_DAY, 3);
                    event = new WeekViewEvent(5, getEventTitle(startTime), startTime, endTime);
                    event.setColor(getResources().getColor(R.color.event_color_01));
                    events.add(event);

                    startTime = Calendar.getInstance();
                    startTime.set(Calendar.DAY_OF_MONTH, startTime.getActualMaximum(Calendar.DAY_OF_MONTH));
                    startTime.set(Calendar.HOUR_OF_DAY, 15);
                    startTime.set(Calendar.MINUTE, 0);
                    startTime.set(Calendar.MONTH, newMonth - 1);
                    startTime.set(Calendar.YEAR, newYear);
                    endTime = (Calendar) startTime.clone();
                    endTime.add(Calendar.HOUR_OF_DAY, 3);
                    event = new WeekViewEvent(5, getEventTitle(startTime), startTime, endTime);
                    event.setColor(getResources().getColor(R.color.event_color_02));
                    events.add(event);
                    */

    public WeekDayView getmWeekView(){
        return this.mWeekView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("onResume", "111");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("onPause", "111");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("onStop", "111");
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}