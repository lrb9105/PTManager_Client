package com.teamnova.ptmanager.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.desai.vatsal.mydynamiccalendar.EventModel;
import com.desai.vatsal.mydynamiccalendar.GetEventListListener;
import com.desai.vatsal.mydynamiccalendar.MyDynamicCalendar;
import com.desai.vatsal.mydynamiccalendar.OnDateClickListener;
import com.desai.vatsal.mydynamiccalendar.OnEventClickListener;
import com.desai.vatsal.mydynamiccalendar.OnWeekDayViewClickListener;
import com.teamnova.ptmanager.MainActivity;
import com.teamnova.ptmanager.R;

import java.util.ArrayList;
import java.util.Date;

public class WeekViewActivity5 extends AppCompatActivity {

    private Toolbar toolbar;
    private MyDynamicCalendar myCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view5);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        myCalendar = (MyDynamicCalendar) findViewById(R.id.myCalendar);

        setSupportActionBar(toolbar);

        // 월별 일정을 보여주는 메소드
        myCalendar.showMonthView();

        myCalendar.setCalendarBackgroundColor("#eeeeee");
//        myCalendar.setCalendarBackgroundColor(R.color.gray);

        myCalendar.setHeaderBackgroundColor("#454265");
//        myCalendar.setHeaderBackgroundColor(R.color.black);

        myCalendar.setHeaderTextColor("#ffffff");
//        myCalendar.setHeaderTextColor(R.color.white);

        myCalendar.setNextPreviousIndicatorColor("#245675");
//        myCalendar.setNextPreviousIndicatorColor(R.color.black);

        myCalendar.setWeekDayLayoutBackgroundColor("#965471");
//        myCalendar.setWeekDayLayoutBackgroundColor(R.color.black);

        myCalendar.setWeekDayLayoutTextColor("#246245");
//        myCalendar.setWeekDayLayoutTextColor(R.color.black);

//        myCalendar.isSaturdayOff(true, "#ffffff", "#ff0000");
//        myCalendar.isSaturdayOff(true, R.color.white, R.color.red);

//        myCalendar.isSundayOff(true, "#658745", "#254632");
//        myCalendar.isSundayOff(true, R.color.white, R.color.red);

        myCalendar.setExtraDatesOfMonthBackgroundColor("#324568");
//        myCalendar.setExtraDatesOfMonthBackgroundColor(R.color.black);

        myCalendar.setExtraDatesOfMonthTextColor("#756325");
//        myCalendar.setExtraDatesOfMonthTextColor(R.color.black);

//        myCalendar.setDatesOfMonthBackgroundColor(R.drawable.event_view);
        myCalendar.setDatesOfMonthBackgroundColor("#145687");
//        myCalendar.setDatesOfMonthBackgroundColor(R.color.black);

        myCalendar.setDatesOfMonthTextColor("#745632");
//        myCalendar.setDatesOfMonthTextColor(R.color.black);

//        myCalendar.setCurrentDateBackgroundColor("#123412");
//        myCalendar.setCurrentDateBackgroundColor(R.color.black);

        myCalendar.setCurrentDateTextColor("#00e600");
//        myCalendar.setCurrentDateTextColor(R.color.black);

        myCalendar.setEventCellBackgroundColor("#852365");
//        myCalendar.setEventCellBackgroundColor(R.color.black);

        myCalendar.setEventCellTextColor("#425684");
//        myCalendar.setEventCellTextColor(R.color.black);



        /*myCalendar.getEventList(new GetEventListListener() {
            @Override
            public void eventList(ArrayList<EventModel> eventList) {

                Log.e("tag", "eventList.size():-" + eventList.size());
                for (int i = 0; i < eventList.size(); i++) {
                    Log.e("tag", "eventList.getStrName:-" + eventList.get(i).getStrName());
                }

            }
        });*/

//        myCalendar.updateEvent(0, "5-10-2016", "8:00", "8:15", "Today Event 111111");

//        myCalendar.deleteEvent(2);

//        myCalendar.deleteAllEvent();

        myCalendar.setBelowMonthEventTextColor("#425684");
//        myCalendar.setBelowMonthEventTextColor(R.color.black);

        myCalendar.setBelowMonthEventDividerColor("#635478");
//        myCalendar.setBelowMonthEventDividerColor(R.color.black);

        myCalendar.setHolidayCellBackgroundColor("#654248");
//        myCalendar.setHolidayCellBackgroundColor(R.color.black);

        myCalendar.setHolidayCellTextColor("#d590bb");
//        myCalendar.setHolidayCellTextColor(R.color.black);

        myCalendar.setHolidayCellClickable(false);
        myCalendar.addHoliday("2-11-2016");
        myCalendar.addHoliday("8-11-2016");
        myCalendar.addHoliday("12-11-2016");
        myCalendar.addHoliday("13-11-2016");
        myCalendar.addHoliday("8-10-2016");
        myCalendar.addHoliday("10-12-2016");


//        myCalendar.setCalendarDate(5, 10, 2016);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_month:
                showMonthView();
                return true;
            case R.id.action_month_with_below_events:
                showMonthViewWithBelowEvents();
                return true;
            case R.id.action_week:
                showWeekView();
                return true;
            case R.id.action_day:
                showDayView();
                return true;
            case R.id.action_agenda:
                showAgendaView();
                return true;
            case R.id.action_today:
                myCalendar.goToCurrentDate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void showMonthView() {

        myCalendar.showMonthView();

        // 월별 일정에서 날짜 클릭 시 연결할 리스너
        myCalendar.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onClick(Date date) {
                Log.d("date", String.valueOf(date));
                Toast.makeText(WeekViewActivity5.this, "11111", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onLongClick(Date date) {
                Log.d("date", String.valueOf(date));
                Toast.makeText(WeekViewActivity5.this, "2222", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void showMonthViewWithBelowEvents() {

        myCalendar.showMonthViewWithBelowEvents();

        myCalendar.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onClick(Date date) {
                Log.e("date", String.valueOf(date));
            }

            @Override
            public void onLongClick(Date date) {
                Log.e("date", String.valueOf(date));
            }
        });

    }

    private void showWeekView() {

        myCalendar.showWeekView();

        myCalendar.addEvent("6-14-2021", "8:00", "9:00", "Today Event 1");
        myCalendar.addEvent("6-14-2021", "9:00", "10:00", "Today Event 2");
        myCalendar.addEvent("6-14-2021", "10:00", "11:00", "Today Event 3");
        myCalendar.addEvent("6-14-2021", "11:00", "12:00", "Today Event 4");
        myCalendar.addEvent("6-14-2021", "12:00", "13:00", "Today Event 5");
        myCalendar.addEvent("6-14-2021", "13:00", "14:00", "Today Event 6");

        myCalendar.setOnEventClickListener(new OnEventClickListener() {
            @Override
            public void onClick() {
                Log.e("showWeekView","from setOnEventClickListener onClick");
            }

            @Override
            public void onLongClick() {
                Log.e("showWeekView","from setOnEventClickListener onLongClick");

            }
        });

        myCalendar.setOnWeekDayViewClickListener(new OnWeekDayViewClickListener() {
            @Override
            public void onClick(String date, String time) {
                Log.e("showWeekView", "from setOnWeekDayViewClickListener onClick");
                Log.e("tag", "date:-" + date + " time:-" + time);

            }

            @Override
            public void onLongClick(String date, String time) {
                Log.e("showWeekView", "from setOnWeekDayViewClickListener onLongClick");
                Log.e("tag", "date:-" + date + " time:-" + time);

            }
        });


    }

    private void showDayView() {
        // 날짜 설정 가능
        myCalendar.setCalendarDate(11,5,2021);
        myCalendar.showDayView();

        myCalendar.addEvent("6-14-2021", "8:00", "9:00", "Today Event 1");
        myCalendar.addEvent("6-14-2021", "9:00", "10:00", "Today Event 2");
        myCalendar.addEvent("6-14-2021", "10:00", "11:00", "Today Event 3");
        myCalendar.addEvent("6-14-2021", "11:00", "12:00", "Today Event 4");
        myCalendar.addEvent("6-14-2021", "12:00", "13:00", "Today Event 5");
        myCalendar.addEvent("6-14-2021", "13:00", "14:00", "Today Event 6");

        myCalendar.setOnEventClickListener(new OnEventClickListener() {
            @Override
            public void onClick() {
                Log.e("showDayView", "from setOnEventClickListener onClick");
                Toast.makeText(WeekViewActivity5.this, "11111", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick() {
                Log.e("showDayView", "from setOnEventClickListener onLongClick");

            }
        });

        myCalendar.setOnWeekDayViewClickListener(new OnWeekDayViewClickListener() {
            @Override
            public void onClick(String date, String time) {
                Log.e("showDayView", "from setOnWeekDayViewClickListener onClick");
                Log.e("tag", "date:-" + date + " time:-" + time);
            }

            @Override
            public void onLongClick(String date, String time) {
                Log.e("showDayView", "from setOnWeekDayViewClickListener onLongClick");
                Log.e("tag", "date:-" + date + " time:-" + time);
            }
        });

    }

    private void showAgendaView() {

        myCalendar.showAgendaView();

        myCalendar.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onClick(Date date) {
                Log.e("date", String.valueOf(date));
            }

            @Override
            public void onLongClick(Date date) {
                Log.e("date", String.valueOf(date));
            }
        });

    }
}
