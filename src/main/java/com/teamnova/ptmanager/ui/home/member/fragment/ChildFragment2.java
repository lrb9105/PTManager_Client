package com.teamnova.ptmanager.ui.home.member.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.adapter.lesson.member.MemberLessonAdapter;
import com.teamnova.ptmanager.model.lesson.LessonSchInfo;
import com.teamnova.ptmanager.util.GetDate;
import com.teamnova.ptmanager.viewmodel.schedule.lesson.LessonViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * 예약일정 리스트
 */
public class ChildFragment2 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LessonViewModel lessonViewModel;

    // 리사이클러뷰
    private MemberLessonAdapter memberLessonAdapter;
    private RecyclerView recyclerviewMemberLessonList2;
    private RecyclerView.LayoutManager layoutManager;

    // 이 프래그먼트에서 관리하는 레슨일정 리스트
    private ArrayList<LessonSchInfo> lessonSchList;

    // LessonDetail(레슨내역)에서 예약취소요청 완료 시 다시 넘어옴
    private ActivityResultLauncher<Intent> startActivityResult;

    public ChildFragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChildFragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static ChildFragment2 newInstance(String param1, String param2) {
        ChildFragment2 fragment = new ChildFragment2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 예약취소 완료 시
        startActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // 예약취소 완료 시 여기로 넘어오는지 확인
                            String beModifiedLessonSchId = result.getData().getStringExtra("lessonSchId");
                            for(int i = 0; i < lessonSchList.size(); i++){
                                LessonSchInfo tempInfo = lessonSchList.get(i);
                                if(tempInfo.getLessonSchId().equals(beModifiedLessonSchId)){
                                    tempInfo.setCancelYn("M");
                                    lessonSchList.set(i, tempInfo);
                                    Log.d("예약취소 저장 잘 됐나?", lessonSchList.get(i).toString());

                                    break;
                                }
                            }
                            lessonViewModel.getLessonSchList().setValue(lessonSchList);

                        }
                    }
                });
        super.onCreate(savedInstanceState);
        lessonViewModel = new ViewModelProvider(requireActivity()).get(LessonViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_child2, container, false);

        lessonViewModel.getLessonSchList().observe(getActivity(), lessonSchList -> {
            if(lessonSchList != null && lessonSchList.size() > 0){
                ((TextView)view.findViewById(R.id.text2)).setText(lessonSchList.get(0).getLessonSchId() + "222");
            }

            Log.d("child2 호출되나?","22");

            ArrayList<LessonSchInfo> reservedLessonList = new ArrayList<>();

            // 예약일정만 보여주기 - 오늘날짜 < 레슨날짜 or 오늘날짜 == 레슨날짜 && 현재시간 < 레슨시작시간
            for(LessonSchInfo lesson : lessonSchList){
                if(isAddedLesson(lesson)){
                    reservedLessonList.add(lesson);
                }
            }

            this.lessonSchList = reservedLessonList;

            // 리사이클러뷰 세팅
            recyclerviewMemberLessonList2 = view.findViewById(R.id.recyclerview_member_lesson_list2);
            layoutManager = new LinearLayoutManager(requireActivity());
            recyclerviewMemberLessonList2.setLayoutManager(layoutManager);

            // 데이터 가져와서 adapter에 넘겨 줌
            memberLessonAdapter = new MemberLessonAdapter(reservedLessonList, requireActivity(), startActivityResult);
            recyclerviewMemberLessonList2.setAdapter(memberLessonAdapter);

            // 데이터가 추가되면 리사이클러뷰에 추가해준다!
            /*if(lessonSchList.size() != memberLessonAdapter.getItemCount()){
                LessonSchInfo newLesson = lessonSchList.get(lessonSchList.size() - 1);
                if(isAddedLesson(newLesson)){
                    memberLessonAdapter.addLesson(newLesson);
                    Log.d("새로운 레슨 추가","11");
                }
            }*/
        });

        return view;
    }

    public boolean isAddedLesson(LessonSchInfo lesson){

        SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyyMMdd");

        // 현재날짜 yyyymmdd
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar c1 = Calendar.getInstance();
        String strToday = sdf.format(c1.getTime());

        Date currentDate = null;
        Date lessonDate = null;

        try {
            currentDate = dateFormat.parse(strToday);
            lessonDate = dateFormat.parse(lesson.getLessonDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 오늘날짜 > 레슨날짜라면
        if(currentDate.compareTo(lessonDate) < 0) {// 오늘날짜 < 레슨날짜라면
            return true;
        } else if(currentDate.compareTo(lessonDate) == 0){// 오늘날짜 == 레슨날짜라면
            // 시간포맷
            SimpleDateFormat timeFormat = new SimpleDateFormat ("hh:mm");

            // 현재시간
            String currentTime = null;
            try {
                currentTime = GetDate.getCurrentTime(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 현재시간 < 레슨시작시간 이라면
            try {
                if(timeFormat.parse(currentTime).compareTo(timeFormat.parse(lesson.getLessonSrtTime())) < 0){
                    return true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}