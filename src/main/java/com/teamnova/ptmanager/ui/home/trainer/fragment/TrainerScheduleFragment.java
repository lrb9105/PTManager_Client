package com.teamnova.ptmanager.ui.home.trainer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.ui.schedule.schedule.fragment.MonthSchFragment;
import com.teamnova.ptmanager.ui.schedule.schedule.fragment.WeekSchFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrainerScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrainerScheduleFragment extends Fragment {
    private Spinner selectSch;
    private WeekSchFragment weekSchFragment;
    private MonthSchFragment monthSchFragment;

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
                    setChildFragment(weekSchFragment, "week_frag");

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
        
        return view;
    }

    private void setChildFragment(Fragment child, String tag) {
        FragmentTransaction childFt = getChildFragmentManager().beginTransaction();

        if (!child.isAdded()) {
            childFt.replace(R.id.sch_container, child, tag);
            childFt.addToBackStack(null);
            childFt.commit();
        }
    }
}