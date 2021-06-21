package com.teamnova.ptmanager.ui.schedule.schedule.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.test.WeekViewActivity7;
import com.teamnova.ptmanager.ui.schedule.schedule.decorator.DotDecorator;
import com.teamnova.ptmanager.ui.schedule.schedule.decorator.SaturdayDecorator;
import com.teamnova.ptmanager.ui.schedule.schedule.decorator.SundayDecorator;
import com.teamnova.ptmanager.ui.schedule.schedule.decorator.TodayDecorator;

import java.util.HashMap;
import java.util.HashSet;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MonthSchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonthSchFragment extends Fragment {
    // 달력객체
    private MaterialCalendarView calendar;

    // HashSet객체의 static변수 - 일정이나 기념일 추가 시 해당 변수에 값추가해주기!!
    public HashSet<CalendarDay> dateSet;


    // 날짜에 해당하는 일정 갯수를 가지고 있는 map
    // key: 날짜
    // value: 날짜에 해당하는 일정 갯수
    private HashMap<String, Integer> dateListMap;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MonthSchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MonthSchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MonthSchFragment newInstance() {
        MonthSchFragment fragment = new MonthSchFragment();
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

        View view = inflater.inflate(R.layout.fragment_month_sch, container, false);

        // Inflate the layout for this fragment
        dateSet = new HashSet<>();

        // 6월 하고싶으면 5월 넣어야 함 즉, 월-1을 해야 원하는 월이 출력됨
        CalendarDay c = CalendarDay.from(2021, 5, 20);
        dateSet.add(c);

        // 날짜를 key값으로 해당일의 일정 갯수를 저장하는 hashmap
        dateListMap = new HashMap<>();

        calendar = view.findViewById(R.id.month_sch);

        calendar.addDecorator(new DotDecorator(Color.RED,dateSet));
        calendar.addDecorator(new TodayDecorator(getActivity()));
        calendar.addDecorator(new SundayDecorator());
        calendar.addDecorator(new SaturdayDecorator());

        // 날짜 변경(날짜 클릭 시) 실행되는 콜백 메소드
        calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Toast.makeText(getActivity(), "" + date.getYear(), Toast.LENGTH_SHORT).show();
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

        return view;
    }
}