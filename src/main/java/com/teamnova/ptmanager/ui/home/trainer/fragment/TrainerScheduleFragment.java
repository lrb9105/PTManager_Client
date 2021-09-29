package com.teamnova.ptmanager.ui.home.trainer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.adapter.lecture.LectureRegisteredMemberListAdapter;
import com.teamnova.ptmanager.model.lesson.LessonInfo;
import com.teamnova.ptmanager.ui.schedule.lecture.LectureRegisterdMemberListActivity;
import com.teamnova.ptmanager.ui.schedule.lesson.LessonRegisterActivity;
import com.teamnova.ptmanager.ui.schedule.reservation.ReservationApprovementActivity;
import com.teamnova.ptmanager.ui.schedule.schedule.DailyScheduleActivity;
import com.teamnova.ptmanager.ui.schedule.schedule.fragment.MonthSchFragment;
import com.teamnova.ptmanager.ui.schedule.schedule.fragment.WeekSchFragment;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrainerScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrainerScheduleFragment extends Fragment implements View.OnClickListener{
    private Spinner selectSch;
    private WeekSchFragment weekSchFragment;
    private MonthSchFragment monthSchFragment;
    private ImageButton btnAddLesson;
    // 예약 승인/거절
    private Button btnReservationApprovement;
    public static Calendar moveDate;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TrainerScheduleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrainerHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrainerScheduleFragment newInstance(String param1, String param2) {
        TrainerScheduleFragment fragment = new TrainerScheduleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
        View view = inflater.inflate(R.layout.fragment_trainer_schedule, container, false);

        weekSchFragment = WeekSchFragment.newInstance();
        monthSchFragment = MonthSchFragment.newInstance();
        btnAddLesson = view.findViewById(R.id.btn_add_lesson);
        btnReservationApprovement = view.findViewById(R.id.btn_reservation_approvement);

        // 일정 선택 스피너
        selectSch = view.findViewById(R.id.select_sch);

        final String[] selectSchArr = new String[]{"주간 일정", "월별 일정"};

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, selectSchArr);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectSch.setAdapter(adapter);

        selectSch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    // 이동할 날짜가 주어졌다면
                    if(moveDate != null){
                        setChildFragment(weekSchFragment, "week_frag");
                        //weekSchFragment.moveToDate(moveDate);
                    } else { // 이동할 날짜가 없다면 현재날짜로 이동
                        setChildFragment(weekSchFragment, "week_frag");
                    }

                } else if(position == 1){
                    setChildFragment(monthSchFragment,"month_frag");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 최초로 주간일정 보여줌
        selectSch.setSelection(0);

        // 스피너 숨기기
        selectSch.setVisibility(View.INVISIBLE);

        // 클릭리스너 등록
        setOnClickListener();

        Log.d("TrainerSchFrag","onCreateView 호출");

        return view;
    }

    public void setOnClickListener(){
        btnAddLesson.setOnClickListener(this);
        btnReservationApprovement.setOnClickListener(this);
    }

    // 자식프래그먼트 변경
    public void setChildFragment(Fragment child, String tag) {
        FragmentTransaction childFt = getChildFragmentManager().beginTransaction();

        if (!child.isAdded()) {
            childFt.replace(R.id.sch_container, child, tag);
            childFt.addToBackStack(null);
            childFt.commit();
        }
    }

    // 주간일정으로 변경
    public void selectWeekSch(Calendar moveDate){
        this.moveDate = moveDate;
        selectSch.setSelection(0);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()) {
            case R.id.btn_add_lesson: // 레슨추가 버튼
                // 레슨추가 액티비티로 이동
                intent = new Intent(getActivity(), LessonRegisterActivity.class);
                intent.putExtra("returnPath", "TrainerHomeActivity");

                startActivity(intent);
                break;
            case R.id.btn_reservation_approvement: // 예약 승인/거절 화면으로 이동
                // 레슨추가 액티비티로 이동
                intent = new Intent(getActivity(), ReservationApprovementActivity.class);
                startActivity(intent);

                // 테스트
                /*intent = new Intent(getActivity(), LectureRegisterdMemberListActivity.class);
                intent.putExtra("lectureId", "1");
                startActivity(intent);*/
                break;
            default:
                break;
        }
    }
}