package com.teamnova.ptmanager.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.ui.schedule.schedule.decorator.DotDecorator;
import com.teamnova.ptmanager.ui.schedule.schedule.decorator.SaturdayDecorator;
import com.teamnova.ptmanager.ui.schedule.schedule.decorator.SundayDecorator;
import com.teamnova.ptmanager.ui.schedule.schedule.decorator.TodayDecorator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class WeekViewActivity7 extends AppCompatActivity {
    // 달력객체
    private MaterialCalendarView calendar;

    // HashSet객체의 static변수 - 일정이나 기념일 추가 시 해당 변수에 값추가해주기!!
    public HashSet<CalendarDay> dateSet;


    // 날짜에 해당하는 일정 갯수를 가지고 있는 map
    // key: 날짜
    // value: 날짜에 해당하는 일정 갯수
    private HashMap<String, Integer> dateListMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view7);

        dateSet = new HashSet<>();

        // 6월 하고싶으면 5월 넣어야 함 즉, 월-1을 해야 원하는 월이 출력됨
        CalendarDay c = CalendarDay.from(2021, 5, 20);
        dateSet.add(c);

        // 날짜를 key값으로 해당일의 일정 갯수를 저장하는 hashmap
        dateListMap = new HashMap<>();

        calendar = findViewById(R.id.month_sch);

        calendar.addDecorator(new DotDecorator(Color.RED,dateSet));
        calendar.addDecorator(new TodayDecorator(this));
        calendar.addDecorator(new SundayDecorator());
        calendar.addDecorator(new SaturdayDecorator());

        // 날짜 변경(날짜 클릭 시) 실행되는 콜백 메소드
        calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Toast.makeText(WeekViewActivity7.this, "" + date.getYear(), Toast.LENGTH_SHORT).show();
            }
        });

        // 오늘 날짜가 선택되도록
        CalendarDay today = CalendarDay.today();
        calendar.setDateSelected(today,false);

        // 월 변경 시 실행되는 콜백 메소드
        calendar.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

            }
        });
    }
}